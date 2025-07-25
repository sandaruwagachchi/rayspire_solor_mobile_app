package com.example.pizzazone

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    // UI elements
    private lateinit var greetingText: TextView
    private lateinit var textViewProfileName: TextView
    private lateinit var textViewProfileEmail: TextView
    private lateinit var logoutButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var themeToggleButton: ImageView // Renamed from leftToRightImage for clarity
    private lateinit var profileImage: ShapeableImageView
    private lateinit var cameraIcon: ImageView


    // ActivityResultLauncher for taking a picture from the camera

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {

                profileImage.setImageBitmap(it) // Set the taken picture to profileImage
                Toast.makeText(requireContext(), "Picture taken. Uploading...", Toast.LENGTH_SHORT).show()
                uploadImageToFirebaseStorage(it) // Upload to Firebase Storage
            }
        } else {
            Toast.makeText(requireContext(), "Picture taking cancelled or failed.", Toast.LENGTH_SHORT).show()
        }
    }

    // ActivityResultLauncher for requesting camera permission

                profileImage.setImageBitmap(it)
                Toast.makeText(requireContext(), "Taked picture, Please wait", Toast.LENGTH_SHORT).show()
                uploadImageToFirebaseStorage(it)
            }
        } else {
            Toast.makeText(requireContext(), "Failed to take a picture", Toast.LENGTH_SHORT).show()
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {

            Toast.makeText(requireContext(), "Camera permission is required to take a photo.", Toast.LENGTH_LONG).show()

            Toast.makeText(requireContext(), "We need to permission for take a picture.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        greetingText = view.findViewById(R.id.greetingText)
        textViewProfileName = view.findViewById(R.id.textViewProfileName)
        textViewProfileEmail = view.findViewById(R.id.textViewProfileEmail)
        logoutButton = view.findViewById(R.id.buttonLogout)
        backArrow = view.findViewById(R.id.backArrow)
        themeToggleButton = view.findViewById(R.id.leftToRight) // ID remains 'leftToRight' from XML
        profileImage = view.findViewById(R.id.profileImage)
        cameraIcon = view.findViewById(R.id.cameraIcon)



        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }


        // --- Dark/Light Mode Toggle Logic ---
        themeToggleButton.setOnClickListener {


        leftToRightImage.setOnClickListener {

            toggleAppTheme()
        }


        // --- Camera Icon Click Listener ---

        cameraIcon.setOnClickListener {
            checkCameraPermission()
        }

        loadUserProfile()

        return view
    }

    private fun loadUserProfile() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val userRef = database.getReference("Customers").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val customer = snapshot.getValue(Customer::class.java)
                        customer?.let {
                            greetingText.text = "Hi, ${it.name}!"
                            textViewProfileName.text = it.name
                            textViewProfileEmail.text = it.email


                            // Load profileImageUrl from Firebase Database
                            val profileImageUrl = it.profileImageUrl
                            if (!profileImageUrl.isNullOrEmpty()) {
                                // Load image using Glide library
                                Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.dummy_profile_image) // default image
                                    .error(R.drawable.dummy_profile_image) // image to show on error
                                    .into(profileImage)
                            } else {


                            val profileImageUrl = it.profileImageUrl
                            if (!profileImageUrl.isNullOrEmpty()) {

                                Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.dummy_profile_image)
                                    .error(R.drawable.dummy_profile_image)
                                    .into(profileImage)
                            } else {


                                profileImage.setImageResource(R.drawable.dummy_profile_image)
                            }
                        }
                    } else {

                        Toast.makeText(requireContext(), "User data not found in database.", Toast.LENGTH_SHORT).show()
                        // Show default image if data is missing

                        Toast.makeText(requireContext(), "Not defined data.", Toast.LENGTH_SHORT).show()

 
                        profileImage.setImageResource(R.drawable.dummy_profile_image)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(requireContext(), "Failed to load profile: ${error.message}", Toast.LENGTH_SHORT).show()

                    Toast.makeText(requireContext(), "fail to load profile: ${error.message}", Toast.LENGTH_SHORT).show()

                    error.toException().printStackTrace()
                }
            })
        } else {

            Toast.makeText(requireContext(), "No user logged in. Please log in.", Toast.LENGTH_SHORT).show()

            Toast.makeText(requireContext(), "No user data .Please login first.", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }


    private fun toggleAppTheme() {
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_NO)

                Toast.makeText(requireContext(), "Switched to Light Mode", Toast.LENGTH_SHORT).show()

                Toast.makeText(requireContext(), "Changed Light Mode", Toast.LENGTH_SHORT).show()

            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)

                Toast.makeText(requireContext(), "Switched to Dark Mode", Toast.LENGTH_SHORT).show()

                Toast.makeText(requireContext(), "Changed Dark Mode", Toast.LENGTH_SHORT).show()

            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)

                Toast.makeText(requireContext(), "Switched to Dark Mode", Toast.LENGTH_SHORT).show()

                Toast.makeText(requireContext(), "Changed dark mode", Toast.LENGTH_SHORT).show()

            }
        }
        requireActivity().recreate() // Recreate activity to apply theme changes immediately
    }


    private fun saveThemePreference(mode: Int) {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("theme_mode", mode)
            apply()
        }
    }

    private fun loadThemePreference(): Int {
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return sharedPref.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }


    // --- Camera permission and opening camera functions ---


    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {

                Toast.makeText(requireContext(), "We need camera permission to take your profile picture.", Toast.LENGTH_LONG).show()

                Toast.makeText(requireContext(), "We need to permission take a picture.", Toast.LENGTH_LONG).show()

                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            takePictureLauncher.launch(takePictureIntent)
        } else {

            Toast.makeText(requireContext(), "No camera app found.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Function to upload image to Firebase Storage ---
    private fun uploadImageToFirebaseStorage(bitmap: Bitmap) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to upload picture.", Toast.LENGTH_SHORT).show()

            Toast.makeText(requireContext(), "Not found camera app.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadImageToFirebaseStorage(bitmap: Bitmap) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please login to take a picture.", Toast.LENGTH_SHORT).show()

            return
        }

        val userId = currentUser.uid

        // Create a file in the "profile_images" folder named after the user's UID
        val profileImageRef = storageRef.child("profile_images/$userId.jpg")

        // Convert Bitmap to ByteArray
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // 100% quality
        val data = baos.toByteArray()

        // Upload Task
        val uploadTask = profileImageRef.putBytes(data)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Upload successful, get download URL
            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                // Update user data in Realtime Database


        val profileImageRef = storageRef.child("profile_images/$userId.jpg")


        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()


        val uploadTask = profileImageRef.putBytes(data)

        uploadTask.addOnSuccessListener { taskSnapshot ->

            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()


                updateProfileImageUrlInDatabase(userId, imageUrl)
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to upload picture: ${e.message}", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()

            // You can use a Progress Bar here if desired
            // Toast.makeText(requireContext(), "Uploading... $progress%", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Function to update profileImageUrl in Realtime Database ---


        }
    }



    private fun updateProfileImageUrlInDatabase(userId: String, imageUrl: String) {
        val userRef = database.getReference("Customers").child(userId)
        userRef.child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile picture saved successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(requireContext(), "Failed to save profile picture in Database: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Customer Data Class (updated with profileImageUrl field)

                Toast.makeText(requireContext(), "Error, Profile picture saved unsuccessfully: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }




    data class Customer(
        val userId: String = "",
        val name: String = "",
        val email: String = "",

        val profileImageUrl: String? = null // This field has been added

        val profileImageUrl: String? = null

    )
}