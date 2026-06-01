package com.clawstack.carabaseadmin.ui.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clawstack.carabaseadmin.data.network.TelemetryResponse
import com.clawstack.carabaseadmin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Project Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Self-hosted SQLite Engine", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.fetchStats() }) {
                        Icon(Icons.Default.Refresh, "Refresh", tint = CyberEmerald)
                    }
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.Default.Logout, "Logout", tint = DestructiveRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyberEmerald)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Getting Started Guide (The Blue Card from Web)
                item {
                    GettingStartedCard()
                }

                // Stat Cards Section
                item {
                    val stats = uiState.aggregateStats
                    
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCardMini(
                                label = "Users",
                                value = stats?.totalUsers?.toString() ?: "0",
                                icon = Icons.Default.People,
                                color = Blue400,
                                modifier = Modifier.weight(1f)
                            )
                            StatCardMini(
                                label = "Tables",
                                value = stats?.totalTables?.toString() ?: "0",
                                icon = Icons.Default.TableChart,
                                color = CyberEmerald,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCardMini(
                                label = "Policies",
                                value = stats?.totalPolicies?.toString() ?: "0",
                                icon = Icons.Default.Security,
                                color = Purple400,
                                modifier = Modifier.weight(1f)
                            )
                            StatCardMini(
                                label = "DB Size",
                                value = formatSize(stats?.dbSize ?: 0L),
                                icon = Icons.Default.Storage,
                                color = Amber400,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // System Health Section
                item {
                    SystemHealthCard(uiState.stats)
                }
            }
        }
    }
}

@Composable
fun GettingStartedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0x1A60A5FA)), // Blue 950/20 style
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3360A5FA)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = Blue400, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Getting Started Guide", color = Blue400, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                "Follow three simple steps to build and secure your first application backend.",
                color = TextMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            HorizontalDivider(color = Color(0x1A60A5FA), modifier = Modifier.padding(vertical = 8.dp))
            
            GuideItem(1, "Build the Foundation", "Create your first database table.")
            GuideItem(2, "Lock it Down", "Set up Row Level Security (RLS).")
            GuideItem(3, "Connect your App", "Generate a Lobster Key (API Key).")
        }
    }
}

@Composable
fun GuideItem(num: Int, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0x3360A5FA),
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(num.toString(), color = Blue400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(desc, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        }
    }
}

@Composable
fun StatCardMini(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SlateDarkBase,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = TextMuted, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun SystemHealthCard(stats: TelemetryResponse?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Terminal, null, tint = TextMuted, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("System Health", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            
            HealthRow("Memory (RSS)", formatSize(stats?.memory?.rss ?: 0L), Sky400)
            HealthRow("Uptime", formatUptime(stats?.uptime ?: 0.0), Rose400)
            HealthRow("Engine Status", "OPERATIONAL", CyberEmerald)
        }
    }
}

@Composable
fun HealthRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(value, style = MaterialTheme.typography.bodySmall, color = color, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}

fun formatSize(bytes: Long): String {
    if (bytes == 0L) return "0 B"
    val k = 1024.0
    val sizes = arrayOf("B", "KB", "MB", "GB")
    val i = Math.floor(Math.log(bytes.toDouble()) / Math.log(k)).toInt()
    return String.format("%.2f %s", bytes / Math.pow(k, i.toDouble()), sizes[i])
}

fun formatUptime(seconds: Double): String {
    val totalSeconds = seconds.toLong()
    val days = totalSeconds / (24 * 3600)
    val hours = (totalSeconds % (24 * 3600)) / 3600
    val mins = (totalSeconds % 3600) / 60
    return "${days}d ${hours}h ${mins}m"
}
