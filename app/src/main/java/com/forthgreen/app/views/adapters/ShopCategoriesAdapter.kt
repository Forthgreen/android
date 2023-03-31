package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_shop_categories_rv.view.*

/**
 * @author Nandita Gandhi
 * @since 05-Aug-2021
 */
class ShopCategoriesAdapter(val loadMoreListener: LoadMoreListener, val mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "ShopCategoriesAdapter"
        const val ROW_TYPE_ELEMENT = 1
        const val ROW_TYPE_LOAD_MORE = 2
    }

    // Variables
    private var hasMore: Boolean = false
    private var index = 0
    private val categoriesList = arrayListOf<Filter>()
    private val clickCallback by lazy { mFragment as CategoryClickCallback }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> ShoCategoriesViewHolder(parent.inflate(R.layout.row_shop_categories_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShoCategoriesViewHolder -> {
                holder.bind(categoriesList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) {
            categoriesList.size + 1
        } else {
            categoriesList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            categoriesList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENT
        }
    }

    fun updateProductBookmark(filter: Filter, scrollIndex: Int) {
        this.index = scrollIndex
        val index = categoriesList.indexOfFirst { category -> category.id == filter.id }
        if (index != -1) {
            categoriesList.removeAt(index)
            categoriesList.add(index, filter)
            notifyItemChanged(index)
        }
    }

    fun submitList(list: ArrayList<Filter>, hasMore: Boolean) {
        categoriesList.clear()
        categoriesList.addAll(list)
        this.hasMore = hasMore
        notifyDataSetChanged()
    }

    inner class ShoCategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.apply {
                tvViewAll.setOnClickListener {
                    clickCallback.showProductsOfCategory(categoriesList[adapterPosition])
                }
            }
        }

        fun bind(category: Filter) {
            itemView.apply {
                category.apply {
                    tvCategoryName.text = name
                }
                val productsAdapter = ShopProductsAdapter(mFragment)
                recyclerViewProducts.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    adapter = productsAdapter
                    scrollToPosition(index)
                }
                productsAdapter.submitList(category.products, category.id)
            }
        }
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMore() {
            if (hasMore) {
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMoreListener.onLoadMore()
            }
        }
    }

    interface CategoryClickCallback {
        fun showProductsOfCategory(category: Filter)
    }
}