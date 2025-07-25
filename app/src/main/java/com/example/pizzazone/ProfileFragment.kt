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
import com.bumptech.glide.Glide // අලුතින් import කරන්න
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage // අලුතින් import කරන්න
import com.google.firebase.storage.StorageReference // අලුතින් import කරන්න
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
    private lateinit var leftToRightImage: ImageView
    private lateinit var profileImage: ShapeableImageView
    private lateinit var cameraIcon: ImageView


    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
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
        leftToRightImage = view.findViewById(R.id.leftToRight)
        profileImage = view.findViewById(R.id.profileImage)
        cameraIcon = view.findViewById(R.id.cameraIcon)



        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }


        leftToRightImage.setOnClickListener {
            toggleAppTheme()
        }



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
                            textViewProfileName.text = "${it.name}"
                            textViewProfileEmail.text = "${it.email}"


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
                        Toast.makeText(requireContext(), "Not defined data.", Toast.LENGTH_SHORT).show()

                        profileImage.setImageResource(R.drawable.dummy_profile_image)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "fail to load profile: ${error.message}", Toast.LENGTH_SHORT).show()
                    error.toException().printStackTrace()
                }
            })
        } else {
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
                Toast.makeText(requireContext(), "Changed Light Mode", Toast.LENGTH_SHORT).show()
            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(requireContext(), "Changed Dark Mode", Toast.LENGTH_SHORT).show()
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(requireContext(), "Changed dark mode", Toast.LENGTH_SHORT).show()
            }
        }
        requireActivity().recreate()
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


    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
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
                Toast.makeText(requireContext(), "Download URL ලබා ගැනීමට අසමත් විය: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "පින්තූරය upload කිරීමට අසමත් විය: ${e.message}", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()

        }
    }


    private fun updateProfileImageUrlInDatabase(userId: String, imageUrl: String) {
        val userRef = database.getReference("Customers").child(userId)
        userRef.child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile picture saved successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error, Profile picture saved unsuccessfully: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    data class Customer(
        val userId: String = "",
        val name: String = "",
        val email: String = "",
        val profileImageUrl: String? = null
    )
}