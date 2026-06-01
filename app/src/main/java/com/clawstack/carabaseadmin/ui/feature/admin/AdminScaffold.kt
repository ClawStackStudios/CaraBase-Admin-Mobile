package com.clawstack.carabaseadmin.ui.feature.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.clawstack.carabaseadmin.ui.feature.dashboard.DashboardScreen
import com.clawstack.carabaseadmin.ui.feature.dashboard.DashboardViewModel
import com.clawstack.carabaseadmin.ui.feature.users.UsersScreen
import com.clawstack.carabaseadmin.ui.feature.users.UsersViewModel
import com.clawstack.carabaseadmin.ui.feature.audit.AuditScreen
import com.clawstack.carabaseadmin.ui.feature.audit.AuditViewModel

sealed class AdminBottomNavItem(val route: String, val icon: @Composable () -> Unit, val title: String) {
    object Dashboard : AdminBottomNavItem("dashboard_tab", { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") }, "System")
    object Users : AdminBottomNavItem("users_tab", { Icon(Icons.Default.People, contentDescription = "Users") }, "Users")
    object Audit : AdminBottomNavItem("audit_tab", { Icon(Icons.Default.Security, contentDescription = "Audit") }, "Audit")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    dashboardViewModel: DashboardViewModel,
    usersViewModel: UsersViewModel,
    auditViewModel: AuditViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val items = listOf(
        AdminBottomNavItem.Dashboard,
        AdminBottomNavItem.Users,
        AdminBottomNavItem.Audit
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = item.icon,
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AdminBottomNavItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AdminBottomNavItem.Dashboard.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onLogout = onLogout
                )
            }
            composable(AdminBottomNavItem.Users.route) {
                UsersScreen(viewModel = usersViewModel)
            }
            composable(AdminBottomNavItem.Audit.route) {
                AuditScreen(viewModel = auditViewModel)
            }
        }
    }
}
