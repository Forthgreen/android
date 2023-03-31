package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.ReviewData
import com.forthgreen.app.repository.models.UserProfile
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_restaurant_reviews_rv.view.*

class RestaurantReviewsAdapter(val onClick: MenuClickCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "RestaurantReviewsAdapter"
    }

    //variables
    private var reviewsList = mutableListOf<ReviewData>()
    private var userId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReviewsViewHolder(parent.inflate(R.layout.row_restaurant_reviews_rv))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReviewsViewHolder -> {
                holder.bind(reviewsList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return reviewsList.size
    }

    fun submitList(list: List<ReviewData>, mUserId: String = "") {
        reviewsList.addAll(list)
        this.userId = mUserId
        notifyDataSetChanged()
    }

    inner class ReviewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.ivMenu.setOnClickListener {
                onClick.openMenuOptions(reviewsList[adapterPosition])
            }
            itemView.civUserImage.setOnClickListener {
                onClick.openUserProfile(reviewsList[adapterPosition].userDetails)
            }
            itemView.tvName.setOnClickListener {
                onClick.openUserProfile(reviewsList[adapterPosition].userDetails)
            }
        }

        fun bind(review: ReviewData) {
            itemView.apply {

                //Hide three dot menu for self review by comparing userId.
                if (review.userDetails._id == userId) {
                    ivMenu.gone()
                } else {
                    ivMenu.visible()
                }
                //assign values
                civUserImage.loadURL(review.userDetails.image, true)
                tvName.text = "${review.userDetails.firstName}"
                rbRating.rating = review.rating.toFloat()
                tvReviewTitle.text = review.title
                tvReview.text = review.review
            }
        }
    }

    interface MenuClickCallback {
        fun openMenuOptions(review: ReviewData)
        fun openUserProfile(user: UserProfile)
    }
}