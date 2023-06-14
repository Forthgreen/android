package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.utils.Constants
import com.forthgreen.app.views.activities.inflate
import kotlinx.android.synthetic.main.row_category_list.view.*

class CategoryListAdapter(val mFragment: Fragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val categoryList: ArrayList<Filter> =  Constants.categoriesList
    private val clickCallback by lazy { mFragment as ShopCategoriesAdapter.CategoryClickCallback }

   inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.apply {
                categoryItem.setOnClickListener {
                    clickCallback.showProductsOfCategory(categoryList[adapterPosition])
                }
            }
        }
        fun bindCategoryProduct(categoryItem: Filter) {
            itemView.apply {
                tvBrandName.text = categoryItem.name
                Glide.with(this)
                    .load(categoryItem.resourceId)
                    .into(civUserImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(parent.inflate(R.layout.row_category_list))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is CategoryViewHolder -> {
                holder.bindCategoryProduct(categoryList[position])
            }
        }
    }

    interface CategoryClickCallback {
        fun showProductsOfCategory(category: Filter)
    }
}