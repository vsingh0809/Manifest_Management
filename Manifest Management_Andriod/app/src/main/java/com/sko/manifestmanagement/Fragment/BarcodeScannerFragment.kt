package com.sko.manifestmanagement.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BarcodeScannerFragment : Fragment() {

    private val args: BarcodeScannerFragmentArgs by navArgs()
    private var hasScanned: Boolean = false // Flag to track scanning state

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (!hasScanned) { // Start scanner only if no scan has occurred
            startBarcodeScanner()
        }
    }

    private fun startBarcodeScanner() {
        IntentIntegrator.forSupportFragment(this).apply {
            setPrompt("Scan to: ${args.guestCheckInStatus}")
            setBeepEnabled(true)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setCameraId(0)
            initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        Log.d("BarcodeScannerFragment", "result content: ${result.contents}")

        result.contents?.let {
            hasScanned = true // Set the flag to true after a successful scan
            Toast.makeText(context, "${args.guestCheckInStatus} Scan Success: $it", Toast.LENGTH_LONG).show()
            when (args.guestCheckInStatus) {
                "check-in" -> checkInGuest(it.toInt())
                "check-out" -> checkOutGuest(it.toInt())
            }
        } ?: run {
            Toast.makeText(context, "Scan Failed: No barcode detected", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkInGuest(userId: Int) = makeApiCall(userId, "Check In Successful!", "User ID does not exist.")

    private fun checkOutGuest(userId: Int) = makeApiCall(userId, "Check Out Successful!", "User ID does not exist.")

    private fun makeApiCall(userId: Int, successMessage: String, failureMessage: String) {
        val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
        val call = if (successMessage.contains("Check In")) {
            apiService.checkInGuest(userId)
        } else {
            apiService.checkOutGuest(userId)
        }

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                context?.let {
                    when {
                        response.isSuccessful -> {
                            Toast.makeText(it, successMessage, Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                        response.code() == 404 -> { // Guest not found
                            Toast.makeText(it, "Guest with ID $userId not found.", Toast.LENGTH_SHORT).show()
                        }
                        response.code() == 400 -> { // Guest deleted or already checked in
                            val errorMessage = response.errorBody()?.string() ?: failureMessage
                            Toast.makeText(it, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        else -> { // Other unexpected errors
                            Log.d("BarcodeScannerFragment", "Error: ${response.message()}")
                            Toast.makeText(it, "Unexpected error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                context?.let {
                    Toast.makeText(it, "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}
