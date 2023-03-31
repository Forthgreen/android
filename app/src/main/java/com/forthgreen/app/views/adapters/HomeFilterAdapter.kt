package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.R
import com.forthgreen.app.repository.models.Filter
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.utils.gone
import com.forthgreen.app.views.utils.visible
import kotlinx.android.synthetic.main.row_single_filter.view.*

class HomeFilterAdapter(private val mHomeFilterCallbacks: HomeFilterCallbacks) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "Preferences"
    }

    private var filterList = arrayListOf<Filter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HomeFilterViewHolder(parent.inflate(R.layout.row_single_filter))
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeFilterViewHolder -> {
                holder.bindFilters(filterList[position])

            }
        }
    }

    fun updateList(list: ArrayList<Filter>) {
        filterList.clear()
        filterList.addAll(list)
        notifyDataSetChanged()
    }

    fun isSelectAll(select: Boolean) {
        filterList.forEach { it.isSelected = select }
        notifyDataSetChanged()
    }

    fun getFilterList() = filterList

    inner class HomeFilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                // Invert Checkbox
                filterList[adapterPosition].isSelected = !filterList[adapterPosition].isSelected
                mHomeFilterCallbacks.onFilterChange(filterList)
                notifyDataSetChanged()
            }
        }

        fun bindFilters(filter: Filter) {
            itemView.apply {
                // Assign values
                if (filter.resourceId != 0) {
                    ivProductIcon.visible()
                    ivProductIcon.setImageResource(filter.resourceId)
                } else {
                    ivProductIcon.gone()
                }
                tvHomeFilter.text = filter.name
                if (filter.isSelected) ivChecked.visible() else ivChecked.gone()
            }

        }
    }

    interface HomeFilterCallbacks {
        fun onFilterChange(filterList: List<Filter>)
    }
}