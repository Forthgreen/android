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
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_my_product.view.*

class MyProductListAdapter(mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> ProductViewHolder(parent.inflate(R.layout.row_my_product))
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

    fun updateList(list: List<Product>, mPage: Int, hasMore: Boolean) {
        if (mPage == 1) {
            productList.clear()
        }
        this.hasMore = hasMore
        productList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                mProductDetailCallback.showProductDetail(productList[adapterPosition])
            }
        }

        fun bindProducts(product: Product) {
            itemView.apply {
                // Assign Values
                product.apply {
                    // Load First Image or Empty String to in turn serve as placeholder.
                    ivProductImage.loadURL(images.firstOrEmpty(), false, resources.getDimension(R.dimen.rounded_radius).toInt())
                    tvProductName.text = name
                    tvBrandName.text = brandName
                    tvProductPrice.text = productPriceText
                }
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
    }
}