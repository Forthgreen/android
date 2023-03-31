package com.forthgreen.app.views.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.forthgreen.app.repository.models.Contact
import com.forthgreen.app.views.activities.inflate
import kotlinx.android.synthetic.main.row_home_users_rv.view.*
import kotlinx.android.synthetic.main.row_invites_users_rv.view.*
import kotlinx.android.synthetic.main.row_invites_users_rv.view.civUserImage
import kotlinx.android.synthetic.main.row_invites_users_rv.view.tvUserFullName
import kotlinx.android.synthetic.main.row_invites_users_rv.view.tvUserName
import kotlinx.android.synthetic.main.row_invites_users_section.view.*
import android.graphics.BitmapFactory

import android.provider.ContactsContract

import android.content.ContentUris


import android.graphics.Bitmap

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.transition.Transition
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import java.io.IOException
import java.lang.Exception

import com.forthgreen.app.R
import java.io.ByteArrayOutputStream
import java.io.InputStream


/**
 * @author Nandita Gandhi
 * @since 12-04-2021
 */
class InvitesUsersAdapter(
    private val clickListener: InvitesUserClickCallback
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG = "InvitesUsersAdapter"
        const val SECTION_VIEW = 0
        const val CONTENT_VIEW = 1
    }

    private val mContactUsersList = mutableListOf<Contact>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SECTION_VIEW -> InvitesUserSectionViewHolder(parent.inflate(R.layout.row_invites_users_section))
            else -> InvitesUserViewHolder(parent.inflate(R.layout.row_invites_users_rv))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InvitesUserViewHolder -> {
                holder.bindUsers(mContactUsersList[position])
                holder.setIsRecyclable(false)
            }
            is InvitesUserSectionViewHolder -> {
                holder.bindUsers(mContactUsersList[position].name)
                holder.setIsRecyclable(false)
            }
        }
    }

    override fun getItemCount(): Int {
        return mContactUsersList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mContactUsersList.get(position).isSection) {
            SECTION_VIEW
        } else {
            CONTENT_VIEW
        }
    }

    fun submitList(listOfUsers: List<Contact>) {
        mContactUsersList.addAll(listOfUsers)
        notifyDataSetChanged()
    }

    inner class InvitesUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindUsers(user: Contact) {
            // Assign values
            itemView.apply {
                val name = user.name
                tvUserFullName.text = name
                user.numbers.forEach {
                    tvUserName.text = it
                    val bitmap = retrieveContactPhoto(context, it, user.id)
                    if (bitmap != null) {
                        civUserImage.setImageBitmap(bitmap)
                    } else {
                        civUserImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_avatar))
                    }
                }
            }

            itemView.btnInvite.setOnClickListener {
                clickListener.performInvitesUserClickAction(user.numbers.get(0))
            }
        }
    }

    inner class InvitesUserSectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindUsers(user: String) {
            itemView.apply {
                tvUserSection.text = user
            }
        }
    }

    interface InvitesUserClickCallback {
        fun performInvitesUserClickAction(invitesUserPhoneNumber: String)
    }


    fun retrieveContactPhoto(context: Context, number: String?, mCotactId: String): Bitmap? {
        val contentResolver = context.contentResolver
        var contactId: String? = null
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )
        val projection =
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID)
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID))
            }
            cursor.close()
        }
        var photo = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.ic_placeholder_broken_img
        )
        try {
            if (contactId != null) {
                val inputStream: InputStream? =
                    ContactsContract.Contacts.openContactPhotoInputStream(
                        context.contentResolver,
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, mCotactId.toLong())
                    )
                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream)
                }
               (inputStream != null)
                inputStream?.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return photo
    }
}
