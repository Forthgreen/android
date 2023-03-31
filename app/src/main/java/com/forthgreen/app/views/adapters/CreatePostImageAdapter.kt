package com.forthgreen.app.views.adapters

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.forthgreen.app.R
import com.forthgreen.app.views.activities.inflate
import com.forthgreen.app.views.utils.setOnSafeClickListener
import kotlinx.android.synthetic.main.row_layout_create_post_gallery.view.*

/**
 * @author Chetan Tuteja (chetan.tuteja@gmail.com)
 * @since 01-May-21
 */
class CreatePostImageAdapter(private val createPostCallback: CreatePostCallback)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TAG = "CreatePostImageAdapter"
    }

    // Variables
    private val postImages = mutableListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder(parent.inflate(R.layout.row_layout_create_post_gallery))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(postImages[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return postImages.size
    }

    fun submitList(imageList: List<Uri>) {
        if (imageList.isNotEmpty()) {
            postImages.addAll(imageList)
            notifyDataSetChanged()
        }
    }

    fun submitImage(image: Uri) {
        postImages.add(image)
        notifyItemInserted(postImages.lastIndex)
    }

    fun fetchSelectedImages(): List<Uri> = postImages

    inner class ImageViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.ivCross.setOnSafeClickListener {
                // Remove image and give a callback.
                postImages.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                createPostCallback.imageRemoved()
            }
        }

        fun bind(imageUri: Uri) {
            itemView.apply {
                // Assign Values
                Glide.with(this)
                        .load(imageUri)
                        .error(R.drawable.ic_placeholder_broken_img)
                        .into(sivPostImage)
            }
        }
    }

    interface CreatePostCallback {
        fun imageRemoved()
    }
}