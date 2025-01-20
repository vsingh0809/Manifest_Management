package com.sko.manifestmanagement.model

data class CrewMember(
    val crewId: Int,
    val salutation: String, // Numeric value for salutation
    val firstName: String,
    val lastName: String,
    val contactNumber: String,
    val dob: String, // Date of birth (can be parsed to a Date or String)
    val gender: String, // 0 for male, 1 for female
    val crewUsername: String?, // Username can be null
    val email: String,
    val password: String, // Encrypted password, not needed to display
    val crewImage: String? // Add this field
)
