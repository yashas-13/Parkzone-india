package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostScreen(
    onAddSpot: (title: String, description: String, price: Double, lat: Double, lng: Double, hasEvCharging: Boolean, isCovered: Boolean, hasSecurityCamera: Boolean, isDynamicPricing: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    
    // Amenities
    var hasEvCharging by remember { mutableStateOf(false) }
    var isCovered by remember { mutableStateOf(false) }
    var hasSecurityCamera by remember { mutableStateOf(false) }
    var isDynamicPricing by remember { mutableStateOf(false) }
    
    // Mock coordinates for Bangalore
    var lat by remember { mutableStateOf("12.9716") }
    var lng by remember { mutableStateOf("77.5946") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Host Your Parking Space",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Earn money by renting out your unused parking space to drivers in Bangalore.",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Spot Title (e.g. Covered Garage in Indiranagar)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF1E293B),
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF1E293B),
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price per hour (₹)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF0F172A).copy(alpha = 0.8f),
                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF1E293B),
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Amenities & Features", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("EV Charging Station", color = MaterialTheme.colorScheme.onBackground)
            Switch(checked = hasEvCharging, onCheckedChange = { hasEvCharging = it })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Covered Parking", color = MaterialTheme.colorScheme.onBackground)
            Switch(checked = isCovered, onCheckedChange = { isCovered = it })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Security Camera", color = MaterialTheme.colorScheme.onBackground)
            Switch(checked = hasSecurityCamera, onCheckedChange = { hasSecurityCamera = it })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Pricing Strategy", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Dynamic Pricing", color = MaterialTheme.colorScheme.onBackground)
                Text("Automatically adjust price based on demand and local events.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = isDynamicPricing, onCheckedChange = { isDynamicPricing = it })
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                val priceDouble = price.toDoubleOrNull() ?: 0.0
                val latDouble = lat.toDoubleOrNull() ?: 12.9716
                val lngDouble = lng.toDoubleOrNull() ?: 77.5946
                onAddSpot(title, description, priceDouble, latDouble, lngDouble, hasEvCharging, isCovered, hasSecurityCamera, isDynamicPricing)
                
                title = ""
                description = ""
                price = ""
                hasEvCharging = false
                isCovered = false
                hasSecurityCamera = false
                isDynamicPricing = false
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = title.isNotBlank() && price.isNotBlank(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("List Parking Space", fontWeight = FontWeight.Bold)
        }
    }
}
