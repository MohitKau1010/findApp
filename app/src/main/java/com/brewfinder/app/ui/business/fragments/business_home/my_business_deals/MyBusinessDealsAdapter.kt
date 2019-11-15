package com.brewfinder.app.ui.business.fragments.business_home.my_business_deals

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_item_deals_near_me.view.menu_row_item_business_deals
import kotlinx.android.synthetic.main.row_item_my_business_deals.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *@author Rashmi
 * 10/15/2019
 */
class MyBusinessDealsAdapter(var context: Context, private val mListener: MyBusinessDealsItemListener) : RecyclerView.Adapter<MyBusinessDealsAdapter.MyViewHolder>() {


    private var mDealList = arrayListOf<Deal>()

    /**
     * to create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rootView = LayoutInflater.from(parent.context).inflate(R.layout.row_item_my_business_deals, parent, false)
        return MyViewHolder(rootView)
    }


    /**
     *Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int {
        return mDealList.size

    }

    /**
     * to update the RecyclerView.ViewHolder contents with the item at the given position and also sets up some private fields to be used by RecyclerView.
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(mDealList[position]) {

            holder.itemView.apply {
                tv_title_my_business_deals.text = deal_companyName

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
                            tv_time_my_business_deal.text = formatMyString("$days", "days ago")
                        } else {
                            tv_time_my_business_deal.text = formatMyString("$days", "days ago")
                        }
                    } else {
                        tv_time_my_business_deal.text = formatMyString("$hours", "hours ago")
                    }
                } else {
                    tv_time_my_business_deal.text = formatMyString("$minutes", "minutes ago")
                }


                /**
                 * getting current date
                 */
                tv_my_business_date_.text = getDateTime(createdAt?.toLong()!!)


                /**
                 * getting deal image
                 */
                Glide.with(context).load(deal_image).placeholder(R.drawable.ic_beer_bg).into(img_my_business_deal)


                /**
                 * getting user icon
                 */
                Glide.with(context).load(deal_company_logo).placeholder(R.drawable.ic_beer_bg).into(profile_image_business_deal)


                /**
                 * setting listeners to open data
                 */
                menu_row_item_business_deals.setOnClickListener {

                    //for dialog view
                    mListener.onMyDealMoreAction(mDealList[position], mDealList[position])
                }


            }
        }
    }

    /**
     *to set proper format for time
     */
    private fun formatMyString(data: String, text: String): String {
        return String.format("%s", "$data $text")
    }

    /**
     * to get date
     */
    @SuppressLint("SimpleDateFormat")
    private fun getDateTime(s: Long): String? {
        return try {
            val sdf = SimpleDateFormat("MM/dd/yyyy")
            val netDate = Date(s)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    /**
     * to get updated data
     */
    fun updateList(list: List<Deal>) {
        mDealList.clear()
        mDealList.addAll(list)
        notifyDataSetChanged() //it will notify adapter ,list has changed
    }


    /**
     * to delete items from list*
     * getting dealId first
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

interface MyBusinessDealsItemListener {
    fun onMyDealMoreAction(myDealId: Deal, myDealDetail: Deal)
    fun onDialogClick(buttonClick: String, dealId: Deal)
}