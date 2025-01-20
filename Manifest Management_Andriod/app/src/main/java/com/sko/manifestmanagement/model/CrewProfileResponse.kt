package com.sko.manifestmanagement.model


data class CrewProfileResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val contactNumber: String,
    val profileImageUrl: String?
)

