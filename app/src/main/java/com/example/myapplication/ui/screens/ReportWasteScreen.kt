package com.example.myapplication.ui.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import com.example.myapplication.ui.theme.SoftBlue
import com.example.myapplication.ui.viewmodel.AuthViewModel
import com.example.myapplication.ui.viewmodel.ReportState
import com.example.myapplication.ui.viewmodel.ReportViewModel
import com.example.myapplication.utils.ComposeFileProvider
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
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var addressText by remember { mutableStateOf("Tap to fetch location") }

    var showImageSourceSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val reportState by viewModel.reportState.collectAsState()
    val currentUser = authViewModel.currentUser

    // 1. Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
        }
        showImageSourceSheet = false
    }

    // 2. Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempCameraUri
        }
        showImageSourceSheet = false
    }

    // 3. Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            scope.launch {
                val loc = locationHelper.getCurrentLocation()
                if (loc != null) {
                    location = Pair(loc.latitude, loc.longitude)
                    addressText = "Lat: ${String.format("%.4f", loc.latitude)}, Lng: ${String.format("%.4f", loc.longitude)}"
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(reportState) {
        if (reportState is ReportState.Success) {
            description = ""
            wasteType = "Select Waste Type"
            imageUri = null
            viewModel.resetState()
            Toast.makeText(context, "Report Submitted Successfully!", Toast.LENGTH_LONG).show()
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
            text = "Report Illegal Dumping",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
        )

        // Image Upload Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(EcoGradientStart, EcoGradientEnd)),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable { showImageSourceSheet = true },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .clickable { imageUri = null }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.padding(4.dp))
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddAPhoto,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = EcoGradientStart
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add Proof Photo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Tap to capture or upload", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }

        // GPS Location Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    locationPermissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = EcoGradientStart, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Incident Location", style = MaterialTheme.typography.labelSmall, color = EcoGradientStart)
                    Text(
                        addressText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color.Gray)
            }
        }

        // Waste Type Dropdown
        Box {
            OutlinedTextField(
                value = wasteType,
                onValueChange = {},
                readOnly = true,
                label = { Text("What type of waste is it?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
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
                listOf("Plastic", "Organic/Food", "Industrial/Construction", "Electronic (E-Waste)", "Medical", "Hazardous", "Others").forEach { type ->
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
            label = { Text("Tell us more (Optional)") },
            placeholder = { Text("e.g. Near the community park entrance...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp)
        )

        if (reportState is ReportState.Error) {
            Text(
                text = (reportState as ReportState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (imageUri == null) {
                    Toast.makeText(context, "Please add a photo first", Toast.LENGTH_SHORT).show()
                } else if (location == null) {
                    Toast.makeText(context, "Please fetch location first", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.submitReport(
                        context = context,
                        userId = currentUser?.uid ?: "",
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
                .height(60.dp),
            enabled = reportState !is ReportState.Loading && currentUser != null,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EcoGradientStart)
        ) {
            if (reportState is ReportState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    "Submit Environmental Report",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    // Modern Image Source Bottom Sheet
    if (showImageSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showImageSourceSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp, start = 24.dp, end = 24.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Choose Image Source",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            try {
                                // EXACT authority from Manifest
                                val authority = "com.example.myapplication.fileprovider"
                                val uri = ComposeFileProvider.getImageUri(context, authority)
                                tempCameraUri = uri
                                cameraLauncher.launch(uri)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Camera Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = EcoGradientStart)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Take a Photo", style = MaterialTheme.typography.bodyLarge)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { galleryLauncher.launch("image/*") }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = SoftBlue)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Choose from Gallery", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
