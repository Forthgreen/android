package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.fragments.ProductDetailFragment
import com.forthgreen.app.views.utils.loadURL
import kotlinx.android.synthetic.main.restaurant_detail_view_pager_layout.view.*

class ViewPagerShopFeedAdapter(val productDetailCallBack: ProductDetailCallBack) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "ViewPagerAdapter"
    }

    //variables
    private var imagesList = mutableListOf<String>()
    private var product = Product()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewPagerViewHolder(parent.inflate(R.layout.restaurant_detail_view_pager_layout))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewPagerViewHolder -> {
                holder.bind(imagesList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

    fun submitList(list: List<String>) {
        imagesList.clear()
        imagesList.addAll(list)
        notifyDataSetChanged()
    }

    fun submitList(product: Product, list: List<String>) {
        imagesList.clear()
        imagesList.addAll(list)
        this.product = product
        notifyDataSetChanged()
    }

    inner class ImageViewPagerViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(image: String) {
            itemView.apply {
                //Assign Values
                ivImage.loadURL(image, false)
            }

            itemView.setOnClickListener {
                productDetailCallBack.productDetailImageClick(product)
            }
        }
    }

    interface ProductDetailCallBack {
        fun productDetailImageClick(product: Product)
    }
}