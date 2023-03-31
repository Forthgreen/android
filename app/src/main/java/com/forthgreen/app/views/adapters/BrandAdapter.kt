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
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_brands.view.*
import kotlinx.android.synthetic.main.row_textview.view.*

class BrandAdapter(private val mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "Brands"
        const val ROW_TYPE_BRANDS = 1
        const val ROW_TYPE_VIEW_MORE = 5
    }

    private var brandList = mutableListOf<Brand>()
    private val showBrands by lazy { mFragment as ShowBrandsCallBack }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BrandViewHolder(parent.inflate(R.layout.row_brands))
    }

//    override fun getItemViewType(position: Int): Int {
//        return when (position) {
//            brandList.size -> ROW_TYPE_VIEW_MORE
//            else -> ROW_TYPE_BRANDS
//        }
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BrandViewHolder -> {
                holder.bindBrandImage(brandList[position])
            }
            is TextViewHolder -> {
                holder.bindText()
            }
        }
    }

    override fun getItemCount(): Int {
        return brandList.size
    }

    fun getBrands(list: List<Brand>) {
        brandList.clear()
        brandList.addAll(list)
        notifyDataSetChanged()

    }


    inner class BrandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                showBrands.showBrandDetail(brandList[adapterPosition])
            }
        }

        fun bindBrandImage(brand: Brand) {
            itemView.apply {
                //Assign values
                civBrandImage.loadURL(brand.logo, false)
                tvBrandName.text = brand.brandName
            }
        }
    }

    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                showBrands.showMoreBrands()
            }
        }

        fun bindText() {
            itemView.apply {
                //Assign values
                tvViewMore.visible()
            }
        }
    }

    interface ShowBrandsCallBack {
        fun showMoreBrands()
        fun showBrandDetail(brand: Brand)
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