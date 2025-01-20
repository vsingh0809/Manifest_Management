package com.sko.manifestmanagement.model
//
//data class UpdateCrewoperatorDTO(
//    val firstName: String,
//    val lastName: String,
//    val contactNumber: String,
//    val dob: String, // You might want to convert this to a proper date format if needed
//    val gender: String // Can be "Male", "Female", "Other"
//)


data class UpdateCrewoperatorDTO(
    val firstName: String,
    val lastName: String,
    val contactNumber: String,
    val dob: String,  // Keep it as a string, properly formatted before sending
    val gender: String,


) {
    // Helper function to validate gender
    fun isValidGender(): Boolean {
        return gender == "Male" || gender == "Female" || gender == "Other"
    }
}


