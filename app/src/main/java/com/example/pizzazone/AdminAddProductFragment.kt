package com.example.pizzazone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pizzazone.Domain.ItemModel
import com.example.pizzazone.ViewModel.MainViewModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AdminAddProductFragment : Fragment() {

    private lateinit var imagePreview: ImageView
    private lateinit var buttonChooseImage: Button
    private lateinit var btnAddProduct: Button
    private lateinit var categoryDropdown: MaterialAutoCompleteTextView

    private lateinit var editTextName: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextRate: EditText
    private lateinit var editTextDescription: EditText

    private lateinit var viewModel: MainViewModel

    private var selectedImageUri: Uri? = null
    private var selectedCategoryTitle: String? = null
    private var selectedCategoryId: String? = null
    private var categoryTitleToIdMap: Map<String, String> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_add_product, container, false)

        imagePreview = view.findViewById(R.id.imagePreview)
        buttonChooseImage = view.findViewById(R.id.buttonChooseImage)
        btnAddProduct = view.findViewById(R.id.btnhome)
        categoryDropdown = view.findViewById(R.id.editTextCategory)

        editTextName = view.findViewById(R.id.editTextProductName)
        editTextPrice = view.findViewById(R.id.editTextPrice)
        editTextRate = view.findViewById(R.id.editTextRate)
        editTextDescription = view.findViewById(R.id.editTextDescription)

        val database = FirebaseDatabase.getInstance().reference
        val storageRef = FirebaseStorage.getInstance().reference

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.loadCategory().observe(viewLifecycleOwner) { categoryList ->
            val titles = categoryList.map { it.title }
            categoryTitleToIdMap = categoryList
                .filter { it.title != null }
                .associate { it.title!! to it.id.toString() }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, titles)
            categoryDropdown.setAdapter(adapter)

            categoryDropdown.setOnClickListener {
                categoryDropdown.showDropDown()
            }

            categoryDropdown.setOnItemClickListener { parent, _, position, _ ->
                val selectedTitle = parent.getItemAtPosition(position).toString()
                selectedCategoryTitle = selectedTitle
                selectedCategoryId = categoryTitleToIdMap[selectedTitle]
            }
        }

        val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                imagePreview.setImageURI(it)
                Log.d("IMAGE_PICKER", "Image URI: $it")
            }
        }

        buttonChooseImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnAddProduct.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val price = editTextPrice.text.toString().trim().toDoubleOrNull()
            val rate = editTextRate.text.toString().trim().toDoubleOrNull()
            val description = editTextDescription.text.toString().trim()
            val categoryId = selectedCategoryId

            if (name.isEmpty() || price == null || rate == null || description.isEmpty()
                || categoryId.isNullOrEmpty() || selectedImageUri == null
            ) {
                Toast.makeText(requireContext(), "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val itemsRef = database.child("Items")

            itemsRef.get().addOnSuccessListener { dataSnapshot ->
                val maxKey = dataSnapshot.children
                    .mapNotNull { it.key?.toIntOrNull() }
                    .maxOrNull() ?: -1

                val newKey = (maxKey + 1).toString()

                val imageRef = storageRef.child("Items/$newKey.jpg")

                imageRef.putFile(selectedImageUri!!)
                    .addOnSuccessListener {
                        imageRef.downloadUrl.addOnSuccessListener { uri ->

                            val itemModel = ItemModel(
                                title = name,
                                description = description,
                                picUrl = arrayListOf(uri.toString()),
                                price = price,
                                rating = rate,
                                categoryId = categoryId
                            )

                            itemsRef.child(newKey).setValue(itemModel)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT).show()
                                    clearFields()
                                    navigateToHome()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), "Failed to save product: ${e.message}", Toast.LENGTH_SHORT).show()
                                }

                        }.addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to get image URL", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to read database: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun clearFields() {
        editTextName.text?.clear()
        editTextPrice.text?.clear()
        editTextRate.text?.clear()
        editTextDescription.text?.clear()
        categoryDropdown.setText("")
        imagePreview.setImageResource(R.drawable.ic_launcher_background)
        selectedImageUri = null
        selectedCategoryTitle = null
        selectedCategoryId = null
    }

    private fun navigateToHome() {
        val intent = Intent(activity, AdminHomeScreenActivity::class.java)
        intent.putExtra("showDetailsFragment", true)
        startActivity(intent)
    }
}
