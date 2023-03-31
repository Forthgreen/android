package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.RatingAndReview
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_product_review_rv.view.*

/**
 * Created by Chetan Tuteja on 15-May-20.
 */
class ProductReviewAdapter(val mClickListener: ProductReviewClickListener, val loadMoreListener: LoadMoreListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "ProductReviewAdapter"
        const val RESULTS_PER_PAGE = 30
        const val ROW_TYPE_REVIEW = 1
        const val ROW_TYPE_LOAD_MORE = 0
    }

    //Variables
    private var hasMore = false
    private var reviewList = mutableListOf<RatingAndReview>()
    private var mUserId = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> ProductReviewViewHolder(parent.inflate(R.layout.row_product_review_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductReviewViewHolder -> {
                holder.bind(reviewList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hasMore) {
            reviewList.size + 1
        } else {
            reviewList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            reviewList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_REVIEW
        }
    }

    fun receiveList(list: List<RatingAndReview>, page: Int, mUserId: String) {
        if (page == 1) {
            reviewList.clear()
        }
        this.mUserId = mUserId
        this.hasMore = list.size >= RESULTS_PER_PAGE
        reviewList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ProductReviewViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.ivMenu.setOnClickListener {
                mClickListener.performMenuClick(reviewList[adapterPosition])
            }
            itemView.tvUserName.setOnClickListener {
                mClickListener.openUserProfile(reviewList[adapterPosition])
            }
            itemView.civUserImage.setOnClickListener {
                mClickListener.openUserProfile(reviewList[adapterPosition])
            }
        }

        fun bind(review: RatingAndReview) {
            itemView.apply {
                //Hide three dot menu for self review by comparing userId.
                if (review.userRef == mUserId) {
                    ivMenu.gone()
                } else {
                    ivMenu.visible()
                }

                //Assign values
                tvUserName.text = review.fullName
                civUserImage.loadURL(review.image, false)
                rbUserRating.rating = review.rating
                tvReviewTitle.text = review.title
                tvReview.text = review.review
            }
        }
    }

    inner class LoadMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindMore() {
            if (hasMore) {
                //Assign Values
                itemView.tvNoMoreData.gone()
                itemView.progressBar.visible()
                loadMoreListener.onLoadMore()
            }
        }
    }

    //Interface for click Listener to handle callbacks in Fragment
    interface ProductReviewClickListener {
        fun performMenuClick(review: RatingAndReview)
        fun openUserProfile(review: RatingAndReview)
    }
}