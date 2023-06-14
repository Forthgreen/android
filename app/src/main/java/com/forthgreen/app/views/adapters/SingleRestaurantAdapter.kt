package com.forthgreen.app.views.adapters

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Restaurant
import com.forthgreen.app.utils.GeneralFunctions
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.utils.*
import kotlinx.android.synthetic.main.row_single_restaurant.view.*

/**
 * @author Chetan Tuteja (chetan.tuteja@gmail.com)
 * @since 18-Jan-21
 */
class SingleRestaurantAdapter(private val singleRestaurantCallback: SingleRestaurantCallback)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "SingleRestaurantAdapter"
    }

    // Variables
    private val restaurantList = mutableListOf<Restaurant>()
    private var showShimmer = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RestaurantViewHolder(parent.inflate(R.layout.row_single_restaurant))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RestaurantViewHolder -> {
                holder.bind(restaurantList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    fun submitList(listWeGet: List<Restaurant>, mPage: Int, mShowShimmer: Boolean) {
        if (mPage == 1) {
            restaurantList.clear()
        }
        this.showShimmer = mShowShimmer
        restaurantList.addAll(listWeGet)
        notifyDataSetChanged()
    }

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.apply {
                setOnSafeClickListener {
                    singleRestaurantCallback.onSingleRestaurantClick(
                            restaurant = restaurantList[adapterPosition], dismissCard = false)
                }
               /* ivCross.setOnSafeClickListener {
                    singleRestaurantCallback.onSingleRestaurantClick(
                            restaurant = restaurantList[adapterPosition], dismissCard = true)
                }*/
            }
        }

        fun bind(restaurant: Restaurant) {
            itemView.apply {
                if (showShimmer) {
                    groupRestaurant.gone()
//                    flShimmer.visible()
//                    flShimmer.startShimmer()
                } else {
//                    flShimmer.apply {
//                        gone()
//                        stopShimmer()
//                    }
                    groupRestaurant.visible()
                    // Assign Values
                  //  ivCross.visible()
                    tvRestaurantName.text = restaurant.name
                    // Load First Image or Empty String to in turn serve as placeholder.
                    ivRestaurantImage.loadURL(
                        restaurant.images.firstOrEmpty(),
                        false, resources.getDimension(R.dimen.rounded_radius).toInt()
                    )
                    tvRestaurantName.text = restaurant.name
                    rbRating.rating = restaurant.ratings.averageRating.toFloat()
                    tvRatingCounts.text = restaurant.ratings.count.toString()
                    tvCuisineInfo.text = restaurant.typeOfFood
                    tvDistanceInfo.text = "${GeneralFunctions.getDistanceInMiles(restaurant.distance)} miles"

                    // Set price range
                    val spanPrice: Spannable = SpannableString("$$$")
                    spanPrice.setSpan(ForegroundColorSpan(ContextCompat.getColor(context,
                            R.color.colorFollowingButton)),
                            0, restaurant.price.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    tvPrice.text = spanPrice
                }
            }
        }
    }


    interface SingleRestaurantCallback {
        fun onSingleRestaurantClick(restaurant: Restaurant, dismissCard: Boolean)
    }
}