package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class OderInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_oderer_infromation, container, false)

        val backArrow = view.findViewById<ImageView>(R.id.shipping_back_arrow)
        val buttonsave = view.findViewById<Button>(R.id.buttonsave)

        val editTextName = view.findViewById<EditText>(R.id.editTextName)
        val editTextAddress = view.findViewById<EditText>(R.id.editTextAddress)
        val editTextMobile = view.findViewById<EditText>(R.id.editTextMobile)
        val editTextZipCode = view.findViewById<EditText>(R.id.editTextZipCode)


        arguments?.let { bundle ->
            editTextName.setText(bundle.getString("name"))
            editTextAddress.setText(bundle.getString("address"))
            editTextMobile.setText(bundle.getString("mobile"))
            editTextZipCode.setText(bundle.getString("zipCode"))
        }

        buttonsave.setOnClickListener {

            editTextName.error = null
            editTextAddress.error = null
            editTextMobile.error = null
            editTextZipCode.error = null

            var isValid = true


            if (editTextName.text.toString().trim().isEmpty()) {
                editTextName.error = "Please enter your full name"
                isValid = false
            }

            if (editTextAddress.text.toString().trim().isEmpty()) {
                editTextAddress.error = "Please enter your shipping address"
                isValid = false
            }

            val mobile = editTextMobile.text.toString().trim()
            if (mobile.isEmpty()) {
                editTextMobile.error = "Please enter your mobile number"
                isValid = false
            } else if (!mobile.matches(Regex("\\d{10,15}"))) { // 10-15 digits
                editTextMobile.error = "Please enter a valid mobile number"
                isValid = false
            }

            val zipCode = editTextZipCode.text.toString().trim()
            if (zipCode.isEmpty()) {
                editTextZipCode.error = "Please enter your zip code"
                isValid = false
            } else if (!zipCode.matches(Regex("\\d{4,10}"))) { // 4-10 digits
                editTextZipCode.error = "Please enter a valid zip code"
                isValid = false
            }

            if (isValid) {
                Toast.makeText(requireContext(), "Shipping Details Saved Successfully...", Toast.LENGTH_SHORT).show()


                val intent = Intent(requireContext(), CheckoutScreenActivity::class.java).apply {
                    putExtra("name", editTextName.text.toString().trim())
                    putExtra("address", editTextAddress.text.toString().trim())
                    putExtra("mobile", mobile)
                    putExtra("zipCode", zipCode)
                }
                startActivity(intent)

                requireActivity().finish()
            }
        }

        backArrow.setOnClickListener {

            val currentName = editTextName.text.toString().trim()
            val currentAddress = editTextAddress.text.toString().trim()
            val currentMobile = editTextMobile.text.toString().trim()
            val currentZipCode = editTextZipCode.text.toString().trim()

            val intent = Intent(requireContext(), CheckoutScreenActivity::class.java).apply {
                putExtra("name", currentName)
                putExtra("address", currentAddress)
                putExtra("mobile", currentMobile)
                putExtra("zipCode", currentZipCode)
            }
            startActivity(intent)

            requireActivity().finish()
        }

        return view
    }
}