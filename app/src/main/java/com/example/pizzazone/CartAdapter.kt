import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzazone.CartProduct
import com.example.pizzazone.R

class CartAdapter(private val items: List<CartProduct>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.imageProduct)
        val name = view.findViewById<TextView>(R.id.textProductName)
        val price = view.findViewById<TextView>(R.id.textProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = items[position]
        holder.name.text = product.name
        holder.price.text = product.price
        holder.image.setImageResource(product.imageResId)
    }

    override fun getItemCount(): Int = items.size
}
