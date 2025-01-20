//
//
package com.sko.manifestmanagement.Fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.sko.manifestmanagement.R
import com.sko.manifestmanagement.Retrofit.ApiServices
import com.sko.manifestmanagement.Retrofit.RetrofitInstance
import com.sko.manifestmanagement.databinding.FragmentEditProfileBinding
import com.sko.manifestmanagement.model.CrewMember
import com.sko.manifestmanagement.model.UpdateCrewoperatorDTO
import com.sko.manifestmanagement.model.UpdateProfileImageDTO
import com.sko.manifestmanagement.utils.BitmapUtils
import com.sko.manifestmanagement.utils.BitmapUtils.encodeBitmapToBase64
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import android.os.Build

//
//
//class  EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
//
//    private var _binding: FragmentEditProfileBinding? = null
//    private val binding get() = _binding!!
//
//    private var token: String? = null
//    private var crewId: Int = 0
//    private val IMAGE_PICK_CODE = 1001
//    private val PERMISSION_CODE = 1002
//
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        _binding = FragmentEditProfileBinding.bind(view)
//
//        // Retrieve the token and crewId from SharedPreferences
//        val sharedPreferences =
//            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
//        token = sharedPreferences.getString("auth_token", null)
//
//        // Get crewId from arguments (if passed)
//        arguments?.let {
//            crewId = it.getInt("crewId", 0)
//        }
//
//        // Fetch crew details from the API
//        fetchCrewDetails()
//
//        binding.changeProfileImageButton.setOnClickListener {
//            openImagePicker()
//        }
//
//
//        // Save button to submit the updated profile
//        binding.saveButton.setOnClickListener {
//            val updatedCrewMember = formatDateForApi(binding.editDob.text.toString())?.let { formattedDob ->
//                UpdateCrewoperatorDTO(
//                    firstName = binding.editFirstName.text.toString(),
//                    lastName = binding.editLastName.text.toString(),
//                    contactNumber = binding.editContact.text.toString(),
//                    dob = formattedDob,
//                    gender = getSelectedGender()
//
//                )
//            }
//
//            // Validate the date of birth
//            if (updatedCrewMember?.dob != null) {
//                updateCrewMemberProfile(updatedCrewMember)
//            } else {
//                Toast.makeText(requireContext(), "Invalid date format. Please use yyyy-MM-dd.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//
//    // Fetch crew member details from the API and pre-fill the form
//    private fun fetchCrewDetails() {
//        token?.let { nonNullToken ->
//            val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
//
//            val call = apiService.getCrewByIdWithToken(crewId , "Bearer $nonNullToken")
//            call.enqueue(object : Callback<CrewMember> {
//                override fun onResponse(call: Call<CrewMember>, response: Response<CrewMember>) {
//                    if (response.isSuccessful) {
//                        val crewMember = response.body()
//                        crewMember?.let {
//                            binding.editFirstName.setText(it.firstName)
//                            binding.editLastName.setText(it.lastName)
//                            binding.editContact.setText(it.contactNumber)
//                            binding.editDob.setText(formatDobForDisplay(it.dob)) // Adjust the date format for display
//                            when (it.gender.trim().toLowerCase(Locale.getDefault())) {
//                                "male" -> binding.radioMale.isChecked = true
//                                "female" -> binding.radioFemale.isChecked = true
//                                "other" -> binding.radioOther.isChecked = true
//                                else -> Log.d("GenderCheck", "Unrecognized gender: ${it.gender}")
//                            }
//                            if (!it.crewImage.isNullOrEmpty()) {
//                                // Load Base64 string with Glide
//                                val base64Image = "data:image/png;base64,${it.crewImage}"
//                                Glide.with(requireContext())
//                                    .load(base64Image)
//                                    .placeholder(R.drawable.profile)  // Optional: placeholder
//                                    .into(binding.itemImage)  // Your ImageView
//                            } else {
//                                binding.itemImage.setImageResource(R.drawable.profile)
//                            }
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "Failed to fetch crew details", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<CrewMember>, t: Throwable) {
//                    Toast.makeText(requireContext(), "Error fetching crew details: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }
//
//    // Convert the entered date into the expected DateTime format (yyyy-MM-dd)
//    private fun formatDateForApi(dateString: String): String? {
//        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // User input format with separators (yyyy-MM-dd)
//        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())  // API expected format with separators (yyyy-MM-dd)
//
//        return try {
//            val date = inputFormat.parse(dateString)  // Parse the input date
//            date?.let { outputFormat.format(it) }  // Return formatted date with separators
//        } catch (e: ParseException) {
//            Log.e("DateParseError", "Error parsing date: ${e.message}")
//            null
//        }
//    }
//
//    // Get the selected gender from the radio group
//    private fun getSelectedGender(): String {
//        return when (binding.genderRadioGroup.checkedRadioButtonId) {
//            R.id.radioMale -> "Male"
//            R.id.radioFemale -> "Female"
//            R.id.radioOther -> "Other"
//            else -> "Male" // Default value
//        }
//    }
//
//    // Convert the API date format (yyyy-MM-dd) to display-friendly format (yyyy-MM-dd)
//    private fun formatDobForDisplay(dob: String?): String? {
//        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//        return try {
//            val date = inputFormat.parse(dob)  // Parse from the API format
//            date?.let { outputFormat.format(it) }  // Return formatted date for display
//        } catch (e: ParseException) {
//            Log.e("DateParseError", "Error parsing date: ${e.message}")
//            null
//        }
//    }
//
//    // Update the crew member profile
//    private fun updateCrewMemberProfile(updatedCrewDTO: UpdateCrewoperatorDTO) {
//        token?.let { nonNullToken ->
//            val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
//
//            val call = apiService.updateCrewDetails("Bearer $nonNullToken", crewId, updatedCrewDTO)
//            call.enqueue(object : Callback<CrewMember> {
//                override fun onResponse(call: Call<CrewMember>, response: Response<CrewMember>) {
//                    if (response.isSuccessful) {
//                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
//                        findNavController().navigate(R.id.action_editProfileFragment2_to_profileFragment)
//                    } else {
//                        Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<CrewMember>, t: Throwable) {
//                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }
//
//    private fun openImagePicker() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, IMAGE_PICK_CODE)
//    }
//
//
//    private fun pickImageFromGallery() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(intent, IMAGE_PICK_CODE)
//    }
//
//
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
//            val selectedImageUri = data?.data
//            if (selectedImageUri != null) {
//                // Display the selected image in the ImageView
//                binding.itemImage.setImageURI(selectedImageUri)
//                binding.itemImage.tag = selectedImageUri
//
//                // Convert the image to Base64 and upload it
//                val base64Image = convertImageToBase64(selectedImageUri)
//                if (base64Image != null) {
//                    updateProfileImage(base64Image) // Call the API directly
//                } else {
//                    Toast.makeText(requireContext(), "Error converting image to Base64", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//
//    private fun convertImageToBase64(imageUri: Uri): String? {
//        return try {
//            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            BitmapUtils.encodeBitmapToBase64(bitmap)
//        } catch (e: Exception) {
//            Log.e("ImageToBase64Error", "Error converting image to Base64: ${e.message}")
//            null
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, open the gallery
//                pickImageFromGallery()
//            } else {
//                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//
//
//
//
//    private fun uploadProfileImage() {
//        val selectedImageUri = binding.itemImage.tag as? Uri
//        if (selectedImageUri != null) {
//            val base64Image = convertImageToBase64(selectedImageUri)
//            if (base64Image != null) {
//                updateProfileImage(base64Image)
//            } else {
//                Toast.makeText(requireContext(), "Error converting image", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun updateProfileImage(base64Image: String) {
//        token?.let { nonNullToken ->
//            val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
//            val updateImageDTO = UpdateProfileImageDTO(imageBase64 = base64Image)
//
//            val call = apiService.updateProfileImage("Bearer $nonNullToken", crewId, updateImageDTO)
//            call.enqueue(object : Callback<Void> {
//                override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                    if (response.isSuccessful) {
//                        Toast.makeText(requireContext(), "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(requireContext(), "Failed to update profile picture: ${response.message()}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<Void>, t: Throwable) {
//                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//    }
//
//
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private var token: String? = null
    private var crewId: Int = 0

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEditProfileBinding.bind(view)

        // Initialize ActivityResultLaunchers
        setupActivityResultLaunchers()

        // Retrieve token and crewId from SharedPreferences and arguments
        val sharedPreferences =
            requireActivity().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("auth_token", null)
        arguments?.let {
            crewId = it.getInt("crewId", 0)
        }

        // Fetch crew details from the API
        fetchCrewDetails()

        // Change Profile Image Button
        binding.changeProfileImageButton.setOnClickListener {
            showImagePickerDialog()
        }

        // Save button to submit the updated profile
        binding.saveButton.setOnClickListener {
            val updatedCrewMember = formatDateForApi(binding.editDob.text.toString())?.let { formattedDob ->
                UpdateCrewoperatorDTO(
                    firstName = binding.editFirstName.text.toString(),
                    lastName = binding.editLastName.text.toString(),
                    contactNumber = binding.editContact.text.toString(),
                    dob = formattedDob,
                    gender = getSelectedGender()
                )
            }

            if (updatedCrewMember?.dob != null) {
                updateCrewMemberProfile(updatedCrewMember)
            } else {
                Toast.makeText(requireContext(), "Invalid date format. Please use yyyy-MM-dd.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCrewDetails() {
        token?.let { nonNullToken ->
            val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)

            val call = apiService.getCrewByIdWithToken(crewId, "Bearer $nonNullToken")
            call.enqueue(object : Callback<CrewMember> {
                override fun onResponse(call: Call<CrewMember>, response: Response<CrewMember>) {
                    if (response.isSuccessful) {
                        val crewMember = response.body()
                        crewMember?.let {
                            binding.editFirstName.setText(it.firstName)
                            binding.editLastName.setText(it.lastName)
                            binding.editContact.setText(it.contactNumber)
                            binding.editDob.setText(formatDobForDisplay(it.dob)) // Adjust the date format for display
                            when (it.gender.trim().toLowerCase(Locale.getDefault())) {
                                "male" -> binding.radioMale.isChecked = true
                                "female" -> binding.radioFemale.isChecked = true
                                "other" -> binding.radioOther.isChecked = true
                                else -> Log.d("GenderCheck", "Unrecognized gender: ${it.gender}")
                            }
                            if (!it.crewImage.isNullOrEmpty()) {
                                // Load Base64 string with Glide
                                val base64Image = "data:image/png;base64,${it.crewImage}"
                                Glide.with(requireContext())
                                    .load(base64Image)
                                    .placeholder(R.drawable.profile) // Optional: placeholder
                                    .into(binding.itemImage) // Your ImageView
                            } else {
                                binding.itemImage.setImageResource(R.drawable.profile)
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch crew details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CrewMember>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error fetching crew details: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun formatDateForApi(dateString: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) }
        } catch (e: ParseException) {
            Log.e("DateParseError", "Error parsing date: ${e.message}")
            null
        }
    }

    private fun getSelectedGender(): String {
        return when (binding.genderRadioGroup.checkedRadioButtonId) {
            R.id.radioMale -> "Male"
            R.id.radioFemale -> "Female"
            R.id.radioOther -> "Other"
            else -> "Male" // Default value
        }
    }

    private fun formatDobForDisplay(dob: String?): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return try {
            val date = inputFormat.parse(dob)
            date?.let { outputFormat.format(it) }
        } catch (e: ParseException) {
            Log.e("DateParseError", "Error parsing date: ${e.message}")
            null
        }
    }

    private fun updateCrewMemberProfile(updatedCrewDTO: UpdateCrewoperatorDTO) {
        token?.let { nonNullToken ->
            val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)

            val call = apiService.updateCrewDetails("Bearer $nonNullToken", crewId, updatedCrewDTO)
            call.enqueue(object : Callback<CrewMember> {
                override fun onResponse(call: Call<CrewMember>, response: Response<CrewMember>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_editProfileFragment2_to_profileFragment)
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CrewMember>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setupActivityResultLaunchers() {

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    // Validate image size and show confirmation dialog
                    showChangeProfileConfirmationDialog(selectedImageUri)
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pickImageFromGallery()
            } else {
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Choose Picture from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change Profile Picture")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // When "Choose Picture from Gallery" is clicked
                    checkAndRequestPermission()
                }
                1 -> {
                    // When "Cancel" is clicked, dismiss the dialog
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }



    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            pickImageFromGallery()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                Toast.makeText(requireContext(), "Permission is required to access the gallery", Toast.LENGTH_SHORT).show()
            }
            permissionLauncher.launch(permission)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun showChangeProfileConfirmationDialog(imageUri: Uri) {
        val imageSizeInKB = getImageSizeInKB(imageUri)

        if (imageSizeInKB > 60) {
            // Show an error message if the image size exceeds 60 KB
            Toast.makeText(requireContext(), "Image size should not exceed 60 KB", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Profile Picture Change")
        builder.setMessage("Do you want to change your profile picture?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            // If Yes is clicked, update the profile picture
            binding.itemImage.setImageURI(imageUri)
            binding.itemImage.tag = imageUri

            // Convert the image to Base64 and upload it
            val base64Image = convertImageToBase64(imageUri)
            if (base64Image != null) {
                updateProfileImage(base64Image)
            } else {
                Toast.makeText(requireContext(), "Error converting image to Base64", Toast.LENGTH_SHORT).show()
            }

            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun getImageSizeInKB(imageUri: Uri): Long {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val byteArray = inputStream?.readBytes()
            inputStream?.close()
            byteArray?.size?.div(1024L) ?: 0L // Convert bytes to KB
        } catch (e: Exception) {
            Log.e("ImageSizeError", "Error getting image size: ${e.message}")
            0L
        }
    }



// Update the image picker launcher to trigger the confirmation dialog


    private fun convertImageToBase64(imageUri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            BitmapUtils.encodeBitmapToBase64(bitmap)
        } catch (e: Exception) {
            Log.e("ImageToBase64Error", "Error converting image to Base64: ${e.message}")
            null
        }
    }

    private fun updateProfileImage(base64Image: String) {
        token?.let { nonNullToken ->
            val apiService = RetrofitInstance.retrofit.create(ApiServices::class.java)
            val updateImageDTO = UpdateProfileImageDTO(imageBase64 = base64Image)

            val call = apiService.updateProfileImage("Bearer $nonNullToken", crewId, updateImageDTO)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to update profile picture: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
