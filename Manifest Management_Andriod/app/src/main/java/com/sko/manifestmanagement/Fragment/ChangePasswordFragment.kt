package com.sko.manifestmanagement.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.auth0.android.jwt.JWT
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentChangePasswordBinding
import com.sko.manifestmanagement.model.LoginRequest
import com.sko.manifestmanagement.model.UpdateRequest
import com.sko.manifestmanagement.model.VerifyPassword
import com.sko.manifestmanagement.utils.TokenUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class ChangePasswordFragment : Fragment() {
    lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            if (isTokenExpired(token)) {
                Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
                return
            }

            // val crewId = TokenUtils.extractIdFromToken(token)
            val crewId = TokenUtils.extractIdFromToken(token, "Id")

            if (crewId != null) {

            } else {
                Toast.makeText(requireContext(), "Invalid token. Please login again.", Toast.LENGTH_SHORT).show()
            }

            binding.newPasword.isVisible = false
            binding.confirmPasword.isVisible = false
            binding.savePassword.isVisible = false
            binding.resetPassword.isVisible = false
            var oldPassword: String

            binding.verifyOldPassword.setOnClickListener {
                oldPassword = binding.oldPasword.text.toString()
                Log.d("OldPassword", "${oldPassword}")

                val verifyPassword = VerifyPassword(crewId, oldPassword)
                verifyOldPassword(verifyPassword)
            }

            binding.savePassword.setOnClickListener{
                oldPassword = binding.oldPasword.text.toString()
                val passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}\$".toRegex()
                val newPassword = binding.newPasword.text.toString()
                val confirmNewPassword = binding.confirmPasword.text.toString()

                if(newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter both newPassword and confirmNewPassword",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if(!newPassword.matches(passwordRegex) || !confirmNewPassword.matches(passwordRegex)) {
                    Toast.makeText(requireContext(), "Password should contain at least one uppercase, one digit and one special character", Toast.LENGTH_SHORT)
                        .show()
                } else if (oldPassword == newPassword) {
                    Toast.makeText(
                        requireContext(),
                        "Old Password and new Password are same please change new password",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (oldPassword == confirmNewPassword) {
                    Toast.makeText(
                        requireContext(),
                        "Old Password and confirm new Password are same please change confirm new password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (newPassword == confirmNewPassword) {
                    updatePassword(crewId, newPassword)
                    findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "New Password and Confirm New Password do not match. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }else{
            Toast.makeText(requireContext(), "No token found. Please login first.", Toast.LENGTH_SHORT).show()
        }

        binding.resetPassword.setOnClickListener{
            binding.newPasword.setText("")
            binding.confirmPasword.setText("")
        }
    }

    private fun verifyOldPassword(verifyPassword: VerifyPassword){
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)

        // Call the updatePassword API
        apiService.verifyOldPassword(verifyPassword).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    // Show success message
                    Toast.makeText(
                        requireContext(),
                        "Password verified successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    binding.oldPasword.isEnabled = false
                    binding.verifyOldPassword.isEnabled = false
                    binding.newPasword.isVisible = true
                    binding.confirmPasword.isVisible = true
                    binding.savePassword.isVisible = true
                    binding.resetPassword.isVisible = true

                } else {
                    // Handle error, show response code or error message
                    Toast.makeText(
                        requireContext(),
                        "Password is wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle network failure or exception
                Toast.makeText(
                    requireContext(),
                    "Failed to verify password: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updatePassword(id: Int?,newPassword: String) {
        // Create the Retrofit instance for ApiServices
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)


        // Call the updatePassword API
        apiService.updatePassword(UpdateRequest(id,newPassword)).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    // Show success message
                    Toast.makeText(
                        requireContext(),
                        "Password updated successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().navigate(R.id.action_changePasswordFragment_to_loginFragment)

                } else {
                    // Handle error, show response code or error message
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle network failure or exception
                Toast.makeText(
                    requireContext(),
                    "Failed to update password: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
}
