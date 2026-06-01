package com.clawstack.carabaseadmin.ui.feature.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clawstack.carabaseadmin.data.network.UserMetadata
import com.clawstack.carabaseadmin.ui.theme.CyberEmerald
import com.clawstack.carabaseadmin.ui.theme.DestructiveRed
import com.clawstack.carabaseadmin.ui.theme.SlateDarkSurface
import com.clawstack.carabaseadmin.ui.theme.TextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(viewModel: UsersViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<UserMetadata?>(null) }
    var deleteConfirmText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Audit", fontWeight = FontWeight.Bold) },
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
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search users...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberEmerald,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyberEmerald)
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.error!!, color = DestructiveRed)
                }
            } else {
                val filteredUsers = uiState.users.filter {
                    it.username.contains(uiState.searchQuery, ignoreCase = true) ||
                    it.uuid.contains(uiState.searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredUsers, key = { it.uuid }) { user ->
                        UserCard(user = user, onDeleteClick = { showDeleteDialog = user })
                    }

                    if (uiState.users.size < uiState.totalUsers && uiState.searchQuery.isEmpty()) {
                        item {
                            Button(
                                onClick = { viewModel.fetchUsers(uiState.users.size) },
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
                                    Text("Load More Inhabitants")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        showDeleteDialog?.let { user ->
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = null
                    deleteConfirmText = ""
                },
                icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = DestructiveRed) },
                title = { Text("Scuttle Inhabitant?") },
                text = {
                    Column {
                        Text("This action is permanent and absolute. Type ${user.username} to confirm.")
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = deleteConfirmText,
                            onValueChange = { deleteConfirmText = it },
                            placeholder = { Text("Username") },
                            singleLine = true,
                            isError = uiState.deleteError != null
                        )
                        if (uiState.deleteError != null) {
                            Text(
                                text = uiState.deleteError!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            viewModel.deleteUser(user.uuid)
                            showDeleteDialog = null
                            deleteConfirmText = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed),
                        enabled = deleteConfirmText == user.username
                    ) {
                        Text("Scuttle")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showDeleteDialog = null
                        deleteConfirmText = ""
                        viewModel.clearDeleteError()
                    }) {
                        Text("Abort")
                    }
                }
            )
        }
    }
}

@Composable
fun UserCard(user: UserMetadata, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SlateDarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.username, style = MaterialTheme.typography.titleMedium, color = CyberEmerald, fontWeight = FontWeight.Bold)
                Text("UUID: ${user.uuid.take(8)}...", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Agent Keys", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(user.active_keys.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Last Active", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(user.last_login ?: "Never", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            IconButton(
                onClick = onDeleteClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = TextMuted)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete User")
            }
        }
    }
}
