package com.sko.manifestmanagement.Fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentChangeEmailBinding
import com.sko.manifestmanagement.model.ApiResponse
import com.sko.manifestmanagement.model.Crewoperator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeEmailFragment : Fragment(R.layout.fragment_change_email) {

    private var _binding: FragmentChangeEmailBinding? = null
    private val binding get() = _binding!!

    private var token: String? = null
    private var crewId: Int = 0
    private var currentEmailOfCrew: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentChangeEmailBinding.bind(view)

        // Retrieve the token from SharedPreferences or from arguments
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("auth_token", null)

        // Get the crewId passed from the previous fragment
        arguments?.let {
            crewId = it.getInt("crewId", 0)
            currentEmailOfCrew = it.getString("currentEmail", null)
        }
        binding.crewUsername.setText("Old " +currentEmailOfCrew)
        binding.crewUsername.isEnabled =false

        binding.saveEmail.setOnClickListener {
            val newEmail = binding.newEmail.text.toString().trim()
            val confirmEmail = binding.confirmEmail.text.toString().trim()

            if (newEmail.isNotEmpty() && confirmEmail.isNotEmpty()) {
                if (newEmail == confirmEmail) {
                    // Proceed with updating the email
                    if (isValidEmail(newEmail)) {
                        updateEmail(newEmail)  // Call the function with new email only
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please enter a valid email address.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // New email and confirm email do not match
                    Toast.makeText(
                        requireContext(),
                        "The new email and confirm email do not match.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Email fields are empty
                Toast.makeText(
                    requireContext(),
                    "Please fill both the new email and confirm email fields.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(Regex(emailPattern))
    }

    private fun updateEmail(newEmail: String) {
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)

        // Make sure the API call matches your API requirements.
        apiService.updateEmail(crewId, newEmail)
            .enqueue(object : Callback<ApiResponse<Crewoperator>> {
                override fun onResponse(
                    call: Call<ApiResponse<Crewoperator>>,
                    response: Response<ApiResponse<Crewoperator>>
                ) {
                    if (response.isSuccessful) {
                        // Successfully updated email
                        Toast.makeText(
                            requireContext(),
                            "Email updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().navigateUp()  // Navigate back to the profile
                    } else {
                        // Handle errors such as email already taken
                        val errorMessage = response.errorBody()?.string()
                        if (errorMessage != null && errorMessage.contains("The email is already in use")) {
                            Toast.makeText(
                                requireContext(),
                                "This email is already taken. Please try a different one.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "An unknown error occurred.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Crewoperator>>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Request failed. Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

