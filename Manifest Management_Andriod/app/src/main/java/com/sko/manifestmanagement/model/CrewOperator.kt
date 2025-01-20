package com.sko.manifestmanagement.model

data class Crewoperator(
    val crewId: Int,
    val salutation: String,
    val firstName: String,
    val lastName: String,
    val contactNumber: String,
    val dob: String,  // You may want to use a Date type instead of String
    val gender: String,
    val crewUsername: String,
    val email: String,
    val password: String,  // This should probably not be returned in a real API response
    val crewImage: String?  // If the image URL is null, it will be handled correctly
)

