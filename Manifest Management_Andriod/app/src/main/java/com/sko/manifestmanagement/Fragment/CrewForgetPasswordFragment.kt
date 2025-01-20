package com.sko.manifestmanagement.Fragment

import android.content.Context
import android.os.Bundle
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
import com.sko.manifestmanagement.databinding.FragmentCrewForgetPasswordBinding
import com.sko.manifestmanagement.model.UpdateRequest
import com.sko.manifestmanagement.utils.TokenUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class CrewForgetPasswordFragment : Fragment() {
    lateinit var binding: FragmentCrewForgetPasswordBinding
    private var isOtpVerified: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrewForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.enterOtp.isVisible = false
        binding.verifyOtp.isVisible = false
        binding.resendOtp.isVisible = false
        //binding.newPassword.isVisible=false
        //binding.confirmPassword.isVisible=false
        //binding.savePasswordButton.isVisible=false
        //binding.resetPasswordButton.isVisible=false

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

            binding.savePasswordButton.setOnClickListener {
                val email = binding.emailOrUsername.text.toString()
                val passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}\$".toRegex()
                val newPassword = binding.newPassword.text.toString()
                val confirmNewPassword =binding.confirmPassword.text.toString()

                if(newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please enter both newPassword and confirmNewPassword",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if(newPassword==confirmNewPassword) {
                    updatePassword(crewId, newPassword)
                }else if(newPassword!=confirmNewPassword){
                    Toast.makeText(requireContext(), "Passwords do not match. Please try again.", Toast.LENGTH_SHORT).show()
                }else if(!newPassword.matches(passwordRegex) || !confirmNewPassword.matches(passwordRegex)){
                Toast.makeText(requireContext(), "Password should contain at least one uppercase, one digit and one special character", Toast.LENGTH_SHORT)
                    .show()
            }
            }
        } else {
            Toast.makeText(requireContext(), "No token found. Please login first.", Toast.LENGTH_SHORT).show()
        }

        binding.emailOrUsername.requestFocus()
        binding.sendOtp.setOnClickListener {
            val email=binding.emailOrUsername.text.toString()
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_SHORT)
                    .show()
            }
            else if(!email.matches(emailRegex)){
                Toast.makeText(requireContext(), "Please enter valid email", Toast.LENGTH_SHORT)
                    .show()
            }
            else {
                binding.enterOtp.isVisible = true
                binding.verifyOtp.isVisible = true
            }
            sendOTP(email)
        }

        binding.verifyOtp.setOnClickListener {
            if (binding.enterOtp.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val email=binding.emailOrUsername.text.toString()
            val enteredOtp = binding.enterOtp.text.toString().trim()
            verifyOtp(email, enteredOtp)
        }


        binding.resendOtp.setOnClickListener {
            val email=binding.emailOrUsername.text.toString()
            sendOTP(email)
        }

        binding.resetPasswordButton.setOnClickListener{
            binding.newPassword.setText("")
            binding.confirmPassword.setText("")
        }
    }

    private fun sendOTP(email: String) {
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)


        apiService.sendOtp(email).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "OTP sent successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.emailOrUsername.isEnabled=false
                    binding.sendOtp.isEnabled=false
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Failed to send OTP: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun verifyOtp(email: String, otp: String) {
        binding.enterOtp.isVisible=true
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
        //val otpRequest = mapOf("email" to email, "otp" to otp)
        val otpRequest = mapOf("email" to email, "otp" to otp)

        apiService.verifyOtp(otpRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "OTP Verified Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    isOtpVerified = true
                    // Continue the flow when OTP is entered
                    binding.verifyOtp.isEnabled = false
                    binding.enterOtp.isEnabled=false
                    binding.newPassword.isVisible = true
                    binding.confirmPassword.isVisible = true
                    binding.savePasswordButton.isVisible = true
                    binding.resetPasswordButton.isVisible = true
                } else {
                    binding.resendOtp.isEnabled = true
                    Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
    private fun updatePassword(id: Int?, newPassword: String) {
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
                    findNavController().navigate(R.id.action_crewForgetPasswordFragment_to_logInFragment)
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
