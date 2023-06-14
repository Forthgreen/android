package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Brand
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.fragments.CategoryProductsFragment.Companion.PRODUCT_RESULTS_PER_PAGE
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_brand_list.view.*
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_products.view.*

/**
 * @author shraychona@gmail.com
 * @since 23 Jun 2020
 */
@Deprecated(message = "We do not need to use Home Adapter anymore. Since the recent Forthgreen " +
        "changes, we only need Products and its adapter.")
class HomeAdapter(private val mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "HomeAdapter"
        const val ROW_TYPE_BRANDS_LIST = 1
        const val ROW_TYPE_PRODUCT = 2
        const val ROW_TYPE_LOAD_MORE = 3
    }

    //Variables
    private var productList = mutableListOf<Product>()
    private val loadMoreListener by lazy { mFragment as LoadMoreListener }
    private val mProductDetailCallback by lazy { mFragment as ProductListAdapter.ProductDetailCallback }
    private val mBrandsAdapter by lazy { BrandAdapter(mFragment) }
    private val showBrands by lazy { mFragment as BrandAdapter.ShowBrandsCallBack }

    private var hasMore = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> ProductViewHolder(parent.inflate(R.layout.row_products))
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) {
            productList.size + 1
        } else {
            productList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            productList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_PRODUCT
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

    fun updateProductList(list: List<Product>, mPage: Int) {
        if (mPage == 1) {
            productList.clear()
        }
        this.hasMore = list.size >= PRODUCT_RESULTS_PER_PAGE
        productList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateBrandsList(list: List<Brand>) {
        mBrandsAdapter.getBrands(list)
        notifyDataSetChanged()
    }

    inner class BrandListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.tvViewMore.setOnClickListener {
                showBrands.showMoreBrands()
            }
        }

        fun bindBrands() {
            itemView.apply {
                //Assign Values
                recyclerViewBrands.apply {
                    adapter = mBrandsAdapter
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                }
            }
        }
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                mProductDetailCallback.showProductDetail(productList[adapterPosition])
            }
        }

        fun bindProducts(product: Product) {
            itemView.apply {
                //Assign Values
                if (product.images.isNotEmpty())
                    ivProductImage.loadURL(product.images.first(), false)
                tvProductName.text = product.name
                tvBrandName.text = product.brandName
                tvBrandName.visible()
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
}