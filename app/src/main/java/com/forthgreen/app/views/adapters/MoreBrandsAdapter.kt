package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Brand
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_single_brand.view.*

class MoreBrandsAdapter(val showDetail: ShowBrandDetail, val loadMore: LoadMoreListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "MoreBrandsAdapter"
        const val BRANDS = 2
        const val LOAD = 7
    }

    //variables
    private var brandList = mutableListOf<Brand>()
    private var hasMore: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BRANDS -> MoreBrandsViewHolder(parent.inflate(R.layout.row_single_brand))
            else -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MoreBrandsViewHolder -> {
                holder.bindBrands(brandList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            brandList.size -> LOAD
            else -> BRANDS

        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) brandList.size + 1
        else brandList.size
    }

    fun getMoreBrands(brands: List<Brand>, mPage: Int, hasMore: Boolean) {
        if (mPage == 1) {
            brandList.clear()
        }
        brandList.addAll(brands)
        this.hasMore = hasMore
        notifyDataSetChanged()

    }

    inner class MoreBrandsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                showDetail.showBrandDetail(brandList[adapterPosition])
            }
        }

        fun bindBrands(brands: Brand) {
            itemView.apply {
                //assign
                ciBrandImage.loadURL(brands.logo,false)
                tvBrandNameLabel.text=brands.brandName
                tvBrandFollowers.text= "${brands.followers} followers"
            }
        }
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMore() {
            if (hasMore) {
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMore.onLoadMore()
            }
        }
    }

    interface ShowBrandDetail {
        fun showBrandDetail(brands: Brand)
    }
}