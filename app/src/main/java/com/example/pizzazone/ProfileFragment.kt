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
    private lateinit var storage: FirebaseStorage // Firebase Storage සඳහා
    private lateinit var storageRef: StorageReference // Storage Reference සඳහා

    // UI elements
    private lateinit var greetingText: TextView
    private lateinit var textViewProfileName: TextView
    private lateinit var textViewProfileEmail: TextView
    private lateinit var logoutButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var leftToRightImage: ImageView
    private lateinit var profileImage: ShapeableImageView
    private lateinit var cameraIcon: ImageView

    // කැමරාවෙන් පින්තූරයක් ගැනීම සඳහා ActivityResultLauncher එකක්
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                profileImage.setImageBitmap(it) // profileImage එකට පින්තූරය සකසන්න
                Toast.makeText(requireContext(), "පින්තූරය ගත්තා. Upload කරමින් සිටී...", Toast.LENGTH_SHORT).show()
                uploadImageToFirebaseStorage(it) // Firebase Storage වෙත upload කරන්න
            }
        } else {
            Toast.makeText(requireContext(), "පින්තූර ගැනීම අවලංගු කරන ලදී හෝ අසාර්ථක විය.", Toast.LENGTH_SHORT).show()
        }
    }

    // අවසර ඉල්ලීම සඳහා ActivityResultLauncher එකක්
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "ඡායාරූපයක් ගැනීමට කැමරා අවසරය අවශ්‍ය වේ.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance() // Firebase Storage ආරම්භ කරන්න
        storageRef = storage.reference // Storage Reference ආරම්භ කරන්න

        // Initialize UI elements from the inflated view
        greetingText = view.findViewById(R.id.greetingText)
        textViewProfileName = view.findViewById(R.id.textViewProfileName)
        textViewProfileEmail = view.findViewById(R.id.textViewProfileEmail)
        logoutButton = view.findViewById(R.id.buttonLogout)
        backArrow = view.findViewById(R.id.backArrow)
        leftToRightImage = view.findViewById(R.id.leftToRight)
        profileImage = view.findViewById(R.id.profileImage)
        cameraIcon = view.findViewById(R.id.cameraIcon)

        // Set up click listeners
        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        // --- Dark/Light Mode Toggle Logic ---
        leftToRightImage.setOnClickListener {
            toggleAppTheme()
        }
        // --- End Dark/Light Mode Toggle Logic ---

        // --- කැමරා අයිකනය ක්ලික් කිරීමේ Logic ---
        cameraIcon.setOnClickListener {
            checkCameraPermission()
        }
        // --- End Camera Icon Click Listener ---

        // Load user profile data
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

                            // Firebase Database එකෙන් profileImageUrl එක load කරන්න
                            val profileImageUrl = it.profileImageUrl
                            if (!profileImageUrl.isNullOrEmpty()) {
                                // Glide පුස්තකාලය භාවිතයෙන් පින්තූරය load කරන්න
                                Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.dummy_profile_image) // default image එකක්
                                    .error(R.drawable.dummy_profile_image) // error වුවහොත් පෙන්වන image එක
                                    .into(profileImage)
                            } else {
                                // URL එකක් නොමැති නම් default image එක පෙන්වන්න
                                profileImage.setImageResource(R.drawable.dummy_profile_image)
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "දත්ත ගබඩාවේ පරිශීලක දත්ත හමු නොවීය.", Toast.LENGTH_SHORT).show()
                        // දත්ත නොමැති නම් default image එක පෙන්වන්න
                        profileImage.setImageResource(R.drawable.dummy_profile_image)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "ප්‍රොෆයිලය load කිරීමට අසමත් විය: ${error.message}", Toast.LENGTH_SHORT).show()
                    error.toException().printStackTrace()
                }
            })
        } else {
            Toast.makeText(requireContext(), "කිසිදු පරිශීලකයෙක් login වී නැත. කරුණාකර login වන්න.", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    // --- Dark/Light Mode Toggle Function ---
    private fun toggleAppTheme() {
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK

        when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(requireContext(), "Light Mode වෙත මාරු විය", Toast.LENGTH_SHORT).show()
            }
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(requireContext(), "Dark Mode වෙත මාරු විය", Toast.LENGTH_SHORT).show()
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveThemePreference(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(requireContext(), "Dark Mode වෙත මාරු විය", Toast.LENGTH_SHORT).show()
            }
        }
        requireActivity().recreate()
    }

    // --- Theme Preference Save/Load Functions ---
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

    // --- කැමරා අවසරය සහ කැමරාව විවෘත කිරීමේ කාර්යයන් ---
    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(requireContext(), "ඔබගේ ප්‍රොෆයිල් පින්තූරය ගැනීමට අපට කැමරා අවසරය අවශ්‍යයි.", Toast.LENGTH_LONG).show()
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
            Toast.makeText(requireContext(), "කැමරා App එකක් හමු නොවීය.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Firebase Storage වෙත පින්තූරය upload කිරීමේ ශ්‍රිතය ---
    private fun uploadImageToFirebaseStorage(bitmap: Bitmap) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "පින්තූරය upload කිරීමට කරුණාකර login වන්න.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        // "profile_images" ෆෝල්ඩරය තුළ පරිශීලකයාගේ UID එක නමින් ගොනුවක් සාදන්න
        val profileImageRef = storageRef.child("profile_images/$userId.jpg")

        // Bitmap එක ByteArray එකකට පරිවර්තනය කරන්න
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos) // පින්තූරයේ ගුණාත්මකභාවය 100%
        val data = baos.toByteArray()

        // Upload Task එක
        val uploadTask = profileImageRef.putBytes(data)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Upload සාර්ථකයි, download URL එක ලබා ගන්න
            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                // Realtime Database හි පරිශීලක දත්ත යාවත්කාලීන කරන්න
                updateProfileImageUrlInDatabase(userId, imageUrl)
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Download URL ලබා ගැනීමට අසමත් විය: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "පින්තූරය upload කිරීමට අසමත් විය: ${e.message}", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            // ඔබට මෙහි Progress Bar එකක් පෙන්වීමට අවශ්‍ය නම් භාවිතා කළ හැක
            // Toast.makeText(requireContext(), "Uploading... $progress%", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Realtime Database හි profileImageUrl යාවත්කාලීන කිරීමේ ශ්‍රිතය ---
    private fun updateProfileImageUrlInDatabase(userId: String, imageUrl: String) {
        val userRef = database.getReference("Customers").child(userId)
        userRef.child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "ප්‍රොෆයිල් පින්තූරය සාර්ථකව සුරකින ලදී.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "ප්‍රොෆයිල් පින්තූරය Database හි සුරැකීමට අසමත් විය: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    // Customer Data Class (profileImageUrl ක්ෂේත්‍රය සමඟ යාවත්කාලීන කර ඇත)
    data class Customer(
        val userId: String = "",
        val name: String = "",
        val email: String = "",
        val profileImageUrl: String? = null // මෙය අලුතින් එකතු කර ඇත
    )
}