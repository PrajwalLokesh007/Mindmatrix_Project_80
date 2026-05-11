package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.EcoGradientEnd
import com.example.myapplication.ui.theme.EcoGradientStart
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.viewmodel.ReportState
import com.example.myapplication.ui.viewmodel.ReportViewModel
import com.example.myapplication.utils.LocationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportWasteScreen(viewModel: ReportViewModel, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }
    
    var description by remember { mutableStateOf("") }
    var wasteType by remember { mutableStateOf("Select Waste Type") }
    var showDropdown by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var addressText by remember { mutableStateOf("Fetching location...") }

    val reportState by viewModel.reportState.collectAsState()
    val currentUser = authViewModel.currentUser

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(Unit) {
        val loc = locationHelper.getCurrentLocation()
        if (loc != null) {
            location = Pair(loc.latitude, loc.longitude)
            addressText = "${loc.latitude}, ${loc.longitude}"
        } else {
            addressText = "Location Permission Required"
        }
    }

    LaunchedEffect(reportState) {
        if (reportState is ReportState.Success) {
            description = ""
            wasteType = "Select Waste Type"
            imageUri = null
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Report Waste",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        // Image Upload Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(EcoGradientStart, EcoGradientEnd)),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = EcoGradientStart
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upload Proof of Waste", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // GPS Location Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = null, tint = EcoGradientStart)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Captured Location", style = MaterialTheme.typography.labelSmall)
                    Text(
                        addressText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Waste Type Dropdown
        Box {
            OutlinedTextField(
                value = wasteType,
                onValueChange = {},
                readOnly = true,
                label = { Text("Waste Type") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { showDropdown = !showDropdown }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                listOf("Plastic", "Organic", "Industrial", "E-Waste", "Others").forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            wasteType = type
                            showDropdown = false
                        }
                    )
                }
            }
        }

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Describe the situation") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp)
        )

        if (reportState is ReportState.Error) {
            Text(
                text = (reportState as ReportState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (currentUser != null && imageUri != null && location != null) {
                    viewModel.submitReport(
                        userId = currentUser.uid,
                        imageUri = imageUri!!,
                        wasteType = wasteType,
                        lat = location!!.first,
                        lng = location!!.second,
                        description = description
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = reportState !is ReportState.Loading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EcoGradientStart)
        ) {
            if (reportState is ReportState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    "Submit Report",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
