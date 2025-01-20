

package com.sko.manifestmanagement.Database



import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "guest_details")
data class Guest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val guestId: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val mobileNo: String,
    val boardingPoint: String,
    val destinationPoint: String,
    val createdDate: String? = null  // Make createdDate nullable if it can be missing
)

