package com.brewfinder.app.ui.business.fragments.business_home.deal_near_me

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.Favourite
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_item_deals_near_me.view.*
import java.util.concurrent.TimeUnit

/**
 *@author Rashmi
 * 10/11/2019
 */
class DealsNearMeAdapter(var context: Context, var mListener: DealNearMeItemListener) : RecyclerView.Adapter<DealsNearMeAdapter.MyViewHolder>() {

    private var mDealList = arrayListOf<Deal>()

    /**
     *associating view with the adapter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.row_item_deals_near_me, parent, false)
        return MyViewHolder(rootView)
    }


    /**
     *Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return mDealList.size

    }

    /**
     *responsible for binding view acc to the items in the list also used to assign data to each ViewItem
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        with(mDealList[position]) {

            holder.itemView.apply {
                //declaring variable
                is_fav = mDealList[position].is_fav

                tv_title_business_deals_near_me.text = deal_companyName

                //get current createdAt
                val currentTimestamp = System.currentTimeMillis()
                val differenceTime: Long = currentTimestamp - mDealList[position].createdAt!!.toLong()
                val milliseconds: Long = differenceTime

                // long minutes = (milliseconds / 1000) / 60;
                val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                // long seconds = (milliseconds / 1000);
                val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)

                val days = TimeUnit.MILLISECONDS.toDays(milliseconds)

                if (minutes >= 60) {
                    if (hours >= 24) {
                        if (days >= 30) {
                            tv_time_deal_near_me.text = formatMyString("$days", "days ago")
                        } else {
                            tv_time_deal_near_me.text = formatMyString("$days", "days ago")
                        }
                    } else {
                        tv_time_deal_near_me.text = formatMyString("$hours", "hours ago")
                    }
                } else {
                    tv_time_deal_near_me.text = formatMyString("$minutes", "minutes ago")
                }




                Glide.with(context)
                        .load(deal_image)
                        .placeholder(R.drawable.ic_beer_bg)
                        .into(img_row_item_deal_near_me)

                Glide.with(context).load(deal_company_logo).placeholder(R.drawable.ic_beer_bg).into(profile_image_deal_near_me)


                // setting listeners on some items
                tv_deal_details_deal_near_me.setOnClickListener {

                    mListener.onClickDealDetail(mDealList[position])

                }

                //share
                share_row_item_business_deals_near_me.setOnClickListener {

                    mListener.onClickShare(mDealList[position].deal_id.toString())
                }
                //chat
                comment_row_item_business_deals_near_me.setOnClickListener {


                    mListener.onClickComment(mDealList[position].deal_id.toString())
                }

                //follow
                follow_row_item_my_business_deal_near_me.setOnClickListener {

                }

                //heart
                heart_row_item_deals_near_me.setOnClickListener {


                    /**
                     * check if it is true or not(data observed in fragment)
                     */
                    if (is_fav.equals("true"))
                        notifyFavouriteDeal(position, "false")
                    else
                        notifyFavouriteDeal(position, "true")

                    /**
                     * model updated in firebase repository
                     */
                    val fav_model = Favourite()
                    fav_model.deal_id = mDealList[position].deal_id.toString()
                    fav_model.is_fav = mDealList[position].is_fav
                    mListener.onClickHeart(fav_model)
                }

                /**
                 *  ui updating acc to is fav
                 */

                if (is_fav.equals("true")) {
                    heart_row_item_deals_near_me.setImageResource(R.drawable.ic_red_heart)
                } else {
                    heart_row_item_deals_near_me.setImageResource(R.drawable.ic_heart)

                }
            }
        }
    }

    /**
     * to get updated data
     */
    fun updateList(list: List<Deal>) {
        mDealList.clear()
        mDealList.addAll(list)
        notifyDataSetChanged()
    }


    /**
     * formatting time
     * @param data getting data weather it is seconds,minutes,hours,days
     * @param text getting messages
     * @return  it will return data in  String format
     */
    private fun formatMyString(data: String, text: String): String {
        return String.format("%s", "$data $text")
    }

    /**
     * updating values acc to particular id's weather it is fav or not e.g-comment_deal_id
     * @param position will get the position of favourite deal
     * @param isFav will set that particular deal position as fav
     */

    private fun notifyFavouriteDeal(position: Int, isFav: String) {
        mDealList[position].is_fav = isFav
        notifyDataSetChanged()
    }

    /**
     * to delete items from list*
     * @param dealID getting dealId first
     * getting position of deal
     * removing it from particular index
     * notifying adapter list has changed
     */
    fun deleteListItem(dealID: String) {

        val deal = mDealList.find { it.deal_id == dealID }
        val position = mDealList.indexOf(deal)
        mDealList.removeAt(position)
        notifyItemRemoved(position)


    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

interface DealNearMeItemListener {

    fun onClickHeart(favouriteModel: Favourite)
    fun onClickShare(uri: String)
    fun onClickComment(dealId: String)
    fun onClickDealDetail(dealModel: Deal)
    fun onDialogClick(buttonClick: String, dealId: String)
}

