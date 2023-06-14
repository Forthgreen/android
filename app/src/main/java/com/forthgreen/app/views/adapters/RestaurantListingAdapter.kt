package com.forthgreen.app.views.adapters

import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
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
import com.forthgreen.app.views.utils.setOnSafeClickListener
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_load_more.view.*
import kotlinx.android.synthetic.main.row_restaurant.view.*
import kotlinx.android.synthetic.main.row_search.view.*


/**
 * @author shraychona@gmail.com
 * @since 13 Oct 2020
 */
class RestaurantListingAdapter(
        private val mFragment: Fragment,
        private val mClickListener: RestaurantListingCallback,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "HomeAdapter"
      //  const val ROW_TYPE_SEARCH = 2
        const val ROW_TYPE_ELEMENT = 3
        const val ROW_TYPE_LOAD_MORE = 4
    }

    //Variables
    private val restaurantList = mutableListOf<Restaurant>()
    private val loadMoreListener by lazy { mFragment as LoadMoreListener }
    private var hasMore = false
  //  private var showSearch: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ROW_TYPE_LOAD_MORE -> LoadMoreViewHolder(parent.inflate(R.layout.row_load_more))
         /*   ROW_TYPE_SEARCH -> SearchViewHolder(parent.inflate(R.layout.row_search))*/
            else -> RestaurantViewHolder(parent.inflate(R.layout.row_restaurant))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RestaurantViewHolder -> {
              //  holder.bind(restaurantList[position-1])
                holder.bind(restaurantList[position])
            }
            is LoadMoreViewHolder -> {
                holder.bindMore()
            }
        }
    }

    override fun getItemCount(): Int {
       /* return if (hasMore) {
            restaurantList.size + if (showSearch) 2 else 1
        } else {
            restaurantList.size + if (showSearch) 1 else 0
        }*/

        return if (hasMore) {
            restaurantList.size + 1
        } else {
            restaurantList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
          /*  0 -> ROW_TYPE_SEARCH*/
            restaurantList.size-> ROW_TYPE_LOAD_MORE
            else -> ROW_TYPE_ELEMENT
        }
    }

    fun submitList(listWeGet: List<Restaurant>, mPage: Int, hasMore: Boolean, isShowSearch: Boolean = true) {
        if (mPage == 1) {
            restaurantList.clear()
        }
        this.hasMore = hasMore
      //  this.showSearch = isShowSearch
        restaurantList.addAll(listWeGet)
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                }

                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                    mClickListener.onSearchTextChange(text.toString())
                }
            })
        }

    }

    inner class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnSafeClickListener {
                mClickListener.onRestaurantClick(restaurantList[adapterPosition])
            }
        }

        fun bind(restaurant: Restaurant) {
            itemView.apply {
                // Assign Values

               /* // Load First Image or Empty String to in turn serve as placeholder.
                ivRestaurantPic.loadURL(
                    restaurant.thumbnail,
                    false, resources.getDimension(R.dimen.rounded_radius).toInt()
                )*/
                // Load First Image or Empty String to in turn serve as placeholder.
                ivRestaurantPic.loadURL(
                    restaurant.thumbnail,
                    false
                )
                tvRestaurantName.text = restaurant.name
                rattingBar.rating = restaurant.ratings.averageRating.toFloat()
                tvRatingCount.text = restaurant.ratings.count.toString()
                tvCuisine.text = restaurant.typeOfFood
                tvDistance.text = "${GeneralFunctions.getDistanceInMiles(restaurant.distance)} miles"

                // Set Price range
                val spanPrice: Spannable = SpannableString("$$$").apply {
                    setSpan(ForegroundColorSpan(ContextCompat.getColor(context,
                            R.color.colorFollowingButton)), 0, restaurant.price.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                tvPriceRange.text = spanPrice
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

    interface RestaurantListingCallback {
        fun onRestaurantClick(restaurant: Restaurant)
        fun onSearchTextChange(query: String)
    }
}