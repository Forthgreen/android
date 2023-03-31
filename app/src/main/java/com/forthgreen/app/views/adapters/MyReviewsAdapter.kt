package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Review
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_my_reviews_rv.view.*

/**
 * Created by Chetan Tuteja on 16-May-20.
 */
class MyReviewsAdapter(val loadMore: LoadMoreListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "MyReviewsAdapter"
        private const val ROW_TYPE_ELEMENTS = 1
        const val ROW_TYPE_LOAD_MORE = 12
    }

    //Variables
    private var hasMore = false
    private var myReviews = mutableListOf<Review>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> MyReviewsViewHolder(parent.inflate(R.layout.row_my_reviews_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
            is MyReviewsViewHolder -> {
                holder.bindReviews(myReviews[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) {
            myReviews.size + 1
        } else {
            myReviews.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            myReviews.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENTS
        }
    }

    fun myReviews(reviews: List<Review>, page: Int, hasMore: Boolean) {
        if (page == 1) {
            myReviews.clear()
        }
        myReviews.addAll(reviews)
        this.hasMore = hasMore
        notifyDataSetChanged()
    }

    inner class MyReviewsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindReviews(review: Review) {
            itemView.apply {
                //Assign Values
                tvProductNameTop.text = review.name
                rbProductRating.rating = review.rating.toFloat()
                tvReviewTitle.text = review.title
                tvReview.text = review.review
                if (review.images.isNotEmpty())
                    ivProductImage.loadURL(review.images[0], false)
            }
        }
    }

    inner class LoadMoreViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindMore() {
            if (hasMore) {
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMore.onLoadMore()
            }
        }
    }
}