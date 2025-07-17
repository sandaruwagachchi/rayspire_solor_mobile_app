// This file should be named CartAdapter.kt in the Adapter package or similar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.RatingBar // Import RatingBar
import androidx.appcompat.widget.AppCompatButton // Import AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzazone.CartManager
import com.example.pizzazone.CartItem // Import CartItem
import com.example.pizzazone.R

// Pass a lambda to notify the fragment about quantity/item changes for subtotal update
class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onCartChanged: () -> Unit // Callback for any change (add, remove, quantity)
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.imageProduct)
        val name = view.findViewById<TextView>(R.id.textProductName)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBarProduct) // New: RatingBar
        val price = view.findViewById<TextView>(R.id.textProductPrice)
        val quantity = view.findViewById<TextView>(R.id.textQuantity) // For displaying "Qty X"
        val buttonAddOne = view.findViewById<ImageView>(R.id.buttonAddOne) // New: plus in circle
        val buttonRemove = view.findViewById<AppCompatButton>(R.id.buttonRemove) // New: Remove button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartProduct = items[position]

        holder.name.text = cartProduct.item.title
        holder.ratingBar.rating = cartProduct.item.rating.toFloat() // Set rating
        holder.price.text = "$${String.format("%.2f", cartProduct.item.price)}" // Display individual item price
        holder.quantity.text = "Qty ${cartProduct.quantity}" // Display quantity

        if (cartProduct.item.picUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(cartProduct.item.picUrl[0])
                .into(holder.image)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.placeholder_image) // Ensure you have a placeholder_image
                .into(holder.image)
        }

        // *** ADD ONE BUTTON CLICK LISTENER (plus in circle) ***
        holder.buttonAddOne.setOnClickListener {
            CartManager.addItemToCart(cartProduct.item)
            onCartChanged.invoke() // Notify fragment to update subtotal and adapter
        }

        // *** REMOVE BUTTON CLICK LISTENER ***
        holder.buttonRemove.setOnClickListener {
            CartManager.removeAllInstancesOfItem(cartProduct.item)
            onCartChanged.invoke() // Notify fragment to update subtotal and adapter
        }
    }

    override fun getItemCount(): Int = items.size

    // Method to update the adapter's data
    fun updateCartItems(newItems: MutableList<CartItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}