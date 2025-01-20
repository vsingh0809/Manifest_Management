package com.sko.manifestmanagement.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.model.Guest
class GuestAdapter(private var guestList: List<Guest>) : RecyclerView.Adapter<GuestAdapter.GuestViewHolder>() {

    // ViewHolder class
    inner class GuestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.guestName)
        val emailTextView: TextView = view.findViewById(R.id.guestEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_guest, parent, false)
        return GuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) {
        val guest = guestList[position]
        // Set the full name (first and last name)
        holder.nameTextView.text = "${guest.firstName} ${guest.lastName}"
        // Set the email
        holder.emailTextView.text = guest.email
    }

    override fun getItemCount(): Int = guestList.size

    // Method to update the data in the adapter
    fun updateData(newGuestList: List<Guest>) {
        guestList = newGuestList
        notifyDataSetChanged()
    }
}

