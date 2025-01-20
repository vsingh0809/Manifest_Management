package com.sko.manifestmanagement.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.bumptech.glide.Glide
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentProfileBinding
import com.sko.manifestmanagement.model.CrewMember
import com.sko.manifestmanagement.utils.TokenUtils.extractIdFromToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        token?.let { nonNullToken ->
            if (isTokenExpired(nonNullToken)) {
                Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                return
            }

            val crewId = extractIdFromToken(nonNullToken, "Id")

            if (crewId != null) {
                fetchCrewData(crewId, nonNullToken)
            } else {
                Toast.makeText(requireContext(), "Invalid token. Please login again.", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(requireContext(), "No token found. Please login first.", Toast.LENGTH_SHORT).show()
        }

        // Navigate to Change Password Screen
        binding.changePasswordButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

        // Navigate to Logout Button
        binding.logoutButton.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        // Navigate to Edit Profile
//        binding.editProfileButton.setOnClickListener {
//            val bundle = Bundle().apply {
//                val crewId = extractIdFromToken(token ?: "", "Id") ?: 0
//                putInt("crewId", crewId)
//
//                val fullName = binding.itemName.text.toString()
//                val nameParts = fullName.split(" ")
//
//                putString("firstName", nameParts.getOrNull(0) ?: "")  // Default to empty
//                putString("lastName", nameParts.getOrNull(1) ?: "")   // Default to empty
//                val contactNumber = binding.itemContact.text.toString().replace("Contact Number :- ", "").trim()
//                putString("contact", contactNumber)
//
//                // Extract and format DOB
//                val dobWithLabel = binding.itemDob.text.toString()
//                val dob = dobWithLabel.replace("Date of Birth: ", "").split("T")[0]
//                putString("dob", dob)
//                // Extract gender
//                val gender = binding.itemGender.text.toString().replace("Gender :- ", "").trim() // Adjust this if needed
//                putString("gender", gender)
//
//                val crewImage = binding.itemImage.tag?.toString()  // Ensure Base64 is stored in tag
//                if (!crewImage.isNullOrEmpty()) {
//                    putString("imageBase64", crewImage.toString())
//                    Log.d("hhhh",crewImage)
//                } else {
//                    Log.d("ImagePassing", "crewImage is null or empty in ProfileFragment.")
//                }
//
//
//            }
//            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment2, bundle)
//        }

        // Navigate to Edit Profile
        binding.editProfileButton.setOnClickListener {
            val crewId = extractIdFromToken(token ?: "", "Id") ?: 0
            val bundle = Bundle().apply {
                putInt("crewId", crewId)
            }
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment2, bundle)
        }


        binding.changeEmailButton.setOnClickListener {
            // Ensure token is not null and handle null case
            val crewId = extractIdFromToken(token ?: "", "Id") ?: 0
            val currentEmail = binding.itemEmail.text.toString().replace("Email:- ", "").trim()

            // Prepare bundle with crew data
            val bundle = Bundle().apply {
                putInt("crewId", crewId)
                putString("currentEmail", currentEmail)
            }

            // Navigate to ChangeEmailFragment with the data
            findNavController().navigate(R.id.action_profileFragment_to_changeEmailFragment, bundle)
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val jwt = JWT(token)
            val expiration = jwt.expiresAt
            expiration != null && expiration.before(Date())
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }



    private fun fetchCrewData(crewId: Int, token: String) {
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
        val call = apiService.getCrewByIdWithToken(crewId, "Bearer $token")

        call.enqueue(object : Callback<CrewMember> {
            override fun onResponse(call: Call<CrewMember>, response: Response<CrewMember>) {
                if (response.isSuccessful) {
                    val crewMember = response.body()
                    crewMember?.let {
                        binding.itemId.text = "Id: ${it.crewId}"
                        binding.itemName.text = "${it.firstName} ${it.lastName}"
                        binding.itemEmail.text = "Email: ${it.email}"
                        binding.itemContact.text = "Contact Number: ${it.contactNumber}"
                        binding.itemDob.text = "Date of Birth: ${it.dob?.split("T")?.get(0) ?: "Not Available"}"
                        binding.itemGender.text = "Gender: ${it.gender ?: "Not Available"}"

                        // Check if image exists (Base64 encoded)
                        if (!it.crewImage.isNullOrEmpty()) {
                            // Load Base64 string with Glide
                            val base64Image = "data:image/png;base64,${it.crewImage}"
                            Glide.with(requireContext())
                                .load(base64Image)
                                .placeholder(R.drawable.profile)  // Optional: placeholder
                                .into(binding.itemImage)  // Your ImageView
                        } else {
                            binding.itemImage.setImageResource(R.drawable.profile)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CrewMember>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

