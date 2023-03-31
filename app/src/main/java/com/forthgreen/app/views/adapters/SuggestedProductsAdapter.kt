package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.SimilarProduct
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.utils.firstOrEmpty
import com.forthgreen.app.views.utils.loadURL
import kotlinx.android.synthetic.main.row_suggested_products_rv.view.*

/**
 * @author Chetan Tuteja (chetan.tuteja@gmail.com)
 * @since 19-Feb-21
 */
class SuggestedProductsAdapter(private val suggestedProductsCallback: SuggestedProductsCallback) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "SuggestedProductsAdapte"
        private const val MAX_PRODUCTS_COUNT = 8
    }

    // Variables
    private var productList = mutableListOf<SimilarProduct>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SuggestedProductViewHolder(parent.inflate(R.layout.row_suggested_products_rv))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SuggestedProductViewHolder -> {
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

    fun submitList(listWeGet: List<SimilarProduct>) {
        productList.clear()
        productList.addAll(listWeGet)
        notifyDataSetChanged()
    }

    fun updateProduct(product: SimilarProduct) {
        val index = productList.indexOfFirst { prod -> prod._id == product._id }
        if (index != -1) {
            // Remove the old product and add the updated product.
            productList.removeAt(index)
            productList.add(index, product)
            notifyItemChanged(index)
        }
    }

    inner class SuggestedProductViewHolder constructor(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                suggestedProductsCallback.onSuggestedProductsClick(productList[adapterPosition])
            }
            itemView.cbBookmark.setOnClickListener {
                suggestedProductsCallback.updateFollowStatus(productList[adapterPosition])
                itemView.cbBookmark.isChecked = !itemView.cbBookmark.isChecked
            }
        }

        fun bind(product: SimilarProduct) {
            itemView.apply {
                // Assign values
                // Load First Image or Empty String to in turn serve as placeholder.
                ivProductImage.loadURL(product.images.firstOrEmpty(), isUserImage = false)
                tvProductName.text = product.name
                tvBrandName.text = product.brandName
                tvProductPrice.text = product.productPriceText
                cbBookmark.isChecked = product.isBookmark
            }
        }
    }

    interface SuggestedProductsCallback {
        fun onSuggestedProductsClick(product: SimilarProduct)
        fun updateFollowStatus(product: SimilarProduct)
    }
}