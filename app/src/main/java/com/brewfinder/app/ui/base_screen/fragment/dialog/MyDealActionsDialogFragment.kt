package com.brewfinder.app.ui.base_screen.fragment.dialog

import android.os.Bundle
import android.view.View
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.business.fragments.business_home.my_business_deals.MyBusinessDealsItemListener
import com.brewfinder.app.utils.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_my_business_deals.*

/**
 *@author Rashmi
 * 10/16/2019
 */
class MyDealActionsDialogFragment(
        private val myDealModel: Deal,
        private var mListner: MyBusinessDealsItemListener?
) : BaseDialogFragment(), View.OnClickListener {

    override val isFullScreenDialog: Boolean
        get() = false
    override val layoutId: Int
        get() = R.layout.dialog_my_business_deals

    var mFirebaseRepository = FireBaseRepository()


    override fun init() {

        // set click listener
        btn_delete_deal.setOnClickListener(this)
        btn_edit_deal.setOnClickListener(this)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_delete_deal.setOnClickListener(this)
        btn_edit_deal.setOnClickListener(this)
        text_deal.text = myDealModel.description
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_edit_deal -> {
                mListner?.onDialogClick("Edit", myDealModel)
                dismiss()
            }

            R.id.btn_delete_deal -> {
                mListner?.onDialogClick("Delete", myDealModel)
                dismiss()
            }
        }

    }
}