package com.sko.manifestmanagement.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentRegisterBinding
import com.sko.manifestmanagement.model.Crew
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private var isOtpVerified: Boolean = false
    private lateinit var gender: String



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Disable password field initially
        binding.password.isEnabled = false
        binding.emailOrCrewName.isEnabled=true
        // Handle OTP sending
        binding.sendOtpId.setOnClickListener {
            val email = binding.emailOrCrewName.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendOTP(email)
        }

        // Handle Register button click
        binding.registerButton.setOnClickListener {
            val password = binding.password.text.toString()
            val salutation = binding.salutation.text.toString()
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val emailOrCrewName = binding.emailOrCrewName.text.toString()
            val contactNumber = binding.contactNumber.text.toString()
            val dob = binding.dob.text.toString()
            val passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}\$".toRegex()

            if (!isOtpVerified) {
                Toast.makeText(requireContext(), "Please verify OTP first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(salutation.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                emailOrCrewName.isEmpty() || contactNumber.isEmpty() || dob.isEmpty() || password.isEmpty()){
                Toast.makeText(
                    requireContext(),
                    "Please enter all details",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else if(!password.matches(passwordRegex)) {
                Toast.makeText(requireContext(), "Password should contain at least one uppercase, one digit and one special character", Toast.LENGTH_SHORT)
                    .show()
            }else {
                registerUser()
            }
        }

        // Cancel button click listener
        binding.cancelButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_logInFragment)
        }
    }

    private fun sendOTP(email: String) {
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)

        apiService.sendOtp(email).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP sent successfully!", Toast.LENGTH_SHORT).show()
                    promptOtpVerification(email)
                } else {
                    Toast.makeText(requireContext(), "Please enter valid email: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Failed to send OTP: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun promptOtpVerification(email: String) {
        val otpDialog = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_verify_otp, null)
        val otpEditText = otpDialog.findViewById<EditText>(R.id.otpInput)
        val submitButton = otpDialog.findViewById<Button>(R.id.submitOtp)

        val otpAlertDialog = android.app.AlertDialog.Builder(requireContext())
            .setView(otpDialog)
            .setCancelable(true)
            .create()

        submitButton.setOnClickListener {
            val enteredOtp = otpEditText.text.toString().trim()
            if (enteredOtp.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call API to verify OTP
            verifyOtp(email, enteredOtp, otpAlertDialog)
        }
        otpAlertDialog.show()
    }

    private fun verifyOtp(email: String, otp: String, dialog: android.app.AlertDialog) {
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
        //val otpRequest = mapOf("email" to email, "otp" to otp)
        val otpRequest = mapOf("email" to email, "otp" to otp)

        apiService.verifyOtp(otpRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP Verified Successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                        isOtpVerified=true
                    binding.sendOtpId.visibility=View.GONE
                                binding.password.isEnabled = true
                } else {
                    Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
        binding.emailOrCrewName.isEnabled=false
    }

    private fun registerUser() {
        val firstName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        val email = binding.emailOrCrewName.text.toString()
        val contactNumber = binding.contactNumber.text.toString()
        val dobString = binding.dob.text.toString()
        val password = binding.password.text.toString()
        val salutation = binding.salutation.text.toString()

        val genderMale = binding.genderMale
        val genderFemale = binding.genderFemale
        val other = binding.genderOther

        if(genderMale.isChecked)
        {
            gender=genderMale.text.toString()
        }
        else if(genderFemale.isChecked)
        {
            gender=genderFemale.text.toString()
        }
        else
        {
            gender=other.text.toString()
        }

        // Prepare Crew object
        val crew = Crew(
            crewId = 0,
            salutation = salutation,
            firstName = firstName,
            lastName = lastName,
            email = email,
            contactNumber = contactNumber,
            dob = convertDateToISOFormat(dobString),
            gender = gender,
            password = password,
            crewUsername = firstName+lastName,
            crewImage = null
        )

        // Make API call
        RetrofitInstance.retrofit.create(ApiServices::class.java)
            .registerCrew(crew).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()
                        view?.findNavController()?.navigate(R.id.action_registerFragment_to_logInFragment)
                    } else {
                        Toast.makeText(requireContext(), "Please enter valid information", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to register: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun convertDateToISOFormat(dobString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(dobString)
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US).format(date ?: Date())
        } catch (e: Exception){
            e.printStackTrace()
            ""
        }
    }
}


