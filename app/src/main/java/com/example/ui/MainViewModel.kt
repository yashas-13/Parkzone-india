package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AuthManager
import com.example.data.ParkingRepository
import com.example.model.Booking
import com.example.model.ParkingSpot
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val authManager = AuthManager()
    private val repository = ParkingRepository(AppDatabase.getDatabase(application).parkingDao())

    private val _isUserLoggedIn = MutableStateFlow(authManager.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.DRIVER)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _availableSpots = MutableStateFlow<List<ParkingSpot>>(emptyList())
    val availableSpots: StateFlow<List<ParkingSpot>> = _availableSpots.asStateFlow()

    private val _myBookings = MutableStateFlow<List<Booking>>(emptyList())
    val myBookings: StateFlow<List<Booking>> = _myBookings.asStateFlow()
    
    private val _favoriteSpotIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteSpotIds: StateFlow<Set<String>> = _favoriteSpotIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private fun updateProfileFromCurrentUser() {
        viewModelScope.launch {
            authManager.currentUser?.let { user ->
                val profileResult = repository.getUserProfile(user.uid)
                if (profileResult.isSuccess) {
                    val profile = profileResult.getOrNull()
                    if (profile != null) {
                        _userRole.value = profile.role
                        // Update existing profile with latest Google info
                        val updatedProfile = profile.copy(
                            name = user.displayName ?: profile.name,
                            email = user.email ?: profile.email
                        )
                        repository.saveUserProfile(updatedProfile)
                    } else {
                        val newProfile = com.example.model.UserProfile(
                            id = user.uid,
                            name = user.displayName ?: "User",
                            email = user.email ?: "",
                            role = _userRole.value
                        )
                        repository.saveUserProfile(newProfile)
                    }
                }
            }
            loadSpots()
        }
    }

    init {
        if (_isUserLoggedIn.value) {
            updateProfileFromCurrentUser()
        } else {
            // Load dummy spots for preview if not logged in
            loadSpots()
        }
    }

    fun onSignInSuccess() {
        _isUserLoggedIn.value = true
        updateProfileFromCurrentUser()
    }

    fun switchRole(role: UserRole) {
        _userRole.value = role
        viewModelScope.launch {
            authManager.currentUser?.let { user ->
                val profileResult = repository.getUserProfile(user.uid)
                val profile = profileResult.getOrNull()
                if (profile != null) {
                    repository.saveUserProfile(profile.copy(role = role))
                }
            }
        }
    }
    fun loadSpots() {
        viewModelScope.launch {
            _isLoading.value = true
            _availableSpots.value = repository.getAvailableSpots()
            _isLoading.value = false
        }
    }

    fun loadBookings() {
        viewModelScope.launch {
            authManager.currentUser?.uid?.let { uid ->
                _isLoading.value = true
                _myBookings.value = repository.getMyBookings(uid)
                _isLoading.value = false
            }
        }
    }
    
    fun toggleFavorite(spotId: String) {
        val current = _favoriteSpotIds.value.toMutableSet()
        if (current.contains(spotId)) {
            current.remove(spotId)
        } else {
            current.add(spotId)
        }
        _favoriteSpotIds.value = current
    }

    fun addSpot(title: String, description: String, price: Double, lat: Double, lng: Double, hasEvCharging: Boolean, isCovered: Boolean, hasSecurityCamera: Boolean, isDynamicPricing: Boolean, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val uid = authManager.currentUser?.uid ?: "dummy_host"
            val spot = ParkingSpot(
                hostId = uid,
                title = title,
                description = description,
                pricePerHour = price,
                latitude = lat,
                longitude = lng,
                hasEvCharging = hasEvCharging,
                isCovered = isCovered,
                hasSecurityCamera = hasSecurityCamera,
                isDynamicPricing = isDynamicPricing
            )
            repository.addSpot(spot)
            loadSpots()
            onSuccess()
        }
    }

    fun bookSpot(spot: ParkingSpot, durationHours: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val uid = authManager.currentUser?.uid ?: "dummy_user"
            val startTime = System.currentTimeMillis()
            val endTime = startTime + (durationHours * 3600000L)
            
            val booking = Booking(
                spotId = spot.id,
                userId = uid,
                startTimeMillis = startTime,
                endTimeMillis = endTime,
                totalCost = spot.pricePerHour * durationHours
            )
            repository.createBooking(booking)
            loadSpots()
            onSuccess()
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
            _isUserLoggedIn.value = false
        }
    }
}
