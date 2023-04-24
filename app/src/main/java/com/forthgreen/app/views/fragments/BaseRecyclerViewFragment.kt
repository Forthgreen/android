package com.forthgreen.app.views.fragments

import androidx.lifecycle.Observer
import android.view.View
import com.forthgreen.app.R
import kotlinx.android.synthetic.main.fragment_base_recycler_view.*

/**
 * Created by ShrayChona on 04-10-2018.
 * @description extend this class for fragment setup with 1 recyclerView
 */
abstract class BaseRecyclerViewFragment : BaseFragment() {

    override fun init() {
        // Set SwipeRefreshLayout
        if (null != swipeRefreshLayout) {
            swipeRefreshLayout!!.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent,
                    R.color.colorAccent, R.color.colorAccent)
            swipeRefreshLayout!!.setOnRefreshListener { onPullDownToRefresh() }
        }

        setData()

        // Set RecyclerView
        recyclerView.layoutManager = if (null == layoutManager) androidx.recyclerview.widget.LinearLayoutManager(activity) else (layoutManager)
        if (isShowRecyclerViewDivider) {
            // custom divider can be used
//            val dividerItemDecoration = DividerItemDecoration(ContextCompat.getDrawable(activityContext, R.drawable.drawable_top_bottom_color_divider_stroke)!!)
//            recyclerView.addItemDecoration(dividerItemDecoration)

            // native divider is being used
            recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(activity,
                    androidx.recyclerview.widget.LinearLayoutManager.VERTICAL))
        }
        recyclerView.adapter = recyclerViewAdapter

        // Observe SwipeRefresh Layout
        viewModel?.isShowSwipeRefreshLayout()?.observe(this, Observer {
            if (it!!) {
                showSwipeRefreshLoader()
            } else {
                hideSwipeRefreshLoader()
            }
        })

        // Observe Retrofit Errors
        viewModel?.getMessage()?.observe(viewLifecycleOwner, Observer {
            showNoDataText(it?.resId, it?.message)
        })
    }

    /**
     *  @description call this method to when no data is found for recycler view to show some message
     *  @param message {String} String message to be shown (if null method will use resId to show text)
     *  @param resId {Int?} resource id is string (will be used if message value is null)
     */
    fun showNoDataText(resId: Int?, message: String? = null) {
        if (null == resId && null == message) {
            hideNoDataText()
        } else {
            if (0 < recyclerViewAdapter?.itemCount!!) {
                showMessage(resId, message)
            } else {
                tvNoData?.visibility = View.VISIBLE
                tvNoData?.text = message ?: getString(resId!!)
            }
        }
    }

    fun showNoDataTextMessage(resId: Int?, message: String? = null) {
        if (null == resId && null == message) {
            hideNoDataText()
        } else {
            tvNoData?.visibility = View.VISIBLE
            tvNoData?.text = message ?: getString(resId!!)
        }
    }

    /**
     *  @description call this method to hide NoDataText
     */
    fun hideNoDataText() {
        tvNoData?.visibility = View.GONE
    }

    /**
     *  @description call this method to show progress dialog
     */
    fun showSwipeRefreshLoader() {
        swipeRefreshLayout?.post {
            if (null != swipeRefreshLayout) {
                swipeRefreshLayout!!.isRefreshing = true
            }
        }
    }

    /**
     *  @description call this method to hide progress dialog
     */
    fun hideSwipeRefreshLoader() {
        if (null != swipeRefreshLayout && swipeRefreshLayout!!.isRefreshing) {
            swipeRefreshLayout!!.isRefreshing = false
        }
    }

    abstract fun setData()

    abstract val recyclerViewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>?

    abstract val layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager?

    abstract val isShowRecyclerViewDivider: Boolean

    abstract fun onPullDownToRefresh()

}