package com.brewfinder.app.ui.business.fragments.business_home.create_deals

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.brewfinder.app.BrewFinder
import com.brewfinder.app.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_item_add_image.view.*
import kotlinx.android.synthetic.main.row_item_createdealadapter.view.iv_imagecreatedeals
import kotlinx.android.synthetic.main.row_item_editdealadapter.view.*


/**
 * @author Shubham
 * 16/10/19
 */
class CreateDealAdapter(private val mContext: Context, private val mAddImageListener: AddImageListener, private var message: String) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ROW_TYPE_ADD_IMAGE = 1
    val ROW_TYPE_IMAGE = 2
    val ROW_TYPE_EDIT = 3

    var filelist = mutableListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (message == "editdeal") {
            viewType == 3
        }

        return when (viewType) {
            ROW_TYPE_ADD_IMAGE -> {
                AddImageHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.row_item_add_image, parent, false)
                )
            }
            ROW_TYPE_IMAGE -> {
                ImageViewHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.row_item_createdealadapter, parent, false)
                )
            }
            ROW_TYPE_EDIT -> {
                ImageViewHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.row_item_editdealadapter, parent, false)
                )
            }
            else -> {
                ImageViewHolder(
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.row_item_createdealadapter, parent, false)
                )
            }
        }

    }

    override fun getItemCount(): Int {
        return filelist.size + 1
    }

    fun updateList(uri: Uri) {
        filelist.add(uri)
        notifyItemInserted(filelist.size)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == filelist.size) {
            ROW_TYPE_ADD_IMAGE
        } else {
            if (message == "editdeal") {
                ROW_TYPE_EDIT
            } else {
                ROW_TYPE_IMAGE
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder)
            holder.bindItems(filelist[position], mContext)
        else
            (holder as AddImageHolder).bindItems()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(uri: Uri, context: Context) {

            Glide
                    .with(context)
                    .load(uri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_beer_bg)
                    .into(itemView.iv_imagecreatedeals)


            if (message == "editdeal") {
                itemView.iv_cross.setOnClickListener {
                    filelist.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)

                    Toast.makeText(BrewFinder.applicationContext(), "Delete Image", Toast.LENGTH_LONG).show()
                }
            }

        }

    }

    inner class AddImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems() {
            itemView.ivImagePicker.setOnClickListener { mAddImageListener.onAddImage() }
        }

    }

    interface AddImageListener {
        fun onAddImage()
    }
}
