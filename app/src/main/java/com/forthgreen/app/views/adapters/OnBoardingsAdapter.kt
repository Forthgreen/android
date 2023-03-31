package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.OnBoardings
import com.forthgreen.app.views.activities.inflate
import kotlinx.android.synthetic.main.on_boardings_view_pager_layout.view.*

/**
 * @author Nandita Gandhi
 * @since 10-04-2021
 */
class OnBoardingsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "OnBoardingsAdapter"
    }

    //variables
    private var onBoardingData = mutableListOf<OnBoardings>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OnBoardingsViewHolder(parent.inflate(R.layout.on_boardings_view_pager_layout))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OnBoardingsViewHolder -> {
                holder.bind(onBoardingData[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return onBoardingData.size
    }

    fun submitList(onBoarding: List<OnBoardings>) {
        onBoardingData.clear()
        onBoardingData.addAll(onBoarding)
        notifyDataSetChanged()
    }

    inner class OnBoardingsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(onBoardings: OnBoardings) {
            itemView.apply {
                civonBoarding.setImageResource(onBoardings.boardingImage)
                tvTitle.text = resources.getString(onBoardings.title)
                tvDescription.text = resources.getString(onBoardings.description)
            }
        }
    }
}