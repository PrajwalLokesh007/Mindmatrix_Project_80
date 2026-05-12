package com.example.myapplication.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.myapplication.data.model.Report // FIXED: Matches your file
import com.example.myapplication.ui.viewmodel.ReportViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen(viewModel: ReportViewModel) {
    val context = LocalContext.current

    // 1. Fixed State: Using the correct 'Report' type
    var selectedReport by remember { mutableStateOf<Report?>(null) }

    // 2. Observe reports from ViewModel
    val reports by viewModel.reports.collectAsState(initial = emptyList())

    // 3. Check for Location Permission
    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 4. Initial camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(12.9716, 77.5946), 11f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
                mapType = MapType.NORMAL,
                isTrafficEnabled = false
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = true
            )
        ) {
            // 5. Draw markers using the real 'reports' list
            reports.forEach { report ->
                Marker(
                    state = MarkerState(position = LatLng(report.latitude, report.longitude)),
                    title = report.wasteType,
                    snippet = "Status: ${report.status}",
                    icon = if (report.status.equals("Cleaned", ignoreCase = true)) {
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                    } else {
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    },
                    onClick = {
                        selectedReport = report
                        false
                    }
                )
            }
        }

        // 6. Legend Overlay
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Legend",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pending", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Green, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cleaned", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}