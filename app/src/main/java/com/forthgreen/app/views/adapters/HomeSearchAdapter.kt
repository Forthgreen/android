package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Brand
import com.forthgreen.app.repository.models.Product
import com.forthgreen.app.repository.models.ProductSearch
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.views.activities.BaseAppCompactActivity
import com.forthgreen.app.views.activities.doFragmentTransaction
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.fragments.BrandDetailFragment
import com.forthgreen.app.views.fragments.ProductDetailFragment
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.supportFragmentManager
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_search_product.view.*

class HomeSearchAdapter(val mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TAG = "HomeSearchAdapter"
    }

    //Variables
    private val brandProductList = mutableListOf<ProductSearch>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BrandProductViewHolder(parent.inflate(R.layout.row_search_product))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BrandProductViewHolder -> {
                holder.bindBrandProduct(brandProductList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return brandProductList.size
    }

    fun submitList(listWeGet: List<ProductSearch>) {
        brandProductList.clear()
        brandProductList.addAll(listWeGet)
        notifyDataSetChanged()
    }

    inner class BrandProductViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                GeneralFunctions.hideKeyboard(mFragment)
                if (brandProductList[adapterPosition].isProduct) {
                    val mProduct = brandProductList[adapterPosition]

                    //Do Fragment Transaction and Send Product as Bundle
                    (mFragment.activity as BaseAppCompactActivity).doFragmentTransaction(mFragment.supportFragmentManager(),
                            R.id.flFragContainer, ProductDetailFragment.newInstance(Product(_id = mProduct._id, name = mProduct.name,
                            price = mProduct.price, images = mProduct.images)))
                } else {
                    val mBrand = brandProductList[adapterPosition]

                    //Do Fragment Transaction and Send Brand as Bundle
                    (mFragment.activity as BaseAppCompactActivity).doFragmentTransaction(mFragment.supportFragmentManager(),
                            R.id.flFragContainer, BrandDetailFragment.newInstance(Brand(_id = mBrand._id, brandName = mBrand.name,
                            coverImage = mBrand.coverImage, about = mBrand.about, logo = mBrand.logo)))
                }
            }
        }

        fun bindBrandProduct(brandProduct: ProductSearch) {
            itemView.apply {
                //Assign Values according to if it is brand or product
                if (brandProduct.isProduct) {
                    groupBrand.gone()
                    groupProduct.visible()

                    tvProductName.text = brandProduct.name
                    tvBrand.text = brandProduct.brand.name
                    //If images is not empty, set the first image.
                    if (brandProduct.images.isNotEmpty()) {
                        ivProduct.loadURL(brandProduct.images.first(), false)
                    }
                } else {
                    groupBrand.visible()
                    groupProduct.gone()

                    tvBrandName.text = brandProduct.name
                    ciBrandImage.loadURL(brandProduct.logo, false)
                }
            }
        }
    }
}