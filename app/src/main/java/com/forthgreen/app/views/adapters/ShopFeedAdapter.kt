package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.utils.ValueMapping
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.firstOrEmpty
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_product_list_item.view.cbBookmark
import kotlinx.android.synthetic.main.row_product_list_item.view.ivProductImage
import kotlinx.android.synthetic.main.row_product_list_item.view.tvBrandName
import kotlinx.android.synthetic.main.row_product_list_item.view.tvProductName
import kotlinx.android.synthetic.main.row_product_list_item.view.tvProductPrice
import kotlinx.android.synthetic.main.row_product_section_header.view.*


class ShopFeedAdapter(val loadMoreListener: LoadMoreListener, val mFragment: Fragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    ViewPagerShopFeedAdapter.ProductDetailCallBack {

    companion object {
        const val VIEW_TYPE_HEADER = 2
        const val VIEW_TYPE_ITEM = 1
        var shopFeedList = mutableListOf<Product>()
        var isLoadingAdded: Boolean = false
        var isHeaderEven: Boolean = true
    }

    private val productClickCallback by lazy {
        mFragment as ProductsCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> SectionHeaderViewHolder(parent.inflate(R.layout.row_product_section_header))
            else -> ItemViewHolder(parent.inflate(R.layout.row_product_list_item))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SectionHeaderViewHolder -> {
                holder.bind(shopFeedList[position])
            }
            is ItemViewHolder -> {
                holder.bind(shopFeedList[position], position)
            }
        }
    }

    fun updateProduct(product: Product) {
        val index = shopFeedList.indexOfFirst { prod -> prod._id == product._id }
        if (index != -1) {
            // Remove the old product and add the updated product.
            shopFeedList.removeAt(index)
            shopFeedList.add(index, product)
            notifyItemChanged(index)
        }
    }

    fun submitList(listWeGet: List<Product>, hasMore: Boolean, page: Int) {
        if (page == 1) {
            shopFeedList.clear()
        }
        isLoadingAdded = hasMore
        shopFeedList.addAll(listWeGet)
        notifyDataSetChanged()
    }

    fun clearList() {
        shopFeedList.clear()
        notifyDataSetChanged()
    }

    /* override fun getItemViewType(position: Int): Int {
         return if (position % 7 == 0 && position != shopFeedList.size-1) {
             VIEW_TYPE_HEADER
         } else if (position == shopFeedList.size && isLoadingAdded){
             VIEW_TYPE_LOADER
         }else {
             VIEW_TYPE_ITEM
         }
     }*/

    override fun getItemViewType(position: Int): Int {
        return if (position % 7 == 0) {
            isHeaderEven = position % 2 == 0
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return shopFeedList.size
    }

    inner class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.ivProductImageSectionHeader.setOnClickListener {
                productClickCallback.onBrandProductImageClick(shopFeedList[adapterPosition])
            }
            itemView.setOnClickListener {
                productClickCallback.onProductsClick(shopFeedList[adapterPosition])
            }
        }

        fun bind(product: Product) {
            if (adapterPosition == 0) {
                itemView.apply {
                    viewTopDivider.gone()
                }
            } else {
                itemView.apply {
                    viewTopDivider.visible()
                }
            }
            itemView.apply {
                itemView.sectionHeaderConstraintLayout.setOnClickListener {
                    productClickCallback.onProductsClick(shopFeedList[adapterPosition])
                }
                product.let {
                    val brandName = product.brandName
                    tvProductNameTop.text = brandName
                    tvProductNameSectionHeader.text = product.name
                    tvProductPriceSectionHeader.text = product.productPriceText

                    val mImageViewPagerAdapter = ViewPagerShopFeedAdapter(this@ShopFeedAdapter)
                    viewPager.adapter = mImageViewPagerAdapter
                    circlePagerIndicatorShop.setViewPager2(viewPager)
                    if (product.images.isNotEmpty()) {
                       // mImageViewPagerAdapter.submitList(category.images as ArrayList<String>)
                        mImageViewPagerAdapter.submitList(product, product.images as ArrayList<String>)
                    }

                    val minURL =
                        GeneralFunctions.getResizedImage(ValueMapping.getPathSmall(), product.logo)
                    Glide.with(this)
                        .load(minURL)
                        .placeholder(R.drawable.ic_brand_placeholder)
                        .error(R.drawable.ic_brand_placeholder)
                        .into(ivProductImageSectionHeader)
                }
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.cbBookmark.setOnClickListener {
                productClickCallback.updateFollowStatus(shopFeedList[adapterPosition])
            }

            itemView.setOnClickListener {
                productClickCallback.onProductsClick(shopFeedList[adapterPosition])
            }
        }

        fun bind(product: Product, position: Int) {
            itemView.apply {
                if (position % 7 == 0) {
                    setMargins(itemView, 0, 0, 0, 0)
                } else if (isHeaderEven) {
                    if (position % 2 == 0) {
                        setMargins(itemView, 14, 0, 0, 0)
                    } else {
                        setMargins(itemView, 0, 0, 14, 0)
                    }
                } else {
                    if (position % 2 == 0) {
                        setMargins(itemView, 0, 0, 14, 0)
                    } else {
                        setMargins(itemView, 14, 0, 0, 0)
                    }
                }
                product.let {
                    val brandName = product.brandName
                    tvBrandName.text = brandName
                    tvProductName.text = product.name
                    tvProductPrice.text = product.productPriceText
                    cbBookmark.isChecked = product.isBookmark
                    // Load First Image or Empty String to in turn serve as placeholder.
                    ivProductImage.loadURL(
                        product.images.firstOrEmpty(),
                        false
                    )
                }
            }
        }
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    interface ProductsCallback {
        fun onProductsClick(product: Product)
        fun onBrandProductImageClick(product: Product)
        fun updateFollowStatus(product: Product)
    }

    override fun productDetailImageClick(product: Product) {
        productClickCallback.onProductsClick(product)
    }
}

