package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.firstOrEmpty
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_brand_products.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*

class ProductListAdapter(mFragment: Fragment, val isShowBrandName: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "ProductAdapter"
        const val RESULTS_PER_PAGE = 30
        const val ROW_TYPE_PRODUCT = 1
        const val ROW_TYPE_LOAD_MORE = 0
    }

    //variables
    private var productList = mutableListOf<Product>()
    private val loadMoreListener by lazy { mFragment as LoadMoreListener }
    private val mProductDetailCallback by lazy { mFragment as ProductDetailCallback }

    private var hasMore = false
    private var brandNameForProducts: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> ProductViewHolder(parent.inflate(R.layout.row_brand_products))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                holder.bindProducts(productList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            productList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_PRODUCT
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) (productList.size) + 1
        else productList.size
    }

    fun updateList(list: List<Product>, mPage: Int, hasMore: Boolean, mBrandName: String = "") {
        if (mPage == 1) {
            productList.clear()
        }
        this.hasMore = hasMore
        brandNameForProducts = mBrandName
        productList.addAll(list)
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


    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                mProductDetailCallback.showProductDetail(productList[adapterPosition])
            }
            itemView.cbBookmark.setOnClickListener {
                mProductDetailCallback.updateFollowStatus(productList[adapterPosition])
                itemView.cbBookmark.isChecked = !itemView.cbBookmark.isChecked
            }
        }

        fun bindProducts(product: Product) {
            itemView.apply {
                // Assign Values

                // Load First Image or Empty String to in turn serve as placeholder.
                ivProductImage.loadURL(
                    product.images.firstOrEmpty(),
                    false
                )
                tvProductName.text = product.name
                tvBrandName.text = product.brandName
                if (product.brandName.isEmpty()) {
                    tvBrandName.text = brandNameForProducts
                }
                tvProductPrice.text = product.productPriceText
                cbBookmark.isChecked = product.isBookmark
            }
        }
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMore() {
            if (hasMore) {
                //Assign Values
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMoreListener.onLoadMore()
            }
        }
    }

    interface ProductDetailCallback {
        fun showProductDetail(product: Product)
        fun updateFollowStatus(product: Product)
    }
}