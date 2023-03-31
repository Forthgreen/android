package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.utils.firstOrEmpty
import com.forthgreen.app.views.utils.loadURL
import kotlinx.android.synthetic.main.row_shop_products_rv.view.*

/**
 * @author Nandita Gandhi
 * @since 11 Jan 2022
 */
class ShopProductsAdapter(private val mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "ShopProductsAdapter"
        private const val MAX_PRODUCTS_COUNT = 20
    }

    // Variables
    private var productList = mutableListOf<Product>()
    private var category = 0
    private val productClickCallback by lazy {
        mFragment as ProductsCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(parent.inflate(R.layout.row_shop_products_rv))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                holder.bind(productList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return if (productList.size > MAX_PRODUCTS_COUNT) {
            MAX_PRODUCTS_COUNT
        } else {
            productList.size
        }
    }

    fun submitList(listWeGet: List<Product>, category: Int) {
        productList.clear()
        this.category = category
        productList.addAll(listWeGet)
        notifyDataSetChanged()
    }

    fun updateProduct(product: Product) {
        val index = productList.indexOfFirst { prod -> prod._id == product._id }
        if (index != -1) {
            // Remove the old product and add the updated product.
            productList.removeAt(index)
            productList.add(index, product)
            notifyItemChanged(index)
        }
    }

    inner class ProductViewHolder constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                productClickCallback.onProductsClick(productList[adapterPosition - 1])
            }
            itemView.cbBookmark.setOnClickListener {
                productClickCallback.updateFollowStatus(productList[adapterPosition - 1], category)
                itemView.cbBookmark.isChecked = !itemView.cbBookmark.isChecked
            }
        }

        fun bind(product: Product) {
            itemView.apply {
                // Assign values
                // Load First Image or Empty String to in turn serve as placeholder.
                ivProductImage.loadURL(product.images.firstOrEmpty(), isUserImage = false)
                tvProductName.text = product.name
                tvBrandName.text = product.brandName
                tvProductPrice.text = "${product.productPriceText}"
                cbBookmark.isChecked = product.isBookmark
            }
        }
    }

    interface ProductsCallback {
        fun onProductsClick(product: Product)
        fun updateFollowStatus(product: Product, category: Int)
    }
}