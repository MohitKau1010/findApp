package com.brewfinder.app.ui.user.fragments.home_fragment.deal_detail


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brewfinder.app.BrewFinder
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.MyCustomLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_deal_detail.*
import java.util.concurrent.TimeUnit


class DealDetailFragment : BaseFragment() {
    override val mViewModel: BaseViewModel?
        get() = null


    private var dealModel: Deal? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deal_detail, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set toolbar ui
        title_center_text_home_tool.text = "Deal Detail"
        left_image_home_tool.setBackgroundResource(R.drawable.ic_arrow_back)

        left_image_home_tool.setOnClickListener {
            mActivity.onBackPressed()
        }

        arguments?.let {
            dealModel = it.getSerializable("deal") as Deal
            dealModel.apply {

                Glide.with(activity!!)
                        .load(this!!.deal_image)
                        .into(img_deal_detail)

                Glide.with(BrewFinder.applicationContext())
                        .load(deal_company_logo)
                        .placeholder(R.drawable.profile_icon)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profile_deal_detail)

                tv_title_deal_detail.text = deal_companyName


                //get current createdAt
                val currentTimestamp = System.currentTimeMillis()
                val diffrencetime: Long = currentTimestamp - dealModel!!.createdAt!!.toLong()
                val milliseconds: Long = diffrencetime
                val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                val houres = TimeUnit.MILLISECONDS.toHours(milliseconds)
                val days = TimeUnit.MILLISECONDS.toDays(milliseconds)
                if (minutes >= 60) {
                    if (houres >= 24) {
                        if (days >= 30) {
                            tv_time_deal_detail.text = "$days days ago"
                        } else {
                            tv_time_deal_detail.text = "$days days ago"
                        }
                    } else {
                        tv_time_deal_detail.text = "$houres hour ago"
                    }
                } else {
                    tv_time_deal_detail.text = "$minutes min ago"
                }

                tv_deal_decription.text = "Deal Detail: $description"

                btn_redeemcode.setOnClickListener { v: View? ->

                    MyCustomLoader(mActivity).showAlertDialog("Alert!!",
                        "REDEEM CODE IS : $redeem_code",
                        "OK",
                        "CANCEL",
                        DialogInterface.OnClickListener { dialog, p1 ->
                            dialog?.dismiss()
//                    finish()
//                    super.onBackPressed()
                            // mMyCustomLoader.dismissProgressDialog()
                        },
                        DialogInterface.OnClickListener { dialog, p1 ->
                            dialog?.dismiss()
                            //mMyCustomLoader.dismissProgressDialog()
                        })
                }


            }

        }



    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
                DealDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("deal", param1)
                    }
                }
    }

}
