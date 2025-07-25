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
import com.example.pizzazone.Domain.OrderDetail
import com.example.pizzazone.Domain.OrderItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log // Import Log for debugging


class CheckoutScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_checkout_screen, container, false)


        val cartItems = CartManager.cartItems.value ?: emptyList()
        val subtotal = cartItems.sumOf { it.item.price * it.quantity }

        val deliveryFees = 82.50
        val totalAmount = subtotal + deliveryFees


        val orderAmountTextView = view.findViewById<TextView>(R.id.order_amount)
        orderAmountTextView.text = "$${String.format("%.2f", subtotal)}"

        view.findViewById<TextView>(R.id.tv_delivery_fees).text = "$${String.format("%.2f", deliveryFees)}"
        view.findViewById<TextView>(R.id.tv_total_amount).text = "$${String.format("%.2f", totalAmount)}"
        view.findViewById<TextView>(R.id.tv_total_amount_bottom).text = "$${String.format("%.2f", totalAmount)}"

        val buttonConfirm = view.findViewById<Button>(R.id.buttonconfirm)
        val backArrow = view.findViewById<ImageView>(R.id.shipping_back_arrow)
        val editShop = view.findViewById<ImageView>(R.id.edit_shipping_icon)


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

        buttonConfirm.setOnClickListener {
            if (name.isNullOrBlank() || address.isNullOrBlank() || mobile.isNullOrBlank() || zipCode.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Please fill all shipping details before confirming.", Toast.LENGTH_SHORT).show()
                Log.w("CheckoutScreenFragment", "Missing shipping details for order confirmation.")
            } else {
                saveOrderToFirebase(
                    fullName = name,
                    address = address,
                    mobile = mobile,
                    zipCode = zipCode,
                    subtotal = subtotal,
                    deliveryFees = deliveryFees
                )
            }
        }

        editShop.setOnClickListener {
            val intent = Intent(activity, OderInfoActivity::class.java).apply {
                putExtra("name", name)
                putExtra("address", address)
                putExtra("mobile", mobile)
                putExtra("zipCode", zipCode)
            }
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

    private fun saveOrderToFirebase(
        fullName: String,
        address: String,
        mobile: String,
        zipCode: String,
        subtotal: Double,
        deliveryFees: Double
    ) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        Log.d("CheckoutScreenFragment", "Attempting to save order. Current Firebase User: ${currentUser?.email} (UID: ${currentUser?.uid})")

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in. Please log in to confirm your order.", Toast.LENGTH_SHORT).show()
            Log.e("CheckoutScreenFragment", "User is null, cannot save order.")
            return
        }

        val database = FirebaseDatabase.getInstance()
        val ordersRef = database.getReference("Orders")

        val orderId = ordersRef.push().key ?: ""
        if (orderId.isEmpty()) {
            Toast.makeText(requireContext(), "Failed to generate Order ID.", Toast.LENGTH_SHORT).show()
            Log.e("CheckoutScreenFragment", "Generated orderId is empty, cannot save order.")
            return
        }
        Log.d("CheckoutScreenFragment", "Generated Order ID: $orderId")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = dateFormat.format(Date())
        val time = timeFormat.format(Date())

        val cartItems = CartManager.cartItems.value ?: emptyList()
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Your cart is empty. Cannot place an empty order.", Toast.LENGTH_SHORT).show()
            Log.e("CheckoutScreenFragment", "Cart is empty, cannot save order.")
            return
        }

        // FIX STARTS HERE
        // The CartItem now contains an ItemModel, so access its 'title'
        val orderItems = cartItems.map { cartItem ->
            OrderItem(itemName = cartItem.item.title, quantity = cartItem.quantity)
        }
        // FIX ENDS HERE

        val totalAmount = subtotal + deliveryFees

        val orderDetail = OrderDetail(
            orderId = orderId,
            userId = currentUser.uid,
            date = date,
            time = time,
            fullName = fullName,
            address = address,
            mobile = mobile,
            zipCode = zipCode,
            totalAmount = totalAmount,
            items = orderItems
        )

        ordersRef.child(orderId).setValue(orderDetail)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Order Confirmed!", Toast.LENGTH_SHORT).show()
                Log.d("CheckoutScreenFragment", "Order saved successfully for UID: ${currentUser.uid}, Order ID: $orderId")
                CartManager.clearCart()
                startActivity(Intent(requireActivity(), OrderConfirmActivity::class.java))
                requireActivity().finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to save order: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("CheckoutScreenFragment", "Failed to save order: ${e.message}", e)
            }
    }
}