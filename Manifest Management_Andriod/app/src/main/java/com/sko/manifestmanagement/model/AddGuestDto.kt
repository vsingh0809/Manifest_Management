package com.sko.manifestmanagement.model

data class AddGuestDto (
    val salutation:String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val mobileNo: String,
    val email: String,
    val boardingPoint: String,
    val destinationPoint: String,
    val citizenship:String,
    val lastUpdatedBy: Any,
    val lastUpdatedDate: Any,
    val createdBy: Any,
    val createdDate: String
)