package com.example.pizzazone

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class CheckoutScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checkout_screen, container, false)


        val subtotal = arguments?.getDouble("subtotal_amount") ?: 0.0


        val orderAmountTextView = view.findViewById<TextView>(R.id.order_amount)
        orderAmountTextView.text = "$${String.format("%.2f", subtotal)}"


        val deliveryFees = 1.50
        val totalAmount = subtotal + deliveryFees

        view.findViewById<TextView>(R.id.tv_delivery_fees).text = "$${String.format("%.2f", deliveryFees)}"
        view.findViewById<TextView>(R.id.tv_total_amount).text = "$${String.format("%.2f", totalAmount)}"
        view.findViewById<TextView>(R.id.tv_total_amount_bottom).text = "$${String.format("%.2f", totalAmount)}"



        val buttonconfirm = view.findViewById<Button>(R.id.buttonconfirm)
        val backArrow = view.findViewById<ImageView>(R.id.shipping_back_arrow)
        val edit_shop = view.findViewById<ImageView>(R.id.edit_shipping_icon)


        val name = arguments?.getString("name")
        val address = arguments?.getString("address")
        val mobile = arguments?.getString("mobile")
        val zipCode = arguments?.getString("zipCode")

        fun setTextOrHide(textView: TextView, text: String?) {
            if (!text.isNullOrEmpty()) {
                textView.text = text
                textView.visibility = View.VISIBLE
            } else {
                textView.visibility = View.GONE
            }
        }

        setTextOrHide(view.findViewById(R.id.tv_shipping_name), name)
        setTextOrHide(view.findViewById(R.id.tv_shipping_address), address)
        setTextOrHide(view.findViewById(R.id.tv_shipping_mobile), mobile)
        setTextOrHide(view.findViewById(R.id.tv_shipping_zip), zipCode)

        buttonconfirm.setOnClickListener {
            if (name.isNullOrBlank() || address.isNullOrBlank() || mobile.isNullOrBlank() || zipCode.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Please fill all shipping details before confirming.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, OrderConfirmActivity::class.java)
                startActivity(intent)
            }
        }

        edit_shop.setOnClickListener {
            val intent = Intent(activity, OderInfoActivity::class.java)
            startActivity(intent)
        }

        backArrow.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
