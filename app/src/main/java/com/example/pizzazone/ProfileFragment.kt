package com.example.pizzazone

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

data class Customer(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null
)

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    // UI elements
    private lateinit var greetingText: TextView
    private lateinit var textViewProfileName: TextView
    private lateinit var textViewProfileEmail: TextView
    private lateinit var textViewProfilePassword: TextView
    private lateinit var logoutButton: Button
    private lateinit var backArrow: ImageView // This is the ImageView you want to change
    private lateinit var themeToggleButton: ImageView
    private lateinit var profileImage: ShapeableImageView
    private lateinit var cameraIcon: ImageView

    // Edit icons
    private lateinit var editName: ImageView
    private lateinit var editEmail: ImageView
    private lateinit var editPassword: ImageView

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                profileImage.setImageBitmap(it)
                if (isAdded) {
                    Toast.makeText(requireContext(), "Picture taken. Uploading...", Toast.LENGTH_SHORT).show()
                    uploadImageToFirebaseStorage(it)
                }
            }
        } else {
            if (isAdded) {
                Toast.makeText(requireContext(), "Picture taking cancelled or failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            if (isAdded) {
                Toast.makeText(requireContext(), "Camera permission is required to take a photo.", Toast.LENGTH_LONG).show()
            }
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
        textViewProfilePassword = view.findViewById(R.id.textViewProfilePassword)
        logoutButton = view.findViewById(R.id.buttonLogout)
        backArrow = view.findViewById(R.id.backArrow) // Initialize the backArrow
        themeToggleButton = view.findViewById(R.id.themeToggleButton)
        profileImage = view.findViewById(R.id.profileImage)
        cameraIcon = view.findViewById(R.id.cameraIcon)

        editName = view.findViewById(R.id.editName)
        editEmail = view.findViewById(R.id.editEmail)
        editPassword = view.findViewById(R.id.editPassword)

        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            if (isAdded) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }

        // Apply the theme immediately after view inflation and before any user interaction
        // This ensures the back arrow drawable is set correctly when the fragment first appears
        // based on the *current* loaded theme preference.
        val currentThemeMode = loadThemePreference()
        AppCompatDelegate.setDefaultNightMode(currentThemeMode)
        updateBackArrowDrawable(currentThemeMode) // Set initial back arrow drawable

        themeToggleButton.setOnClickListener {
            toggleAppTheme()
        }

        cameraIcon.setOnClickListener {
            checkCameraPermission()
        }

        editName.setOnClickListener {
            if (isAdded) {
                showEditDialog("Name", textViewProfileName.text.toString()) { newName ->
                    updateNameInDatabase(newName)
                }
            }
        }

        editEmail.setOnClickListener {
            if (isAdded) {
                showReAuthDialog("Email", "Enter your current password to update email.") { currentPassword ->
                    showEditDialog("Email", textViewProfileEmail.text.toString()) { newEmail ->
                        updateEmailInFirebase(currentPassword, newEmail)
                    }
                }
            }
        }

        editPassword.setOnClickListener {
            if (isAdded) {
                showReAuthDialog("Password", "Enter your current password to change password.") { currentPassword ->
                    showEditDialog("New Password", "", true) { newPassword ->
                        updatePasswordInFirebase(currentPassword, newPassword)
                    }
                }
            }
        }

        loadUserProfile()

        return view
    }

    // --- NEW FUNCTION TO UPDATE BACK ARROW DRAWABLE ---
    private fun updateBackArrowDrawable(themeMode: Int) {
        if (!isAdded) {
            Log.w("ProfileFragment", "Fragment not attached, cannot update back arrow drawable.")
            return
        }
        when (themeMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> {
                Log.d("ProfileFragment", "Setting back arrow to white (dark mode).")
                backArrow.setImageResource(R.drawable.ic_back_arrow_white)
            }
            AppCompatDelegate.MODE_NIGHT_NO -> {
                Log.d("ProfileFragment", "Setting back arrow to black (light mode).")
                backArrow.setImageResource(R.drawable.ic_back_arrow_black)
            }
            else -> {
                // Fallback for MODE_NIGHT_FOLLOW_SYSTEM or other unhandled modes
                // You can check current UI mode here if needed
                val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                    Log.d("ProfileFragment", "Setting back arrow to white (system dark mode).")
                    backArrow.setImageResource(R.drawable.ic_back_arrow_white)
                } else {
                    Log.d("ProfileFragment", "Setting back arrow to black (system light/unspecified mode).")
                    backArrow.setImageResource(R.drawable.ic_back_arrow_black)
                }
            }
        }
    }


    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        Log.d("ProfileFragment", "loadUserProfile called. Current User: ${currentUser?.email ?: "No user logged in"}")

        if (currentUser != null) {
            val userId = currentUser.uid
            Log.d("ProfileFragment", "Logged in user ID: $userId")
            val userRef = database.getReference("Customers").child(userId)

            userRef.addValueEventListener(object : ValueEventListener {
                @SuppressLint("RestrictedApi")
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ProfileFragment", "onDataChange triggered. Data path: ${snapshot.ref.path}")

                    if (!isAdded) {
                        Log.d("ProfileFragment", "Fragment not attached, skipping UI update in onDataChange.")
                        return
                    }

                    if (snapshot.exists()) {
                        Log.d("ProfileFragment", "DataSnapshot exists. Raw data: ${snapshot.value}")
                        val customer = snapshot.getValue(Customer::class.java)

                        if (customer != null) {
                            Log.d("ProfileFragment", "Customer object created: Name='${customer.name}', Email='${customer.email}', ProfileImageURL='${customer.profileImageUrl}'")

                            greetingText.text = "Hi, ${customer.name}!"
                            textViewProfileName.text = customer.name
                            textViewProfileEmail.text = customer.email
                            textViewProfilePassword.text = "********"

                            val profileImageUrl = customer.profileImageUrl
                            if (!profileImageUrl.isNullOrEmpty()) {
                                Log.d("ProfileFragment", "Loading profile image: $profileImageUrl")
                                if (context != null) {
                                    Glide.with(requireContext())
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.dummy_profile_image)
                                        .error(R.drawable.dummy_profile_image)
                                        .into(profileImage)
                                } else {
                                    Log.e("ProfileFragment", "Context is null for Glide when loading image after isAdded check.")
                                    profileImage.setImageResource(R.drawable.dummy_profile_image)
                                }
                            } else {
                                Log.d("ProfileFragment", "No profile image URL found, setting dummy image.")
                                profileImage.setImageResource(R.drawable.dummy_profile_image)
                            }
                        } else {
                            Log.e("ProfileFragment", "Customer object is null after snapshot.getValue(). This indicates a data mapping issue. Check Customer data class fields vs. Firebase keys (case sensitivity!).")
                            Toast.makeText(requireContext(), "Error: Could not parse user data. Check Firebase structure.", Toast.LENGTH_LONG).show()
                            profileImage.setImageResource(R.drawable.dummy_profile_image)
                        }
                    } else {
                        Log.d("ProfileFragment", "DataSnapshot does not exist for user ID: $userId. User data might be missing in Firebase Realtime Database at 'Customers/$userId'.")
                        Toast.makeText(requireContext(), "User data not found in database.", Toast.LENGTH_SHORT).show()
                        profileImage.setImageResource(R.drawable.dummy_profile_image)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileFragment", "Firebase Database error: ${error.message}", error.toException())
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Failed to load profile: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    profileImage.setImageResource(R.drawable.dummy_profile_image)
                }
            })
        } else {
            Log.d("ProfileFragment", "No user logged in. Redirecting to Login Activity.")
            if (isAdded) {
                Toast.makeText(requireContext(), "No user logged in. Redirecting to Login.", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun showEditDialog(title: String, initialValue: String, isPassword: Boolean = false, onSave: (String) -> Unit) {
        if (!isAdded) {
            Log.w("ProfileFragment", "Attempted to showEditDialog but fragment is not attached.")
            return
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)

        val input = EditText(requireContext())
        input.setText(initialValue)
        if (isPassword) {
            input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            input.hint = "Enter new password"
        }
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val newValue = input.text.toString().trim()
            if (newValue.isNotEmpty()) {
                onSave(newValue)
            } else {
                Toast.makeText(requireContext(), "$title cannot be empty.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun showReAuthDialog(title: String, message: String, onReAuthenticated: (String) -> Unit) {
        if (!isAdded) {
            Log.w("ProfileFragment", "Attempted to showReAuthDialog but fragment is not attached.")
            return
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)

        val input = EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        input.hint = "Current Password"
        builder.setView(input)

        builder.setPositiveButton("Verify") { dialog, _ ->
            val currentPassword = input.text.toString().trim()
            if (currentPassword.isNotEmpty()) {
                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                    user.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if (!isAdded) {
                                Log.w("ProfileFragment", "Re-authentication task completed but fragment detached.")
                                return@addOnCompleteListener
                            }

                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Re-authentication successful.", Toast.LENGTH_SHORT).show()
                                onReAuthenticated(currentPassword)
                            } else {
                                Toast.makeText(requireContext(), "Re-authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                                Log.e("ProfileFragment", "Re-authentication failed", task.exception)
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "User not logged in or email unavailable.", Toast.LENGTH_SHORT).show()
                    Log.w("ProfileFragment", "User is null or email is null during re-authentication attempt.")
                }
            } else {
                Toast.makeText(requireContext(), "Current password cannot be empty.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun updateNameInDatabase(newName: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            database.getReference("Customers").child(userId).child("name").setValue(newName)
                .addOnSuccessListener {
                    if (!isAdded) {
                        Log.w("ProfileFragment", "Name update successful but fragment detached before UI update.")
                        return@addOnSuccessListener
                    }
                    Toast.makeText(requireContext(), "Name updated successfully!", Toast.LENGTH_SHORT).show()
                    textViewProfileName.text = newName
                    greetingText.text = "Hi, $newName!"
                    Log.d("ProfileFragment", "Name updated in DB and UI: $newName")
                }
                .addOnFailureListener { e ->
                    if (!isAdded) {
                        Log.w("ProfileFragment", "Failed to update name but fragment detached.")
                        return@addOnFailureListener
                    }
                    Toast.makeText(requireContext(), "Failed to update name: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileFragment", "Failed to update name in DB", e)
                }
        }
    }

    private fun updateEmailInFirebase(currentPassword: String, newEmail: String) {
        val user = auth.currentUser
        if (user != null) {
            user.updateEmail(newEmail)
                .addOnCompleteListener { task ->
                    if (!isAdded) {
                        Log.w("ProfileFragment", "Email update task completed but fragment detached.")
                        return@addOnCompleteListener
                    }

                    if (task.isSuccessful) {
                        Log.d("ProfileFragment", "Email updated in Firebase Auth: $newEmail")
                        database.getReference("Customers").child(user.uid).child("email").setValue(newEmail)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Email updated successfully!", Toast.LENGTH_SHORT).show()
                                textViewProfileEmail.text = newEmail
                                Log.d("ProfileFragment", "Email updated in Realtime Database.")
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Email updated in Auth, but failed to update in Database: ${e.message}", Toast.LENGTH_LONG).show()
                                Log.e("ProfileFragment", "Failed to update email in Realtime Database", e)
                            }
                    } else {
                        Toast.makeText(requireContext(), "Failed to update email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        Log.e("ProfileFragment", "Failed to update email in Firebase Auth", task.exception)
                    }
                }
        }
    }

    private fun updatePasswordInFirebase(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        if (user != null) {
            if (newPassword.length < 6) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "New password must be at least 6 characters.", Toast.LENGTH_LONG).show()
                }
                Log.w("ProfileFragment", "Password update failed: New password too short.")
                return
            }

            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (!isAdded) {
                        Log.w("ProfileFragment", "Password update task completed but fragment detached.")
                        return@addOnCompleteListener
                    }

                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show()
                        Log.d("ProfileFragment", "Password updated in Firebase Auth.")
                    } else {
                        Toast.makeText(requireContext(), "Failed to update password: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        Log.e("ProfileFragment", "Failed to update password in Firebase Auth", task.exception)
                    }
                }
        }
    }

    private fun toggleAppTheme() {
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        val newMode = when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_YES
        }
        AppCompatDelegate.setDefaultNightMode(newMode)
        Log.d("ProfileFragment", "Attempting to toggle theme to mode: $newMode")

        if (isAdded) {
            saveThemePreference(newMode)
            updateBackArrowDrawable(newMode) // <-- Call this to update the arrow based on new theme
            val modeName = if (newMode == AppCompatDelegate.MODE_NIGHT_YES) "Dark Mode" else "Light Mode"
            Toast.makeText(requireContext(), "Switched to $modeName", Toast.LENGTH_SHORT).show()
            Log.d("ProfileFragment", "Theme preference saved and recreating activity.")
            // Recreating activity is good practice for theme changes, as it ensures all views are redrawn
            requireActivity().recreate()
        } else {
            Log.w("ProfileFragment", "Fragment not attached, cannot toggle theme or recreate activity.")
        }
    }

    private fun saveThemePreference(mode: Int) {
        if (!isAdded) {
            Log.w("ProfileFragment", "Attempted to saveThemePreference but fragment is not attached.")
            return
        }
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("theme_mode", mode)
            apply()
        }
        Log.d("ProfileFragment", "Theme preference saved: mode = $mode")
    }

    private fun loadThemePreference(): Int {
        if (!isAdded) {
            Log.w("ProfileFragment", "Attempted to loadThemePreference but fragment is not attached, returning default.")
            return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        val sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val loadedMode = sharedPref.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        Log.d("ProfileFragment", "Theme preference loaded: mode = $loadedMode")
        return loadedMode
    }

    private fun checkCameraPermission() {
        if (!isAdded) {
            Log.w("ProfileFragment", "Attempted to checkCameraPermission but fragment is not attached.")
            return
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("ProfileFragment", "Camera permission already granted.")
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(requireContext(), "We need camera permission to take your profile picture.", Toast.LENGTH_LONG).show()
                Log.d("ProfileFragment", "Showing camera permission rationale.")
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                Log.d("ProfileFragment", "Requesting camera permission.")
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        if (!isAdded) {
            Log.w("ProfileFragment", "Attempted to openCamera but fragment is not attached.")
            return
        }

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            Log.d("ProfileFragment", "Launching camera intent.")
            takePictureLauncher.launch(takePictureIntent)
        } else {
            Toast.makeText(requireContext(), "No camera app found.", Toast.LENGTH_SHORT).show()
            Log.e("ProfileFragment", "No camera app found to handle ACTION_IMAGE_CAPTURE.")
        }
    }

    private fun uploadImageToFirebaseStorage(bitmap: Bitmap) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            if (isAdded) {
                Toast.makeText(requireContext(), "Please log in to upload picture.", Toast.LENGTH_SHORT).show()
            }
            Log.w("ProfileFragment", "No user logged in, cannot upload image.")
            return
        }

        val userId = currentUser.uid
        val profileImageRef = storageRef.child("profile_images/$userId.jpg")
        Log.d("ProfileFragment", "Attempting to upload image for user: $userId to path: ${profileImageRef.path}")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()

        val uploadTask = profileImageRef.putBytes(data)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            Log.d("ProfileFragment", "Image uploaded successfully. Bytes transferred: ${taskSnapshot.bytesTransferred}")
            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                Log.d("ProfileFragment", "Download URL obtained: $imageUrl")
                updateProfileImageUrlInDatabase(userId, imageUrl)
            }.addOnFailureListener { e ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("ProfileFragment", "Failed to get download URL", e)
            }
        }.addOnFailureListener { e ->
            if (isAdded) {
                Toast.makeText(requireContext(), "Failed to upload picture: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            Log.e("ProfileFragment", "Failed to upload picture", e)
        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            Log.d("ProfileFragment", "Upload progress: $progress%")
        }
    }

    private fun updateProfileImageUrlInDatabase(userId: String, imageUrl: String) {
        val userRef = database.getReference("Customers").child(userId)
        userRef.child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Profile picture saved successfully.", Toast.LENGTH_SHORT).show()
                }
                Log.d("ProfileFragment", "Profile image URL saved to Realtime Database for user: $userId")
            }
            .addOnFailureListener { e ->
                if (isAdded) {
                    Toast.makeText(requireContext(), "Failed to save profile picture in Database: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("ProfileFragment", "Failed to save profile picture URL in Database", e)
            }
    }
}