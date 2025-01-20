

package com.sko.manifestmanagement.Database



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface GuestDao {
    @Insert
    suspend fun insertGuest(guest: Guest)

    @Update
    suspend fun updateGuest(guest: Guest)

    @Query("SELECT * FROM guest_details WHERE guestId = :guestId LIMIT 1")
    suspend fun getGuestByGuestId(guestId: Int): Guest?

    @Query("SELECT * FROM guest_details")
    suspend fun getAllGuests(): List<Guest>

}

