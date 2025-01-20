//package com.sko.manifestmanagement.Adapter
//
//
//
//import android.app.AlertDialog
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.CheckBox
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.sko.manifestmanagement.R
//import com.sko.manifestmanagement.filehandling.FileStorageHelper
//import com.sko.manifestmanagement.model.AlbumItem
//import com.sko.manifestmanagement.model.Crew
//
//class AlbumAdapter(
//    private val context: Context,
//    private val albumList: MutableList<Crew>,
//    private val onEditClick: (Crew) -> Unit,
//
//    ) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
//
//    private val selectedItems = mutableSetOf<Int>() // Track selected items
//    private var isSelectionModeEnabled = false // Track if selection mode is active
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val idTextView: TextView = itemView.findViewById(R.id.title)
//        val titleTextView: TextView = itemView.findViewById(R.id.name)
//        val editButton: ImageView = itemView.findViewById(R.id.edit_icon)
//        val downloadButton: ImageView = itemView.findViewById(R.id.download_icon)
//        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
//
//        init {
//            setupListeners()
//        }
//
//        private fun setupListeners() {
//            // Long click to enable selection mode
//            itemView.setOnLongClickListener {
//                enableSelectionMode()
//                toggleSelection(adapterPosition)
//                true
//            }
//
//            // Normal click toggles selection if in selection mode
//            itemView.setOnClickListener {
//                if (isSelectionModeEnabled) {
//                    toggleSelection(adapterPosition)
//                }
//            }
//
//            // Edit button click
//            editButton.setOnClickListener {
//                showConfirmationDialog("Edit", adapterPosition) {
//                    onEditClick(albumList[adapterPosition])
//                }
//            }
//
//            // Download button click
//            downloadButton.setOnClickListener {
//                downloadItemDetails(adapterPosition)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val albumItem = albumList[position]
//        holder.idTextView.text = "ID: ${albumItem.crewId}"
//        holder.titleTextView.text = albumItem.firstName
//
//        // Handle checkbox visibility and state
//        holder.checkBox.visibility = if (isSelectionModeEnabled) View.VISIBLE else View.GONE
//        holder.checkBox.isChecked = selectedItems.contains(position)
//    }
//
//    override fun getItemCount(): Int = albumList.size
//
//    private fun enableSelectionMode() {
//        if (!isSelectionModeEnabled) {
//            isSelectionModeEnabled = true
//            notifyDataSetChanged() // Update all items to show checkboxes
//        }
//    }
//
//    private fun disableSelectionMode() {
//        if (isSelectionModeEnabled) {
//            isSelectionModeEnabled = false
//            selectedItems.clear()
//            notifyDataSetChanged() // Update all items to hide checkboxes
//        }
//    }
//
//    private fun toggleSelection(position: Int) {
//        if (selectedItems.contains(position)) {
//            selectedItems.remove(position)
//        } else {
//            selectedItems.add(position)
//        }
//        notifyItemChanged(position)
//    }
//
//    private fun showConfirmationDialog(action: String, position: Int, onConfirm: () -> Unit) {
//        AlertDialog.Builder(context)
//            .setTitle("$action Confirmation")
//            .setMessage("Are you sure you want to $action this item?")
//            .setPositiveButton("Yes") { dialog, _ ->
//                dialog.dismiss()
//                onConfirm()
//            }
//            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
//            .show()
//    }
//
//    private fun downloadItemDetails(position: Int) {
//        val album = albumList[position]
//        val content = "ID: ${album.crewId}\nTitle: ${album.crewUsername}\nDescription: ${album.gender}\n\n"
//
//        val isSuccess = FileStorageHelper.saveToLocalStorage(context, content)
//
//        AlertDialog.Builder(context)
//            .setTitle("Download Confirmation")
//            .setMessage(
//                if (isSuccess) "Album details have been saved to local storage."
//                else "Failed to save album details."
//            )
//            .setPositiveButton("OK", null)
//            .show()
//    }
//
//    fun deleteSelectedItems() {
//        val itemsToRemove = selectedItems.map { albumList[it] }
//        albumList.removeAll(itemsToRemove)
//        selectedItems.clear()
//        notifyDataSetChanged()
//        disableSelectionMode()
//    }
//}
