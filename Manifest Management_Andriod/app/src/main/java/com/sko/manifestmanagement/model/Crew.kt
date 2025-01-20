package com.sko.manifestmanagement.model

data class Crew(
    val crewId: Int,               // Unique ID for the crew member
    val salutation: String,           // Enum value for salutation (e.g., 0 = Mr., 1 = Mrs., etc.)
    val firstName: String,         // Crew member's first name
    val lastName: String,          // Crew member's last name
    val contactNumber: String,     // Crew member's contact number
    val dob: String,               // Date of birth in ISO format (e.g., "2024-12-03T06:49:04.932")
    val gender: String,               // Enum value for gender (e.g., 0 = Male, 1 = Female, 2 = Other)
    val crewUsername: String?,     // Crew member's username (nullable)
    val email: String,             // Crew member's email address
    val password: String,          // Crew member's password
    val crewImage: String?         // Image URL or path for the crew image (nullable)
)
