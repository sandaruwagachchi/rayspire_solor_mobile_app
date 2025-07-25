
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

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onCartChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.imageProduct)
        val name = view.findViewById<TextView>(R.id.textProductName)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBarProduct)
        val price = view.findViewById<TextView>(R.id.textProductPrice)
        val quantity = view.findViewById<TextView>(R.id.textQuantity)
        val buttonAddOne = view.findViewById<ImageView>(R.id.buttonAddOne)
        val buttonRemove = view.findViewById<AppCompatButton>(R.id.buttonRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartProduct = items[position]

        holder.name.text = cartProduct.item.title
        holder.ratingBar.rating = cartProduct.item.rating.toFloat()
        holder.price.text = "$${String.format("%.2f", cartProduct.item.price)}"
        holder.quantity.text = "Qty ${cartProduct.quantity}"

        if (cartProduct.item.picUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(cartProduct.item.picUrl[0])
                .into(holder.image)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.placeholder_image)
                .into(holder.image)
        }


        holder.buttonAddOne.setOnClickListener {
            CartManager.addItemToCart(cartProduct.item)
            onCartChanged.invoke()
        }


        holder.buttonRemove.setOnClickListener {
            CartManager.removeAllInstancesOfItem(cartProduct.item)
            onCartChanged.invoke()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateCartItems(newItems: MutableList<CartItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}