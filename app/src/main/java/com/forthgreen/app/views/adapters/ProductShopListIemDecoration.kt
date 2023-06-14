package com.forthgreen.app.views.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log


/**
 * @author shraychona@gmail.com
 * @since 21 Sep 2020
 */
class ProductShopListIemDecoration(private val productSpacing: Int, private val spanCount: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // Item position
        val column = position % spanCount // Item column

        val context: Context = view.context
        val indentation: Int = 10
        val holder = parent.getChildViewHolder(view)
        if (holder is ShopFeedAdapter.ItemViewHolder) {
            Log.d("itemyes", "item $column--$position")
            if (position !=0 && position % 2 != 0) {
                outRect.right = 16
            } else {
                outRect.right = 0
            }
           // outRect.left = 10
        } else {
            Log.d("itemnot", "itemnot$column--$position")
        }

        if (holder is ShopFeedAdapter.SectionHeaderViewHolder) {
            Log.d("itemyesSect", "item $column--$position")
            if (position !=0 && position % 2 != 0) {
                outRect.right = 16
            } else {
                outRect.right = 0
            }
        }


       /* when (parent.getChildViewHolder(view)) {
            is MyRecyclerAdapter.SectionHeaderViewHolder -> {
                // For Product Recycler View, equally divide.
                outRect.left = column * productSpacing / spanCount
                outRect.right = productSpacing - (column + 1) * productSpacing / spanCount
                if (position >= spanCount) {
                    outRect.top = productSpacing // Item top
                }
            }

            is MyRecyclerAdapter.ItemViewHolder -> {
                // For Product Recycler View, equally divide.
                outRect.left = column * productSpacing / spanCount
                outRect.right = productSpacing - (column + 1) * productSpacing / spanCount
                if (position >= spanCount) {
                    outRect.top = productSpacing // Item top
                }
            }
        }*/
    }
}