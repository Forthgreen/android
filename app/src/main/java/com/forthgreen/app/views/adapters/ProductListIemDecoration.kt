package com.forthgreen.app.views.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author shraychona@gmail.com
 * @since 21 Sep 2020
 */
class ProductListIemDecoration(private val productSpacing: Int, private val spanCount: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // Item position
        val column = position % spanCount // Item column

        when (parent.getChildViewHolder(view)) {
            is ProductListAdapter.ProductViewHolder -> {
                // For Product Recycler View, equally divide.
                outRect.left = column * productSpacing / spanCount
                outRect.right = productSpacing - (column + 1) * productSpacing / spanCount
                if (position >= spanCount) {
                    outRect.top = productSpacing // Item top
                }
            }
        }
    }
}