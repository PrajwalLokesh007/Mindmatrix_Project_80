package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.model.Report
import com.example.myapplication.ui.theme.EcoGradientStart
import com.example.myapplication.ui.theme.SoftBlue
import com.example.myapplication.ui.viewmodel.ReportViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(viewModel: ReportViewModel) {
    val reports by viewModel.reports.collectAsState()
    var selectedReport by remember { mutableStateOf<Report?>(null) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(12.9716, 77.5946), 12f) // Bengaluru
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            reports.forEach { report ->
                Marker(
                    state = MarkerState(position = LatLng(report.latitude, report.longitude)),
                    title = report.wasteType,
                    snippet = report.status,
                    onClick = {
                        selectedReport = report
                        false
                    }
                )
            }
        }

        // Search Bar Overlay
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Search for waste locations...",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {}) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = EcoGradientStart)
                }
            }
        }

        // Floating Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { /* Zoom to current location */ },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SoftBlue,
                shape = CircleShape
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        }

        // Selected Marker Detail Card
        selectedReport?.let { report ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-16).dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = report.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            report.wasteType,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            report.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (report.status == "Pending") Color.Red else EcoGradientStart
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            report.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = { selectedReport = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }
        }
    }
}
