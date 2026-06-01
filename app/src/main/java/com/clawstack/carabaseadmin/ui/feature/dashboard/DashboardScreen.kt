package com.clawstack.carabaseadmin.ui.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clawstack.carabaseadmin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Control", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.fetchStats() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Stats",
                            tint = CyberEmerald
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = CyberEmerald
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyberEmerald)
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(uiState.error!!, color = DestructiveRed)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {
                val stats = uiState.aggregateStats
                val telemetry = uiState.stats

                if (stats != null && telemetry != null) {
                    val formatSize = { bytes: Long ->
                        if (bytes == 0L) "0 B" else {
                            val k = 1024.0
                            val sizes = arrayOf("B", "KB", "MB", "GB")
                            val i = Math.floor(Math.log(bytes.toDouble()) / Math.log(k)).toInt()
                            String.format("%.2f %s", bytes / Math.pow(k, i.toDouble()), sizes[i])
                        }
                    }

                    val formatUptime = { seconds: Double ->
                        val totalSeconds = seconds.toLong()
                        val days = totalSeconds / (24 * 3600)
                        val hours = (totalSeconds % (24 * 3600)) / 3600
                        val mins = (totalSeconds % 3600) / 60
                        "${days}d ${hours}h ${mins}m"
                    }

                    val cards = listOf(
                        StatCardData("Total Users", stats.totalUsers.toString(), Icons.Default.People, Blue400),
                        StatCardData("User Tables", stats.totalTables.toString(), Icons.Default.TableChart, CyberEmeraldLight),
                        StatCardData("RLS Policies", stats.totalPolicies.toString(), Icons.Default.Security, Purple400),
                        StatCardData("Database Size", formatSize(stats.dbSize), Icons.Default.Storage, Amber400),
                        StatCardData("Server Uptime", formatUptime(stats.uptime), Icons.Default.TrendingUp, Rose400),
                        StatCardData("Memory (RSS)", formatSize(telemetry.memory.rss), Icons.Default.Memory, Sky400)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(cards) { card ->
                            StatCard(card)
                        }
                    }
                }
            }
        }
    }
}

data class StatCardData(val label: String, val value: String, val icon: ImageVector, val color: Color)

@Composable
fun StatCard(data: StatCardData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(SlateDarkBase, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(data.icon, contentDescription = data.label, tint = data.color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = data.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = data.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
