package com.clawstack.carabaseadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clawstack.carabaseadmin.data.CaraBaseSystemRepository
import com.clawstack.carabaseadmin.data.security.SecureIdentityVault
import com.clawstack.carabaseadmin.ui.feature.dashboard.DashboardScreen
import com.clawstack.carabaseadmin.ui.feature.dashboard.DashboardViewModel
import com.clawstack.carabaseadmin.ui.feature.dashboard.DashboardViewModelFactory
import com.clawstack.carabaseadmin.ui.feature.gateway.GatewayScreen
import com.clawstack.carabaseadmin.ui.feature.gateway.GatewayViewModel
import com.clawstack.carabaseadmin.ui.feature.gateway.GatewayViewModelFactory
import com.clawstack.carabaseadmin.ui.theme.CaraBaseAdminTheme

import com.clawstack.carabaseadmin.ui.feature.admin.AdminScaffold
import com.clawstack.carabaseadmin.ui.feature.users.UsersViewModel
import com.clawstack.carabaseadmin.ui.feature.users.UsersViewModelFactory
import com.clawstack.carabaseadmin.ui.feature.audit.AuditViewModel
import com.clawstack.carabaseadmin.ui.feature.audit.AuditViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the Secure Vault to guarantee token storage invariants
        val vault = SecureIdentityVault(applicationContext)

        // The core repository that enforces the stateless API invariant
        val repository = CaraBaseSystemRepository(vault)

        setContent {
            CaraBaseAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CaraBaseAppNavigation(repository)
                }
            }
        }
    }
}

@Composable
fun CaraBaseAppNavigation(repository: CaraBaseSystemRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "gateway") {
        
        composable("gateway") {
            val factory = remember { GatewayViewModelFactory(repository) }
            val viewModel: GatewayViewModel = viewModel(factory = factory)
            
            GatewayScreen(
                viewModel = viewModel,
                onNavigateToDashboard = {
                    navController.navigate("dashboard") {
                        popUpTo("gateway") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
            val dashboardFactory = remember { DashboardViewModelFactory(repository) }
            val dashboardViewModel: DashboardViewModel = viewModel(factory = dashboardFactory)
            
            val usersFactory = remember { UsersViewModelFactory(repository) }
            val usersViewModel: UsersViewModel = viewModel(factory = usersFactory)
            
            val auditFactory = remember { AuditViewModelFactory(repository) }
            val auditViewModel: AuditViewModel = viewModel(factory = auditFactory)

            AdminScaffold(
                dashboardViewModel = dashboardViewModel,
                usersViewModel = usersViewModel,
                auditViewModel = auditViewModel,
                onLogout = {
                    navController.navigate("gateway") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}
