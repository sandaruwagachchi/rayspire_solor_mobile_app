package com.example.pizzazone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class AdminAddProductFragment : Fragment() {

    private lateinit var imagePreview: ImageView
    private lateinit var buttonChooseImage: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_add_product, container, false)

        val btnhome = view.findViewById<Button>(R.id.btnhome)
        buttonChooseImage = view.findViewById(R.id.buttonChooseImage)
        imagePreview = view.findViewById(R.id.imagePreview)

        btnhome.setOnClickListener {
            val intent = Intent(activity, AdminHomeScreenActivity::class.java)
            intent.putExtra("showDetailsFragment", true)
            startActivity(intent)
        }

        // Register image picker
        val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imagePreview.setImageURI(it)
            }
        }

        buttonChooseImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        return view
    }
}
