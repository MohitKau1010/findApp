package com.brewfinder.app.ui.business.fragments.business_home.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brewfinder.app.R
import com.brewfinder.app.data.model.CommentSection
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_item_comment_list.view.*
import java.util.concurrent.TimeUnit
/**
 *@author Rashmi
 * 11/1/2019
 */
class CommentAdapter(var context: Context, var mCommentList: ArrayList<CommentSection>) :
    RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_comment_list, parent, false)
        return MyViewHolder(rootView)
    }
    override fun getItemCount(): Int {
        return mCommentList.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(mCommentList[position]) {
            holder.itemView.apply {
                // val commentModel = CommentSection()
                tv_comment_body_text.text = mCommentList[position].comment
                Glide.with(context).load(commentUserProfile).placeholder(R.drawable.ic_beer_bg).into(profile_image_comment)
                tv_comment_user_name.text = mCommentList[position].commentUserName
                tv_comment_time.text = mCommentList[position].createdAt
                //get current createdAt
                val currentTimestamp = System.currentTimeMillis()
                val differenceTime: Long = currentTimestamp - mCommentList[position].createdAt.toLong()
                val milliseconds: Long = differenceTime
                // long minutes = (milliseconds / 1000) / 60;
                val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                // long seconds = (milliseconds / 1000);
                val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
                val days = TimeUnit.MILLISECONDS.toDays(milliseconds)
                if (minutes >= 60) {
                    if (hours >= 24) {
                        if (days >= 30) {
                            tv_comment_time.text = formatMyString("$days", "days ago")
                        } else {
                            tv_comment_time.text = formatMyString("$days", "days ago")
                        }
                    } else {
                        tv_comment_time.text = formatMyString("$hours", "hours ago")
                    }
                } else {
                    tv_comment_time.text = formatMyString("$minutes", "minutes ago")
                }
            }
        }
    }
    private fun formatMyString(data: String, text: String): String {
        return String.format("%s", "$data $text")
    }
    fun updateList(list: List<CommentSection>) {
        mCommentList.clear()
        mCommentList.addAll(list)
        notifyDataSetChanged()
    }
    fun clearList() {
        mCommentList.clear()
        notifyDataSetChanged()
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}