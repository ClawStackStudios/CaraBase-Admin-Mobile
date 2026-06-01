package com.clawstack.carabaseadmin.ui.feature.audit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clawstack.carabaseadmin.data.network.AuditLog
import com.clawstack.carabaseadmin.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditScreen(viewModel: AuditViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedLogId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchAuditLogs()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Audit Trail", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = CyberEmerald
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyberEmerald)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.error!!, color = DestructiveRed)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.logs, key = { it.id }) { log ->
                        AuditLogCard(
                            log = log,
                            isExpanded = expandedLogId == log.id,
                            onToggleExpand = {
                                expandedLogId = if (expandedLogId == log.id) null else log.id
                            }
                        )
                    }

                    if (uiState.logs.size < uiState.totalLogs) {
                        item {
                            Button(
                                onClick = { viewModel.fetchAuditLogs(uiState.logs.size) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SlateDarkSurface)
                            ) {
                                if (uiState.isMoreLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = CyberEmerald,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Load More Logs")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditLogCard(
    log: AuditLog,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val isSuccess = log.outcome.equals("success", ignoreCase = true)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggleExpand,
        colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = log.outcome,
                    tint = if (isSuccess) CyberEmerald else DestructiveRed,
                    modifier = Modifier.size(24.dp).padding(top = 2.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = log.action,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = log.event_type.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Actor: ${log.actor ?: "system"}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    Text("Time: ${log.timestamp}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted
                )
            }

            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .background(SlateDarkBase, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "LOG REFERENCE #${log.id} • HOST: ${log.ip_address ?: "127.0.0.1"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 8.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "User-Agent: ${log.user_agent ?: "Unknown"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                    log.details?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberEmeraldLight,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
