package com.sko.manifestmanagement.Fragment

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sko.manifestmanagement.Adapter.GuestDetailAdapter
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentHomeBinding
import com.sko.manifestmanagement.model.GuestDetailDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var apiService: ApiServices
    private lateinit var adapter:GuestDetailAdapter
    private var itemList: MutableList<GuestDetailDto> = mutableListOf()
    private var selectedItems: MutableList<GuestDetailDto> = mutableListOf()
    private lateinit var mBarkDateEditText: EditText
    private lateinit var dBarkDateEditText: EditText


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val navController=findNavController()
        binding = FragmentHomeBinding.inflate(inflater, container, false)


        // Initialize RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = GuestDetailAdapter(requireContext(), itemList,
            onEditClick = { guest ->
                // Pass the guestId to the EditGuestFragment using navigation
                Log.d("onclick guestid","${guest.guestId}")
                val action= HomeFragmentDirections.actionHomeFragmentToEditGuestFragment(guest.guestId)
                navController.navigate(action)},
            navController = navController,
            onDeleteClick = {guest ->
                deleteGuest(guest.guestId)
            },
            onTextViewClick = { guest ->
                Log.d("onclick textview","${guest.guestId}")
                val action = HomeFragmentDirections.actionHomeFragmentToGuestProfile(guest.guestId)
                navController.navigate(action)
            }
        )


        binding.recyclerview.adapter = adapter

        // Initialize Retrofit Service
        apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)

        // Fetch data from API
        fetchItemsFromApi()

        // Handle delete button click
//        binding.deleteButton.setOnClickListener {
//            deleteSelectedItems()
//        }

        // Set up date pickers for mBark and dBark
        setupDatePickers()

        // Handle sort by mbark button click
        binding.sortByMbarkButton.setOnClickListener {
            val mBarkDateString = mBarkDateEditText.text.toString()
            if (mBarkDateString.isNotEmpty()) {
                try {
                    val mBarkDate = LocalDate.parse(mBarkDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    searchByEmbarkDate(mBarkDateString)
                } catch (e: Exception) {
                    showToast("Select Mbark Date")
                    Log.e("HomeFragment", "Invalid mBark date format: $mBarkDateString", e)
                }
            } else {
                showToast("Select Mbark Date")
            }
            setupDatePickers()
        }

        // Handle sort by dbark button click
        binding.sortByDbarkButton.setOnClickListener {
            val dBarkDateString = dBarkDateEditText.text.toString()
            if (dBarkDateString.isNotEmpty()) {
                try {
                    val dBarkDate = LocalDate.parse(dBarkDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    searchByDebarkDate(dBarkDateString)
                } catch (e: Exception) {
                    showToast("Select Dbark Date")
                    Log.e("HomeFragment", "Invalid dBark date format: $dBarkDateString", e)
                }
            } else {
                showToast("Please select a valid date")
            }
            setupDatePickers()
        }
        return binding.root
    }

    private fun handleDeleteItem(item: GuestDetailDto) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)  // Remove item from selected list if already selected
        } else {
            selectedItems.add(item)  // Add item to selected list
        }

        // Toggle delete button visibility based on selection
//        binding.deleteButton.visibility = if (selectedItems.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun deleteGuest(id: Int) {
        Log.d("in deleteGuest", "$id")

        // Make the delete request using the existing apiService instance
        apiService.deleteGuest(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Guest deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    // After successful deletion, fetch updated list
                    fetchItemsFromApi()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to delete guest",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun fetchItemsFromApi() {
        apiService.getGuest().enqueue(object : retrofit2.Callback<List<GuestDetailDto>> {
            override fun onResponse(call: retrofit2.Call<List<GuestDetailDto>>, response: retrofit2.Response<List<GuestDetailDto>>) {
                if (response.isSuccessful) {
                    itemList.clear()
                    response.body()?.let { itemList.addAll(it) }
                    Log.d("HomeFragment", "Fetched items from API: ${response.body()}")
                    adapter.notifyDataSetChanged()
                } else {
                    showToast("Failed to load items")
                    Log.e("HomeFragment", "API response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<GuestDetailDto>>, t: Throwable) {
                showToast("Error: ${t.message}")
                Log.e("HomeFragment", "Error fetching data from API: ${t.message}", t)
            }
        })
    }

    private fun searchByEmbarkDate(mBarkDateString: String) {
        binding.sortByMbarkButton.isEnabled = false
        apiService.searchByEmbarkDate(mBarkDateString).enqueue(object : retrofit2.Callback<List<GuestDetailDto>> {
            override fun onResponse(call: retrofit2.Call<List<GuestDetailDto>>, response: retrofit2.Response<List<GuestDetailDto>>) {
                binding.sortByMbarkButton.isEnabled = true
                if (response.isSuccessful) {
                    itemList.clear()
                    response.body()?.let { itemList.addAll(it) }
                    Log.d("HomeFragment", "API response: ${response.body()}")
                    adapter.notifyDataSetChanged()
                    binding.mbarkDate.setText("select date")
                } else {
                    showToast("Failed to load items")
                    Log.e("HomeFragment", "API response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<GuestDetailDto>>, t: Throwable) {
                binding.sortByMbarkButton.isEnabled = true
                showToast("Error: ${t.message}")
                Log.e("HomeFragment", "API call failed: ${t.message}", t)
            }
        })
    }

    private fun searchByDebarkDate(dBarkDateString: String) {
        binding.sortByDbarkButton.isEnabled = false
        apiService.searchByDebarkDate(dBarkDateString).enqueue(object : retrofit2.Callback<List<GuestDetailDto>> {
            override fun onResponse(call: retrofit2.Call<List<GuestDetailDto>>, response: retrofit2.Response<List<GuestDetailDto>>) {
                binding.sortByDbarkButton.isEnabled = true
                if (response.isSuccessful) {
                    itemList.clear()
                    response.body()?.let { itemList.addAll(it) }
                    Log.d("HomeFragment", "API response: ${response.body()}")
                    adapter.notifyDataSetChanged()
                    binding.dbarkDate.setText("select date")
                } else {
                    showToast("Failed to load items")
                    Log.e("HomeFragment", "API response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<GuestDetailDto>>, t: Throwable) {
                binding.sortByDbarkButton.isEnabled = true
                showToast("Error: ${t.message}")
                Log.e("HomeFragment", "API call failed: ${t.message}", t)
            }
        })
    }

    private fun setupDatePickers() {
        // mBark Date Picker
        mBarkDateEditText = binding.mbarkDate
        mBarkDateEditText.setOnClickListener {
            showDatePickerDialog { year, month, dayOfMonth ->
                val selectedDate = formatDate(year, month, dayOfMonth)
                Log.d("HomeFragment", "mBark selected date: $selectedDate")
                mBarkDateEditText.setText(selectedDate)
            }
        }

        // dBark Date Picker
        dBarkDateEditText = binding.dbarkDate
        dBarkDateEditText.setOnClickListener {
            showDatePickerDialog { year, month, dayOfMonth ->
                val selectedDate = formatDate(year, month, dayOfMonth)
                Log.d("HomeFragment", "dBark selected date: $selectedDate")
                dBarkDateEditText.setText(selectedDate)
            }
        }
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        // Format the date as YYYY-MM-DD
        return String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
    }

    private fun showDatePickerDialog(onDateSet: (Int, Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Show the date picker dialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                onDateSet(selectedYear, selectedMonth, selectedDay)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}