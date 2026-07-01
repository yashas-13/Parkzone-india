package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ParkingSpot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    spots: List<ParkingSpot>,
    favoriteSpotIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onBookSpot: (ParkingSpot) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }
    
    // Filter states
    var filterEv by remember { mutableStateOf(false) }
    var filterCovered by remember { mutableStateOf(false) }
    var filterSecurity by remember { mutableStateOf(false) }
    var filterFavorites by remember { mutableStateOf(false) }
    var maxPrice by remember { mutableStateOf(100f) }
    var sortOption by remember { mutableStateOf("Price: Low to High") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Mockup
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .shadow(20.dp, RoundedCornerShape(12.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("P", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("ParkZone", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
                    Text("BANGALORE CENTRAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF0F172A).copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔔", modifier = Modifier.padding(bottom = 2.dp))
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Search parking near Koramangala...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = { 
                IconButton(onClick = { showFilters = !showFilters }) {
                    Text("Filters", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.8f),
                unfocusedContainerColor = Color(0xFF0F172A).copy(alpha = 0.8f),
                unfocusedBorderColor = Color(0xFF1E293B),
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            )
        )
        
        if (showFilters) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).background(Color(0xFF0F172A), RoundedCornerShape(16.dp)).padding(16.dp)) {
                Text("Max Price: ₹${maxPrice.toInt()}/hr", color = MaterialTheme.colorScheme.onBackground)
                Slider(value = maxPrice, onValueChange = { maxPrice = it }, valueRange = 10f..200f)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Favorites Only", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall)
                    Switch(checked = filterFavorites, onCheckedChange = { filterFavorites = it }, modifier = Modifier.scale(0.8f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("EV Charging", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall)
                    Switch(checked = filterEv, onCheckedChange = { filterEv = it }, modifier = Modifier.scale(0.8f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Covered Parking", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall)
                    Switch(checked = filterCovered, onCheckedChange = { filterCovered = it }, modifier = Modifier.scale(0.8f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Security Camera", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall)
                    Switch(checked = filterSecurity, onCheckedChange = { filterSecurity = it }, modifier = Modifier.scale(0.8f))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Sort By", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { 
                        sortOption = when (sortOption) {
                            "Price: Low to High" -> "Price: High to Low"
                            "Price: High to Low" -> "Distance: Nearest"
                            else -> "Price: Low to High"
                        }
                    }) {
                        Text(sortOption, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Map Mockup View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color(0xFF0A0F18))
                .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(40.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Pseudo map details
            Text(
                "Interactive Map View",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Available Spots",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val filteredSpots = spots.filter { 
                (it.title.contains(searchQuery, ignoreCase = true) || 
                it.description.contains(searchQuery, ignoreCase = true)) &&
                it.pricePerHour <= maxPrice &&
                (!filterEv || it.hasEvCharging) &&
                (!filterCovered || it.isCovered) &&
                (!filterSecurity || it.hasSecurityCamera) &&
                (!filterFavorites || favoriteSpotIds.contains(it.id))
            }.sortedWith { a, b ->
                when (sortOption) {
                    "Price: Low to High" -> a.pricePerHour.compareTo(b.pricePerHour)
                    "Price: High to Low" -> b.pricePerHour.compareTo(a.pricePerHour)
                    "Distance: Nearest" -> {
                        // Mock user location: 12.9716, 77.5946
                        val distA = Math.pow(a.latitude - 12.9716, 2.0) + Math.pow(a.longitude - 77.5946, 2.0)
                        val distB = Math.pow(b.latitude - 12.9716, 2.0) + Math.pow(b.longitude - 77.5946, 2.0)
                        distA.compareTo(distB)
                    }
                    else -> a.pricePerHour.compareTo(b.pricePerHour)
                }
            }
            
            items(filteredSpots) { spot ->
                SpotCard(
                    spot = spot, 
                    isFavorite = favoriteSpotIds.contains(spot.id),
                    onToggleFavorite = { onToggleFavorite(spot.id) },
                    onBook = { onBookSpot(spot) }
                )
            }
        }
    }
}

@Composable
fun SpotCard(spot: ParkingSpot, isFavorite: Boolean, onToggleFavorite: () -> Unit, onBook: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.9f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = spot.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onToggleFavorite, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "Bangalore Central • 0.8 km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val amenities = mutableListOf<String>()
                    if (spot.hasEvCharging) amenities.add("EV")
                    if (spot.isCovered) amenities.add("Cov")
                    if (spot.hasSecurityCamera) amenities.add("Sec")
                    val amenityText = if (amenities.isEmpty()) "BASIC" else amenities.joinToString(" • ")
                    Text(
                        amenityText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF334155).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("PRICE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        if (spot.isDynamicPricing) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("⚡", fontSize = 10.sp)
                        }
                    }
                    Text("₹${spot.pricePerHour} / hour", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF334155).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Text("AVAILABILITY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("12 Slots left", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBook, 
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(2f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Book Space", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { /* View on map */ }, 
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text("🗺️")
                }
            }
        }
    }
}
