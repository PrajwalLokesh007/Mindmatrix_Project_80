package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.User
import com.example.myapplication.ui.components.SectionHeader
import com.example.myapplication.ui.theme.EcoGradientEnd
import com.example.myapplication.ui.theme.EcoGradientStart
import com.example.myapplication.ui.theme.SoftBlue
import com.example.myapplication.ui.viewmodel.UserViewModel

@Composable
fun RewardsProfileScreen(viewModel: UserViewModel = viewModel()) {
    val user by viewModel.userData.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            ProfileHeader(user)
        }

        item {
            StatisticsSection(user)
        }

        item {
            SectionHeader(title = "Achievement Badges")
            BadgesRow()
        }

        item {
            SectionHeader(title = "Your Impact")
        }

        item {
            ImpactCard()
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileHeader(user: User?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = Color.Gray)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user?.name ?: "Loading...",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = if ((user?.ecoPoints ?: 0) > 500) "Elite Environmentalist" else "Eco Pioneer",
            style = MaterialTheme.typography.bodyMedium,
            color = EcoGradientStart
        )
    }
}

@Composable
fun StatisticsSection(user: User?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            label = "Eco Score",
            value = user?.ecoPoints?.toString() ?: "0",
            icon = Icons.Default.Stars,
            modifier = Modifier.weight(1f),
            color = EcoGradientStart
        )
        StatCard(
            label = "Reports",
            value = "...", // Would need a count query in real app
            icon = Icons.Default.Description,
            modifier = Modifier.weight(1f),
            color = SoftBlue
        )
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
    }
}

@Composable
fun BadgesRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        val badges = listOf(
            "Eco Warrior" to Icons.Default.Shield,
            "Early Bird" to Icons.Default.WbSunny,
            "Plast-Ex" to Icons.Default.DeleteForever,
            "Tree Planter" to Icons.Default.Park
        )
        items(badges) { (name, icon) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(listOf(EcoGradientStart, SoftBlue))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(name, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun ImpactCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = EcoGradientStart.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Keep going!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EcoGradientStart
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your contributions have helped clean up local neighborhoods. Every report brings us closer to a greener planet.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
