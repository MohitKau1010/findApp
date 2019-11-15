package com.brewfinder.app.ui.user.fragments.home_fragment.deals

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brewfinder.app.BrewFinder
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.Favourite
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.concurrent.TimeUnit

/**
 *@author Mohit
 * 10/11/2019
 */
class DealsAdapter(listner: DealAdapterListLister) : RecyclerView.Adapter<DealsAdapter.ViewHolder>() {

    private var filelist = arrayListOf<Deal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item_my_user_deals, parent, false)


        return ViewHolder(rootView)
    }

    var mListner: DealAdapterListLister = listner


    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return filelist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(filelist[position], mListner, position)
    }

    fun updateList(list: ArrayList<Deal>) {

        filelist.clear()
        filelist.addAll(list)
        notifyDataSetChanged()
    }

    //the class is hodling the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItems(deal: Deal, mLister: DealAdapterListLister, position: Int) {

            deal.is_fav = filelist[position].is_fav

            //get current time
            val currentTimestamp = System.currentTimeMillis()
            val mdiffrencetime: Long = currentTimestamp - deal.createdAt!!.toLong()
            val milliseconds: Long = mdiffrencetime
            // long minutes = (milliseconds / 1000) / 60;
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
            // long seconds = (milliseconds / 1000);
            val houres = TimeUnit.MILLISECONDS.toHours(milliseconds)

            val days = TimeUnit.MILLISECONDS.toDays(milliseconds)


            val tvDealUserName = itemView.findViewById(R.id.tv_deal_user_name) as TextView
            val tvDealDetail = itemView.findViewById(R.id.tv_deal_details_user) as TextView
            val tvDealTime = itemView.findViewById(R.id.tv_deal_time) as TextView
            val shareRowItemMyDeal =
                itemView.findViewById(R.id.reply_row_item_my_user_deals) as ImageView

            val tvFollowRowItemBusinessDeal =
                itemView.findViewById(R.id.follow_row_item_my_user_deal) as TextView
            val heartRowItemBusinessDeals =
                itemView.findViewById(R.id.heart_row_item_my_user_deals) as ImageView
            val commentRowItemBusinessDeals =
                itemView.findViewById(R.id.comment_row_item_business_deals) as ImageView
            val profileImageDeal = itemView.findViewById(R.id.profile_image_deal) as ImageView
            val ivDealImage = itemView.findViewById(R.id.iv_deal_image) as ImageView

            tvDealUserName.text = deal.deal_companyName
            //tv_deal_time.text= "$minutes min ago"

            if (minutes >= 60) {
                if (houres >= 24) {
                    if (days >= 30) {
                        tvDealTime.text = "$days days ago"
                    } else {
                        tvDealTime.text = "$days days ago"
                    }
                } else {
                    tvDealTime.text = "$houres hour ago"
                }
            } else {
                tvDealTime.text = "$minutes min ago"
            }

            Glide.with(BrewFinder.applicationContext())
                    .load(deal.deal_image)
                    .placeholder(R.drawable.ic_beer_bg)
//                .apply(RequestOptions.circleCropTransform())
                .into(ivDealImage)

            Glide.with(BrewFinder.applicationContext())
                    .load(deal.deal_company_logo)
                    .placeholder(R.drawable.profile_icon)
                    .apply(RequestOptions.circleCropTransform())
                .into(profileImageDeal)
//

            shareRowItemMyDeal.setOnClickListener {
                mListner.onClickShare(filelist[position].deal_id.toString())
            }
            commentRowItemBusinessDeals.setOnClickListener {
                mListner.onClickComment(filelist[position].deal_id.toString())
            }


            tvDealDetail.setOnClickListener {
                mLister.onDealDetail(deal)
            }
            tvFollowRowItemBusinessDeal.setOnClickListener {
                mLister.onClickItem("Follow option not added yet")
            }

            heartRowItemBusinessDeals.setOnClickListener {

                // check if it is true or not(data observed in fragment)
                if (deal.is_fav.equals("true"))
                    notifyFavouriteDeal(position, "false")
                else
                    notifyFavouriteDeal(position, "true")


                //model updated in firebase repository
                val fav_model = Favourite()
                fav_model.deal_id = filelist[position].deal_id.toString()
                fav_model.is_fav = filelist[position].is_fav
                mLister.onClickHeart(fav_model)

            }

            //
            if (deal.is_fav.equals("true")) {
                heartRowItemBusinessDeals.setImageResource(R.drawable.ic_red_heart)
            } else {
                heartRowItemBusinessDeals.setImageResource(R.drawable.ic_heart)
            }


        }

        private fun notifyFavouriteDeal(position: Int, isFav: String) {
            filelist[position].is_fav = isFav
            notifyDataSetChanged()
        }


    }


}

interface DealAdapterListLister {
    fun onClickItem(msg: String)
    fun onDealDetail(dealModel: Deal)
    fun onClickComment(dealId: String)
    fun onClickHeart(favouriteModel: Favourite)
    fun onClickShare(uri: String)
}

