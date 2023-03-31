package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Brand
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.MainActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.fragments.BrandDetailFragment
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_single_mybrand.view.*

class MyBrandsAdapter(val fragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "MyBrandsAdapter"
        const val LOAD_MORE = 1
        const val MY_BRANDS = 5
    }

    private var hasMore = false
    private val loadMoreListener by lazy { fragment as LoadMoreListener }
    private var brandsList = mutableListOf<Brand>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MY_BRANDS -> BrandsViewHolder(parent.inflate(R.layout.row_single_mybrand))
            else -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BrandsViewHolder -> {
                holder.bindBrands(brandsList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) brandsList.size + 1
        else brandsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            brandsList.size -> LOAD_MORE
            else -> MY_BRANDS
        }
    }

    fun updateList(brandList: List<Brand>, page: Int, hasMore: Boolean) {
        if (page == 1) {
            brandsList.clear()
        }
        brandsList.addAll(brandList)
        this.hasMore = hasMore
        notifyDataSetChanged()
        checkBrandUnreadCount()
    }

    inner class BrandsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val brand = brandsList[adapterPosition]
                brand.count = 0
                performTransaction(
                    BrandDetailFragment.newInstance(brand), BrandDetailFragment.TAG,
                    enterAnim = R.anim.slide_in_right, exitAnim = R.anim.fade_out,
                    popEnterAnim = R.anim.fade_in, popExitAnim = R.anim.slide_out_right
                )
                notifyItemChanged(adapterPosition)
                checkBrandUnreadCount()
            }
        }

        fun bindBrands(brand: Brand) {
            itemView.apply {
                tvBrandName.text = brand.brandName
                ciBrandImage.loadURL(brand.logo, false)
                if (brand.count == 0) {
                    ivBrandNotify.gone()
                } else {
                    ivBrandNotify.visible()
                }
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

    private fun performTransaction(
        frag: Fragment,
        fragmentTag: String,
        enterAnim: Int,
        exitAnim: Int,
        popEnterAnim: Int,
        popExitAnim: Int
    ) {
        val perform = fragment.activity as BaseAppCompactActivity
        perform.doFragmentTransaction(
            fragment.supportFragmentManager(),
            R.id.flFragContainer,
            frag,
            fragmentTag,
            isAddFragment = false,
            enterAnimation = enterAnim,
            exitAnimation = exitAnim,
            popEnterAnimation = popEnterAnim,
            popExitAnimation = popExitAnim
        )
    }

    private fun checkBrandUnreadCount() {
        (fragment.activity as MainActivity).showMyBrandsIndicator(brandsList.any { it.count > 0 })
    }

    fun ImageView.loadURL(imageURL: String, isUserImage: Boolean) {
        //Get complete Image URL using Identifier received.
        val minURL = GeneralFunctions.getResizedImage(ValueMapping.getPathSmall(), imageURL)
        val bestURL = GeneralFunctions.getResizedImage(ValueMapping.getPathBest(), imageURL)

        if (isUserImage) {
            Glide.with(this)
                .load(bestURL)
                .thumbnail(Glide.with(this).load(minURL))
                .placeholder(R.drawable.ic_brand_placeholder)
                .error(R.drawable.ic_brand_placeholder)
                .into(this)
        } else {
            Glide.with(this)
                .load(bestURL)
                .thumbnail(Glide.with(this).load(minURL))
                .placeholder(R.drawable.ic_brand_placeholder)
                .error(R.drawable.ic_brand_placeholder)
                .into(this)
        }
    }
}