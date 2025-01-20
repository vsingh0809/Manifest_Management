package com.sko.manifestmanagement.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentEditGuestBinding
import com.sko.manifestmanagement.model.Guest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditGuestFragment : Fragment() {

    private var guestId: Int? = null
    private lateinit var binding: FragmentEditGuestBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditGuestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        guestId = arguments?.getInt("guestId")
        Log.d("EditGuestFragment", "Guest ID: $guestId")

        guestId?.let {
            fetchGuestDetails(it)
        }

        binding.submitButton.setOnClickListener {
            updateGuestDetails()
        }
    }

    // Fetch guest details using the API
    private fun fetchGuestDetails(id: Int) {

        RetrofitInstance.retrofit.create(ApiServices::class.java).getGuestById(id.toString()).enqueue(object : Callback<Guest> {
            override fun onResponse(call: Call<Guest>, response: Response<Guest>) {
                if (response.isSuccessful) {
                    response.body()?.let { guest ->
                        bindGuestData(guest)
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch guest details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Guest>, t: Throwable) {
                Log.e("EditGuestFragment", "Error: ${t.message}")
                Toast.makeText(context, "Error fetching guest details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Bind guest data to the form
    private fun bindGuestData(guest: Guest) {
        binding.firstName.setText(guest.firstName)
        binding.lastName.setText(guest.lastName)
        binding.mobileNo.setText(guest.mobileNo)
        binding.emailOrCrewName.setText(guest.email)
        binding.citizenship.setText(guest.citizenship)
        binding.boardingPoint.setText(guest.boardingPoint)
        binding.destinationPoint.setText(guest.destinationPoint)

        // Set gender radio buttons
        when (guest.gender) {
            "Male" -> binding.radioMale.isChecked = true
            "Female" -> binding.radioFemale.isChecked = true
            else -> binding.radioOther.isChecked = true
        }
    }

    // Update guest details
    private fun updateGuestDetails() {
        val updatedGuest = Guest(
            firstName = binding.firstName.text.toString(),
            lastName = binding.lastName.text.toString(),
            gender = getSelectedGender(),
            mobileNo = binding.mobileNo.text.toString(),
            email = binding.emailOrCrewName.text.toString(),
            citizenship = binding.citizenship.text.toString(),
            boardingPoint = binding.boardingPoint.text.toString(),
            destinationPoint = binding.destinationPoint.text.toString()
        )

        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
        guestId?.let {
            apiService.updateGuest(it, updatedGuest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Guest updated successfully", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()

                    } else {
                        Toast.makeText(context, "Failed to update guest", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("EditGuestFragment", "Error: ${t.message}")
                    Toast.makeText(context, "Error updating guest", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    // Helper method to get selected gender from the radio buttons
    private fun getSelectedGender(): String {
        return when (binding.genderRadioGroup.checkedRadioButtonId) {
            R.id.radioMale -> "Male"
            R.id.radioFemale -> "Female"
            else -> "Other"
        }
    }
}

