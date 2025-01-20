//package com.sko.manifestmanagement.Adapter
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
//import com.sko.manifestmanagement.model.Item
//
//class MyAdapter(
//    private val context: Context,
//    private val itemList: MutableList<Item>, // MutableList for potential data updates
//    private val onItemClick: (Item) -> Unit,  // Single-click listener to show item details
//    private val onSelectionChanged: () -> Unit, // Callback to notify fragment of selection change
//    private val onLongClick: () -> Unit // Callback for long-click to enable checkbox mode
//) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
//
//    private val selectedItems: MutableList<Item> = mutableListOf()
//    private var isSelectionMode = false  // Track if we are in selection mode (checkboxes visible)
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val titleTextView: TextView = itemView.findViewById(R.id.title)
//        val nameTextView: TextView = itemView.findViewById(R.id.name)
//        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
//        val editButton: ImageView = itemView.findViewById(R.id.edit_icon)
//        val downloadButton: ImageView = itemView.findViewById(R.id.download_icon)
//
//        init {
//            setupListeners()
//        }
//
//        private fun setupListeners() {
//            // Handle checkbox selection
//            checkBox.setOnCheckedChangeListener { _, isChecked ->
//                val item = itemList[adapterPosition]
//                item.isSelected = isChecked
//
//                // Update selectedItems list based on checkbox state
//                if (isChecked) {
//                    selectedItems.add(item)
//                } else {
//                    selectedItems.remove(item)
//                }
//
//                // Notify the fragment to update delete button visibility
//                onSelectionChanged() // Callback to update visibility of the delete button
//            }
//
//            // Single-click listener to show item details
//            itemView.setOnClickListener {
//                if (isSelectionMode) {
//                    // In selection mode, toggle the checkbox for selection/deselection
//                    checkBox.isChecked = !checkBox.isChecked
//                    checkBox.performClick() // Triggers checkbox checked change listener
//                } else {
//                    onItemClick(itemList[adapterPosition]) // Show details
//                }
//            }
//
//            // Long-click listener to toggle selection mode and show checkboxes
//            itemView.setOnLongClickListener {
//                isSelectionMode = true
//                checkBox.visibility = View.VISIBLE // Show checkbox
//                onLongClick() // Notify fragment to show delete button
//                true  // Return true to indicate the long click event is handled
//            }
//
//            // Edit button click
//            editButton.setOnClickListener {
//                showConfirmationDialog("Edit", adapterPosition)
//            }
//
//            // Download button click
//            downloadButton.setOnClickListener {
//                downloadItemDetails(adapterPosition)
//            }
//        }
//
//        // Bind item data and update the state of the checkbox visibility and state
//        fun bind(item: Item) {
//            titleTextView.text = item.title
//            nameTextView.text = item.name
//            checkBox.isChecked = item.isSelected
//
//            // Set visibility of the checkbox (only show when selection mode is active)
//            checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = itemList[position]
//        holder.bind(item)
//    }
//
//    override fun getItemCount(): Int = itemList.size
//
//
//    private fun showConfirmationDialog(action: String, position: Int) {
//        val item = itemList[position]
//        AlertDialog.Builder(context)
//            .setTitle("$action Confirmation")
//            .setMessage("Are you sure you want to $action this item?")
//            .setPositiveButton("Yes") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setNegativeButton("No") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//
//    private fun downloadItemDetails(position: Int) {
//        val item = itemList[position]
//        val content = "User ID: ${item.id}\nTitle: ${item.title}\nName: ${item.name}\n\n"
//
//        val isSuccess = FileStorageHelper.saveToLocalStorage(context, content)
//
//        AlertDialog.Builder(context)
//            .setTitle("Download Confirmation")
//            .setMessage(
//                if (isSuccess) "User details have been saved to local storage."
//                else "Failed to save user details."
//            )
//            .setPositiveButton("OK", null)
//            .show()
//    }
//
//    // Function to delete selected items with a confirmation dialog
//    fun deleteSelectedItems() {
//        AlertDialog.Builder(context)
//            .setTitle("Delete Confirmation")
//            .setMessage("Are you sure you want to delete the selected items?")
//            .setPositiveButton("Yes") { dialog, _ ->
//                itemList.removeAll { it.isSelected }
//                selectedItems.clear() // Clear the selected items list
//                notifyDataSetChanged()
//                dialog.dismiss()
//            }
//            .setNegativeButton("No") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//
//    // Get the list of selected items
//    fun getSelectedItems(): List<Item> = selectedItems
//
//    // Function to exit selection mode (hide checkboxes)
//    fun exitSelectionMode() {
//        isSelectionMode = false
//        for (item in itemList) {
//            item.isSelected = false
//        }
//        notifyDataSetChanged()
//    }
//}
