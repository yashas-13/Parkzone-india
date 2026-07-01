package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Booking
import com.example.model.ParkingSpot

@Dao
interface ParkingDao {
    @Query("SELECT * FROM spots WHERE isAvailable = 1")
    suspend fun getAvailableSpots(): List<ParkingSpot>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpot(spot: ParkingSpot)

    @Update
    suspend fun updateSpot(spot: ParkingSpot)

    @Query("SELECT * FROM spots WHERE id = :spotId LIMIT 1")
    suspend fun getSpotById(spotId: String): ParkingSpot?

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    suspend fun getMyBookings(userId: String): List<Booking>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: Booking)
}
