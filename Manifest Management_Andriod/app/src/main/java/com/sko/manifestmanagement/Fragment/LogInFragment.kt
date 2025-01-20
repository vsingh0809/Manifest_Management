package com.sko.manifestmanagement.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.model.LoginRequest
import com.sko.manifestmanagement.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment: Fragment() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var forgot:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_log_in, container, false)

        // Initialize the views using findViewById
        emailEditText = view.findViewById(R.id.emailOrCrewName)
        passwordEditText = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.login)
        registerButton=view.findViewById(R.id.register)
        forgot=view.findViewById(R.id.forgotPassword)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

        forgot.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_crewForgetPasswordFragment)
        }

        // Set onClickListener for login button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(email, password)
            loginUser(loginRequest)
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
        }
        return view
    }

    // Function to call the login API
    private fun loginUser(loginRequest: LoginRequest) {
        // Call the API using Retrofit
        RetrofitInstance.retrofit.create(ApiServices::class.java).loginCrew(loginRequest)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        loginResponse?.let {
                            Log.d("LoginFragment", "Token from server: ${it.Token}") // Log the received token
                            saveToken(it.Token)  // Save the token to SharedPreferences
                            Toast.makeText(requireContext(), "Login successfully", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_logInFragment_to_workingActivity3)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please enter valid credentials", Toast.LENGTH_SHORT).show()
                        Log.e("LoginFragment", "Login Failed: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Login Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // Function to save the token (implement this based on your storage mechanism)
    private fun saveToken(token: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        // Clear any existing token before saving the new one
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
        Log.d("LoginFragment", "Token saved: $token") // Log to verify
    }
}
