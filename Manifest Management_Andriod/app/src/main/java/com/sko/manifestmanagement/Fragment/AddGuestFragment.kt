package com.sko.manifestmanagement.Fragment

import android.R
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentAddGuestBinding
import com.sko.manifestmanagement.model.AddGuestDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddGuestFragment : Fragment() {

    private var _binding: FragmentAddGuestBinding? = null
    private val binding get() = _binding!!

    // Define gender values as an enum for better clarity
    enum class Gender(val value: Int) {
        MALE(0),
        FEMALE(1),
        OTHER(2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddGuestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the Salutation Spinner
        val salutations = arrayOf("Mr", "Mrs", "Miss")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, salutations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.salutationSpinner.adapter = adapter

        // Set click listener for submit button
        binding.submitButton.setOnClickListener {
            // Get values from input fields
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val mobileNo = binding.mobileNo.text.toString()
            if(mobileNo.length!=10){
                Toast.makeText(context, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = binding.email.text.toString()
            val boardingPoint = binding.boardingPoint.text.toString()
            val destinationPoint = binding.destinationPoint.text.toString()
            val selectedSalutation = binding.salutationSpinner.selectedItem.toString()
            val citizenship = binding.citizenship.text.toString()

            // Validate input fields
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) ||
                TextUtils.isEmpty(mobileNo) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(boardingPoint) || TextUtils.isEmpty(destinationPoint)) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get selected gender
            val selectedGenderId = binding.genderGroup.checkedRadioButtonId
            if (selectedGenderId == -1) {
                // No gender selected
                Toast.makeText(requireContext(), "Please select a gender", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val genderRadioButton = view.findViewById<RadioButton>(selectedGenderId)
            val gender = genderRadioButton?.text.toString()

            // Get current date and time
            val createdDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date())
            val lastUpdatedDate = createdDate
            val lastUpdatedBy = "someUser"  // You can replace with actual user info if needed

            // Create Guest object
            val guest = AddGuestDto(
                salutation = selectedSalutation,
                firstName = firstName,
                lastName = lastName,
                gender = gender,
                mobileNo = mobileNo,
                email = email,
                boardingPoint = boardingPoint,
                destinationPoint = destinationPoint,
                createdBy = lastUpdatedBy,
                createdDate = lastUpdatedBy,
                lastUpdatedBy = lastUpdatedBy,
                lastUpdatedDate = lastUpdatedDate,
                citizenship = citizenship
            )

            // Send guest data to API
            sendGuestDataToAPI(guest)
        }
    }

    private fun sendGuestDataToAPI(guest: AddGuestDto) {
        // Make the API call using Retrofit
        RetrofitInstance.retrofit.create(ApiServices::class.java)
            .addGuest(guest)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Guest added successfully", Toast.LENGTH_SHORT).show()
                        clearInputFields()
                    } else {
                        Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Failed to add guest: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun clearInputFields() {
        // Reset all fields to their default state
        binding.firstName.text?.clear()
        binding.lastName.text?.clear()
        binding.mobileNo.text?.clear()
        binding.email.text?.clear()
        binding.boardingPoint.text?.clear()
        binding.destinationPoint.text?.clear()
        binding.citizenship.text?.clear()

        // Reset the Salutation Spinner to its default selection
        binding.salutationSpinner.setSelection(0)  // Resets to "Mr" (first item in the list)

        // Reset the Gender RadioGroup to no selected option
        binding.genderGroup.clearCheck()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clean up the binding to avoid memory leaks
    }
}
