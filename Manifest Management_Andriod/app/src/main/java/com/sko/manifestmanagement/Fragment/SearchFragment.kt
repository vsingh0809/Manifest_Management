package com.sko.manifestmanagement.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sko.manifestmanagement.Adapter.GuestAdapter
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentRegisterBinding
import com.sko.manifestmanagement.databinding.FragmentSearchBinding
import com.sko.manifestmanagement.model.Guest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchBox: EditText
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GuestAdapter
    private var guestList: List<Guest> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }// Store the guest list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bind views
        searchBox = view.findViewById(R.id.searchBox)
        searchButton = view.findViewById(R.id.searchButton)
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView)

        // Initialize the adapter
        adapter = GuestAdapter(guestList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Handle the search button click
        searchButton.setOnClickListener {
            val searchQuery = searchBox.text.toString()
            performSearch(searchQuery)
        }
    }

    // Perform search by ID
    private fun performSearch(query: String) {
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
        apiService.getGuestById(query).enqueue(object : Callback<Guest> {
            override fun onResponse(call: Call<Guest>, response: Response<Guest>) {
                if (response.isSuccessful) {
                    val guest = response.body()
                    if (guest != null) {
                        // Wrap the guest into a list (as RecyclerView expects a list)
                        guestList = listOf(guest)
                        // Update the RecyclerView data
                        adapter.updateData(guestList)
                    } else {
                        Toast.makeText(requireContext(), "Guest not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Guest>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
