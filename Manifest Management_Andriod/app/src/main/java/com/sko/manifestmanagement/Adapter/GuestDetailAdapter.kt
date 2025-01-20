package com.sko.manifestmanagement.Adapter



import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.sko.manifestmanagement.Database.AppDatabase
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.model.GuestDetailDto
import com.sko.manifestmanagement.model.toGuest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuestDetailAdapter(
    private val context: Context,
    private var guestList: MutableList<GuestDetailDto>,  // Make sure it's mutable
    private val onEditClick: (GuestDetailDto) -> Unit,
    private val navController: NavController,
    private val onDeleteClick:(GuestDetailDto) ->Unit,
    private val onTextViewClick: (GuestDetailDto) -> Unit,
) : RecyclerView.Adapter<GuestDetailAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<Int>() // Track selected items
    private var isSelectionModeEnabled = false // Track if selection mode is active

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.title)
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val editButton: ImageView = itemView.findViewById(R.id.edit_icon)
        val downloadButton: ImageView = itemView.findViewById(R.id.download_icon)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val deleteButton : ImageView = itemView.findViewById(R.id.deleteButton)


        init {
            setupListeners()
        }

        private fun setupListeners() {
            // Long click to enable selection mode
            itemView.setOnLongClickListener {
                enableSelectionMode()
                toggleSelection(adapterPosition)
                true
            }

            // Normal click toggles selection if in selection mode
            itemView.setOnClickListener {
                if (isSelectionModeEnabled) {
                    toggleSelection(adapterPosition)
                }
            }

            idTextView.setOnClickListener {
                val guest = guestList[adapterPosition]
                Log.d("view id","${guest.guestId}")
                onTextViewClick(guest)
            }

            nameTextView.setOnClickListener {
                val guest = guestList[adapterPosition]
                Log.d("view id","${guest.guestId}")
                onTextViewClick(guest)
            }

            //delete button click
            deleteButton.setOnClickListener{
                showConfirmationDialog("delete", adapterPosition) {
                    val guest = guestList[adapterPosition]
                    Log.d("delete id","${guest.guestId}")
                    onDeleteClick(guest)
                }


            }


            // Edit button click
            editButton.setOnClickListener {

                showConfirmationDialog("Edit", adapterPosition) {
                    val guest = guestList[adapterPosition]
                    // Add logging to see if the onEditClick is triggered
                    Log.d(
                        "GuestDetailAdapter",
                        "Edit button clicked for guest ID: ${guest.guestId}"
                    )
                    onEditClick(guest)  // Trigger the onEditClick lambda passed from the Fragment                }

                }
            }

            // Download button click
            downloadButton.setOnClickListener {
                downloadItemDetails(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val guestDetail = guestList[position]

        // Update the text fields with `GuestDetailDto` properties
        holder.idTextView.text = "ID: ${guestDetail.guestId}"
        holder.nameTextView.text = "${guestDetail.firstName} ${guestDetail.lastName}"

        // Handle checkbox visibility and state
        holder.checkBox.visibility = if (isSelectionModeEnabled) View.VISIBLE else View.GONE
        holder.checkBox.isChecked = selectedItems.contains(position)
    }

    override fun getItemCount(): Int = guestList.size

    private fun enableSelectionMode() {
        if (!isSelectionModeEnabled) {
            isSelectionModeEnabled = true
            notifyDataSetChanged() // Update all items to show checkboxes
        }
    }

    private fun disableSelectionMode() {
        if (isSelectionModeEnabled) {
            isSelectionModeEnabled = false
            selectedItems.clear()
            notifyDataSetChanged() // Update all items to hide checkboxes
        }
    }

    private fun toggleSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }
        notifyItemChanged(position)
    }

    private fun showConfirmationDialog(action: String, position: Int, onConfirm: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("$action Confirmation")
            .setMessage("Are you sure you want to $action this item?")
            .setPositiveButton("Yes") { _, _ ->
                onConfirm()  // Trigger the confirmation action (e.g., navigate to edit page)
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

//    private fun downloadItemDetails(position: Int) {
//        val guest = guestList[position]
//        val content = """
//            Guest ID: ${guest.guestId}
//            Name: ${guest.firstName} ${guest.lastName}
//            Email: ${guest.email}
//            Gender: ${if (guest.gender == "Male") "Male" else "Female"}
//            Mobile: ${guest.mobileNo}
//            Boarding Point: ${guest.boardingPoint}
//            Destination Point: ${guest.destinationPoint}
//            Created Date: ${guest.createdDate}
//        """.trimIndent()
//
//        val isSuccess = FileStorageHelper.saveToLocalStorage(context, content)
//
//        AlertDialog.Builder(context)
//            .setTitle("Download Confirmation")
//            .setMessage(
//                if (isSuccess) "Guest details have been saved to local storage."
//                else "Failed to save guest details."
//            )
//            .setPositiveButton("OK", null)
//            .show()
//    }

    internal fun downloadItemDetails(position: Int) {
        // Retrieve the GuestDetailDto object safely from the list
        val guestDetailDto = guestList.getOrNull(position) as? GuestDetailDto
            ?: run {
                showErrorDialog("Error", "Guest details not found.")
                return
            }

        // Convert GuestDetailDto to Guest
        val convertedGuest = guestDetailDto.toGuest()

        // Get Room database instance
        val db = AppDatabase.getInstance(context)
        val guestDao = db.guestDao()

        if (context is AppCompatActivity) {
            context.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Check if the guest already exists
                    val existingGuest = guestDao.getGuestByGuestId(convertedGuest.guestId)

                    // Insert or update the guest in the database
                    if (existingGuest == null) {
                        guestDao.insertGuest(convertedGuest)
                    } else {
                        guestDao.updateGuest(convertedGuest)
                    }

                    // Notify the user on the main thread
                    withContext(Dispatchers.Main) {
                        showConfirmationDialog(
                            "Download Confirmation",
                            "Guest details have been saved to Room DB."
                        )
                    }
                } catch (e: Exception) {
                    // Handle any database-related exceptions
                    withContext(Dispatchers.Main) {
                        showErrorDialog("Error", "An error occurred: ${e.message}")
                    }
                }
            }
        }
    }

    // Utility function to show confirmation dialogs
    private fun showConfirmationDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    // Utility function to show error dialogs
    private fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }


    fun deleteSelectedItems() {
        val itemsToRemove = selectedItems.map { guestList[it] }
        guestList.removeAll(itemsToRemove)
        selectedItems.clear()
        notifyDataSetChanged()
        disableSelectionMode()
    }
}
