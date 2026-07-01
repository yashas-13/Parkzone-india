package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

enum class UserRole {
    DRIVER,
    HOST
}

@Serializable
@Entity(tableName = "users")
data class UserProfile(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.DRIVER
)

@Serializable
@Entity(tableName = "spots")
data class ParkingSpot(
    @PrimaryKey val id: String = "",
    val hostId: String = "",
    val title: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val pricePerHour: Double = 0.0,
    val isAvailable: Boolean = true,
    val rating: Double = 5.0,
    val totalRatings: Int = 0,
    val hasEvCharging: Boolean = false,
    val isCovered: Boolean = false,
    val hasSecurityCamera: Boolean = false,
    val isDynamicPricing: Boolean = false
)

@Serializable
@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey val id: String = "",
    val spotId: String = "",
    val userId: String = "",
    val startTimeMillis: Long = 0,
    val endTimeMillis: Long = 0,
    val totalCost: Double = 0.0,
    val status: String = "ACTIVE" // ACTIVE, COMPLETED, CANCELLED
)
