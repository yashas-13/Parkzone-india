package com.example.data

import com.example.model.Booking
import com.example.model.ParkingSpot
import com.example.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ParkingRepository(private val dao: ParkingDao) {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAvailableSpots(): List<ParkingSpot> {
        return try {
            // First try to fetch from Firestore
            val snapshot = firestore.collection("spots").get().await()
            val firestoreSpots = snapshot.toObjects(ParkingSpot::class.java)
            
            if (firestoreSpots.isNotEmpty()) {
                // Sync to local DB
                firestoreSpots.forEach { dao.insertSpot(it) }
                firestoreSpots.filter { it.isAvailable }
            } else {
                val spots = dao.getAvailableSpots()
                if (spots.isEmpty()) {
                    val dummySpots = getDummySpots()
                    dummySpots.forEach { dao.insertSpot(it) }
                    // Sync dummy to Firestore
                    dummySpots.forEach { firestore.collection("spots").document(it.id).set(it) }
                    dummySpots
                } else {
                    spots
                }
            }
        } catch (e: Exception) {
            val spots = dao.getAvailableSpots()
            if (spots.isEmpty()) getDummySpots() else spots
        }
    }

    suspend fun addSpot(spot: ParkingSpot): Result<Unit> {
        return try {
            val newSpot = spot.copy(id = if (spot.id.isEmpty()) UUID.randomUUID().toString() else spot.id)
            dao.insertSpot(newSpot)
            // Sync to Firestore
            firestore.collection("spots").document(newSpot.id).set(newSpot).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMyBookings(userId: String): List<Booking> {
        return try {
            val snapshot = firestore.collection("bookings").whereEqualTo("userId", userId).get().await()
            val firestoreBookings = snapshot.toObjects(Booking::class.java)
            if (firestoreBookings.isNotEmpty()) {
                firestoreBookings.forEach { dao.insertBooking(it) }
                firestoreBookings
            } else {
                dao.getMyBookings(userId)
            }
        } catch (e: Exception) {
            dao.getMyBookings(userId)
        }
    }

    suspend fun createBooking(booking: Booking): Result<Unit> {
        return try {
            val newBooking = booking.copy(id = if (booking.id.isEmpty()) UUID.randomUUID().toString() else booking.id)
            dao.insertBooking(newBooking)
            // Sync booking to Firestore
            firestore.collection("bookings").document(newBooking.id).set(newBooking).await()
            
            // Mark spot as unavailable locally and remotely
            val spot = dao.getSpotById(booking.spotId)
            if (spot != null) {
                val updatedSpot = spot.copy(isAvailable = false)
                dao.updateSpot(updatedSpot)
                firestore.collection("spots").document(updatedSpot.id).set(updatedSpot).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            firestore.collection("users").document(userProfile.id).set(userProfile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile?> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                Result.success(document.toObject(UserProfile::class.java))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getDummySpots(): List<ParkingSpot> {
        return listOf(
            ParkingSpot("1", "host1", "Koramangala 4th Block - Secure Garage", "Covered parking with 24/7 security camera.", 12.9345, 77.6266, 40.0, true, 4.8, 12, hasEvCharging = true, isCovered = true, hasSecurityCamera = true, isDynamicPricing = false),
            ParkingSpot("2", "host2", "Indiranagar 100ft Road - Open Space", "Spacious open parking, suitable for SUVs.", 12.9784, 77.6408, 50.0, true, 4.5, 8, hasEvCharging = false, isCovered = false, hasSecurityCamera = false, isDynamicPricing = true),
            ParkingSpot("3", "host3", "MG Road Commercial - Basement", "Basement parking right next to MG Road Metro.", 12.9750, 77.6060, 80.0, true, 4.9, 45, hasEvCharging = false, isCovered = true, hasSecurityCamera = true, isDynamicPricing = false),
            ParkingSpot("4", "host4", "HSR Layout Sector 2 - Driveway", "Safe neighborhood driveway parking.", 12.9141, 77.6371, 30.0, true, 4.2, 5, hasEvCharging = false, isCovered = false, hasSecurityCamera = false, isDynamicPricing = false)
        )
    }
}
