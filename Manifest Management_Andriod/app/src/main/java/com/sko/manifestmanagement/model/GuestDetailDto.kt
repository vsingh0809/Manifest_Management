package com.sko.manifestmanagement.model

data class GuestDetailDto(
    val salutation: String,
    val boardingPoint: String,
    val citizenship: Any,
    val createdBy: Any,
    val createdDate: String,
    val destinationPoint: String,
    val dob: Any,
    val email: String,
    val firstName: String,
    val gender: String,
    val guestId: Int,
    val guestImage: Any,
    val lastName: String,
    val lastUpdatedBy: Any,
    val lastUpdatedDate: Any,
    val mobileNo: String
)

fun GuestDetailDto.toGuest(): com.sko.manifestmanagement.Database.Guest {
    return com.sko.manifestmanagement.Database.Guest(
        guestId = this.guestId,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        mobileNo = this.mobileNo,
        boardingPoint = this.boardingPoint,
        destinationPoint = this.destinationPoint

    )
}