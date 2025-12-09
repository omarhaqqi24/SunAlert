package com.example.sunalert.ui

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sunalert.HistoryEntity
import com.example.sunalert.HistoryViewModel
import com.example.sunalert.HistoryViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {

    val app = LocalContext.current.applicationContext as Application
    val viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(app)
    )

    val history by viewModel.historyList.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<HistoryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                actions = {
                    if (history.isNotEmpty()) {
                        TextButton(onClick = { viewModel.deleteAll() }) {
                            Text("Clear All", color = Color.Red)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No history available.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(history, key = { it.id }) { item ->
                    HistoryItem(
                        item = item,
                        onDelete = {
                            selectedItem = item
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        if (showDeleteDialog && selectedItem != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete History") },
                text = { Text("Are you sure you want to delete this item?") },
                confirmButton = {
                    TextButton(onClick = {
                        selectedItem?.let { viewModel.deleteHistory(it) }
                        showDeleteDialog = false
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun HistoryItem(
    item: HistoryEntity,
    onDelete: () -> Unit
) {
    val dateFormat = remember {
        SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
    }
    val date = dateFormat.format(Date(item.timestamp))

    val badgeColor = when (item.kategoriRisiko.lowercase()) {
        "low" -> Color(0xFF4CAF50)
        "moderate" -> Color(0xFFFFC107)
        "high" -> Color(0xFFFF9800)
        "very high", "extreme" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.fotoUri,
                contentDescription = "SkyCheck Photo",
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.LightGray, RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("UV ${item.uvIndex}", fontWeight = FontWeight.Bold)
                Text(item.alamat, maxLines = 1, style = MaterialTheme.typography.bodySmall)
                Text(date, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
//                Text("Foto URI: ${item.fotoUri}")

                Spacer(Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.kategoriRisiko,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }
    }
}
