package com.sko.manifestmanagement.Fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentGuestProfileBinding
import com.sko.manifestmanagement.model.Guest
import com.sko.manifestmanagement.model.GuestProfileDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GuestProfile : Fragment() {
        private var _binding: FragmentGuestProfileBinding? = null
        private val binding get() = _binding!!
        private var guestId: Int? = null
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val navController=findNavController()
            _binding = FragmentGuestProfileBinding.inflate(inflater, container, false)
            return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController=findNavController()
        guestId = arguments?.getInt("guestId")
        Log.d("EditGuestFragment", "Guest ID: $guestId")

        guestId?.let {
            fetchGuestDetails(it)
        }


        binding.editButton.setOnClickListener {
            showConfirmationDialog("Edit")
        }

        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to Delete this item?")
                .setPositiveButton("Yes") { _, _ ->
                    guestId?.let { it1 -> deleteGuest(it1)}
                        // Trigger the confirmation action (e.g., navigate to edit page)
                }
                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        binding.checkInButton.setOnClickListener {
            val action = GuestProfileDirections.actionGuestProfileToBarcodeScannerFragment("check-in")
            findNavController().navigate(action)
//            findNavController().navigate(R.id.action_guestProfile_to_barcodeScannerFragment)
        }

        binding.checkOutButton.setOnClickListener {
            val action = GuestProfileDirections
                .actionGuestProfileToBarcodeScannerFragment("check-out")
            findNavController().navigate(action)
//            findNavController().navigate(R.id.action_guestProfile_to_barcodeScannerFragment)
        }


    }

    private fun showConfirmationDialog(action: String) {
        AlertDialog.Builder(context)
            .setTitle("$action Confirmation")
            .setMessage("Are you sure you want to $action this item?")
            .setPositiveButton("Yes") { _, _ ->
                val action = guestId?.let { it1 ->
                    GuestProfileDirections.actionGuestProfileToEditGuestFragment(
                        it1
                    )
                }

                if (action != null) {
                    val navController = findNavController()
                    navController.navigate(action)
                }// Trigger the confirmation action (e.g., navigate to edit page)
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Fetch guest details using the API
    private fun fetchGuestDetails(id: Int) {

        RetrofitInstance.retrofit.create(ApiServices::class.java).getGuestByIDtoProfile(id.toString()).enqueue(object :
            Callback<GuestProfileDTO> {
            override fun onResponse(call: Call<GuestProfileDTO>, response: Response<GuestProfileDTO>) {
                if (response.isSuccessful) {
                    response.body()?.let { guest ->
                        bindGuestData(guest,id)
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch guest details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GuestProfileDTO>, t: Throwable) {
                Log.e("EditGuestFragment", "Error: ${t.message}")
                Toast.makeText(context, "Error fetching guest details", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Bind guest data to the form
    private fun bindGuestData(guest: GuestProfileDTO, id: Int) {
        binding.guestId.setText("ID: " +id.toString())
        binding.firstName.setText("Name: " +guest.firstName)
        binding.lastName.setText(guest.lastName)
        binding.mobileNo.setText("Mobile Number: " +guest.mobileNo)
        binding.email.setText("Email: "+guest.email)
        binding.citizenship.setText("Citizenship: " +guest.citizenship)
        binding.boardingPoint.setText("Boarding Point: " +guest.boardingPoint)
        binding.destinationPoint.setText("Destination Point: " +guest.destinationPoint)
        binding.gender.setText("Gender: " +guest.gender)

        if(guest.mBarkDate.isNullOrBlank() && guest.dBarkDate.isNullOrBlank()){
            binding.status.setText("Pending")
        }else if(guest.mBarkDate.isNotEmpty() && guest.dBarkDate.isNullOrBlank()){
            binding.status.setText("Checked-In")
        }else{
            binding.status.setText("Checked-Out")
        }

        when(binding.status.text){
            "Pending" -> binding.checkOutButton.isEnabled = false;
            "Checked-In" -> binding.checkInButton.isEnabled = false;
            "Checked-Out" ->{
                binding.checkInButton.isEnabled = false
                binding.checkOutButton.isEnabled = false
            }

        }
    }

    private fun deleteGuest(id: Int) {
        var apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
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
                    findNavController().popBackStack()
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
}