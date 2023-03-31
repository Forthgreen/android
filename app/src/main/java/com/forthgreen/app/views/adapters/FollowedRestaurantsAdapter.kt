package com.forthgreen.app.views.adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Restaurant
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.interfaces.LoadMoreListener
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.loadURL
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_restaurant.view.*

class FollowedRestaurantsAdapter(private val mFragment: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "FollowedRestaurantsAdapter"
        const val ROW_TYPE_ELEMENT = 2
        const val ROW_TYPE_LOAD_MORE = 3
    }

    private val loadMoreListener by lazy { mFragment as LoadMoreListener }
    private val mOnClick by lazy { mFragment as RestaurantClickCallback }
    private var hasMore = false
    private var restaurantsList = mutableListOf<Restaurant>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
            else -> RestaurantViewHolder(parent.inflate(R.layout.row_restaurant))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RestaurantViewHolder -> {
                holder.bind(restaurantsList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurantsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            restaurantsList.size -> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENT
        }
    }

    fun updateList(list: List<Restaurant>, page: Int, hasMore: Boolean) {
        if (page == 1) {
            restaurantsList.clear()
        }
        restaurantsList.addAll(list)
        this.hasMore = hasMore
        notifyDataSetChanged()
    }

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                mOnClick.openDetails(restaurantsList[adapterPosition])
            }
        }

        fun bind(restaurant: Restaurant) {
            itemView.apply {
                // set price range
                val spanPrice: Spannable = SpannableString("$$$")
                spanPrice.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            mFragment.requireContext(),
                            R.color.colorFollowingButton
                        )
                    ), 0, restaurant.price.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tvPriceRange.text = spanPrice

                //assign values
                if (restaurant.images.isNotEmpty()) {
                    ivRestaurantPic.loadURL(
                        restaurant.images.first(),
                        false,
                        resources.getDimension(R.dimen.rounded_radius).toInt()
                    )
                }
                tvRestaurantName.text = restaurant.name
                rattingBar.rating = restaurant.ratings.averageRating.toFloat()
                tvRatingCount.text = "${restaurant.ratings.count}"
                tvCuisine.text = restaurant.typeOfFood
                tvDistance.text =
                    "${GeneralFunctions.getDistanceInMiles(restaurant.distance)} miles"
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

    interface RestaurantClickCallback {
        fun openDetails(restaurant: Restaurant)
    }
}