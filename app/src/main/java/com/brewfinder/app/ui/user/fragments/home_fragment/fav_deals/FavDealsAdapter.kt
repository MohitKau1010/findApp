package com.brewfinder.app.ui.user.fragments.home_fragment.fav_deals

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
import com.brewfinder.app.ui.business.fragments.business_home.BusinessHomeFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.concurrent.TimeUnit

/**
 *@author Mohit
 * 10/11/2019
 */
class FavDealsAdapter(listner: FavDealAdapterListLister) :
    RecyclerView.Adapter<FavDealsAdapter.ViewHolder>() {

    private var filelist = arrayListOf<Deal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_fav_deals, parent, false)

        return ViewHolder(rootView)
    }

    var mListner: FavDealAdapterListLister = listner


    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return filelist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(filelist[position], mListner, position)
    }

    fun updateList(list: ArrayList<Deal>) {

        filelist.clear()

        //filter data
        for (i in 0 until list.size - 1) {
            if (list[i].is_fav.equals("true")) {
                filelist.add(list[i])
                notifyDataSetChanged()
            }
        }

    }

    //the class is hodling the list view
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItems(deal: Deal, mLister: FavDealAdapterListLister, position: Int) {


            if (filelist[position].is_fav.equals("true")) {
                deal.is_fav = filelist[position].is_fav

                //get current time
                val currentTimestamp = System.currentTimeMillis()
                val diffrencetime: Long = currentTimestamp - deal.createdAt!!.toLong()
                val milliseconds: Long = diffrencetime
                val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
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
                    itemView.findViewById(R.id.heart_row_item_fav_deals) as ImageView
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

                shareRowItemMyDeal.setOnClickListener {
                    mListner.onClickShare(filelist[position].deal_id.toString())
                }
                commentRowItemBusinessDeals.setOnClickListener {
                    mListner.onClickComment(filelist[position].deal_id.toString())
                }
                tvDealDetail.setOnClickListener {
                    mLister.onDealDetail(deal)
                    //Toast.makeText(MyApplication.applicationContext(),""+it,Toast.LENGTH_LONG).show()
                }
                tvFollowRowItemBusinessDeal.setOnClickListener {
                    mLister.onClickItem("Follow option not added yet")
                    //Toast.makeText(MyApplication.applicationContext(),""+it,Toast.LENGTH_LONG).show()
                }



                if (deal.is_fav.equals("true")) {
                    heartRowItemBusinessDeals.setImageResource(R.drawable.ic_red_heart)
                } else {
                    heartRowItemBusinessDeals.setImageResource(R.drawable.ic_heart)
                }

                heartRowItemBusinessDeals.setOnClickListener {
                    // check if it is true or not(data observed in fragment)
                    if (deal.is_fav.equals("true")) {
                        notifyFavouriteDeal(
                            mLister,
                            position,
                            filelist[position].deal_id.toString(),
                            "false"
                        )
                        heartRowItemBusinessDeals.setImageResource(R.drawable.ic_heart)
                    } else {
                        notifyFavouriteDeal(
                            mLister,
                            position,
                            filelist[position].deal_id.toString(),
                            "true"
                        )
                        heartRowItemBusinessDeals.setImageResource(R.drawable.ic_red_heart)
                    }


                }
            }
        }

        private fun notifyFavouriteDeal(
            mLister: FavDealAdapterListLister,
            position: Int,
            dealId: String,
            isFav: String
        ) {
            //model updated in firebase repository
            val favModel = Favourite()
            favModel.deal_id = dealId
            favModel.is_fav = isFav
            mLister.onClickHeart(favModel)

            deleteListItem(dealId)
//            (fragmentManager?.fragments!![0] as BusinessHomeFragment).refreshDealsNearMe()

//            filelist[position].is_fav = isFav
//            notifyDataSetChanged()
        }


    }

    fun deleteListItem(postID: String) {

        val deal = filelist.find { it.deal_id == postID }
        val positiion = filelist.indexOf(deal)

        filelist.removeAt(positiion)
        notifyItemRemoved(positiion)


    }

}

interface FavDealAdapterListLister {
    fun onClickItem(msg: String)
    fun onDealDetail(dealModel: Deal)
    fun onClickComment(dealId: String)
    fun onClickHeart(favouriteModel: Favourite)
    fun onClickShare(uri: String)
}

