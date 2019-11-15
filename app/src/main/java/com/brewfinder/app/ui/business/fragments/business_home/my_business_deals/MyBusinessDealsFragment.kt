package com.brewfinder.app.ui.business.fragments.business_home.my_business_deals

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.base_screen.fragment.dialog.MyDealActionsDialogFragment
import com.brewfinder.app.ui.business.fragments.business_home.BusinessHomeFragment
import com.brewfinder.app.ui.business.fragments.business_home.create_deals.CreateDealFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import kotlinx.android.synthetic.main.fragment_my_business_deals.*

/**
 *@author Rashmi
 * 10/8/2019
 */
class MyBusinessDealsFragment : BaseFragment(), MyBusinessDealsItemListener,
    SwipeRefreshLayout.OnRefreshListener {

    var mFireBaseRepository = FireBaseRepository()
    private val mAdapter: MyBusinessDealsAdapter by lazy { MyBusinessDealsAdapter(mActivity, this) }
    private val mMyBusinessDealViewModel by lazy { MyBusinessDealViewModel() }

    override val mViewModel: BaseViewModel?
        get() = mMyBusinessDealViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_business_deals, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setting adapter
        rv_my_business_deals.layoutManager = LinearLayoutManager(mActivity)
        rv_my_business_deals.adapter = mAdapter


        swipeRefreshMyBusinessDeals.setOnRefreshListener(this)

        observeData()

        // get deal data
        mMyBusinessDealViewModel.getMyDeals(PrefrenceUtil(mActivity).userId.toString(), true)
    }

    /**
     * observing deal data
     */
    private fun observeData() {

        mMyBusinessDealViewModel.mDealListResponse.observe(this, Observer {
            if (it != null)
                mAdapter.updateList(it)
        })

        mMyBusinessDealViewModel.error.observe(this, Observer {
            if (it.equals("list is empty", true)) {
                mAdapter.updateList(ArrayList())
            }
        })

        mMyBusinessDealViewModel.isShowSwipeRefreshLoader.observe(this, Observer {
            if (it != null && it) showSwipeRefreshLoader() else hideSwipeRefreshLoader()
        })

    }

    /**
     * to refresh the screen
     */
    override fun onRefresh() {
        mMyBusinessDealViewModel.getMyDeals(PrefrenceUtil(mActivity).userId.toString(), false)
    }

    /**
     *to click on dialog buttons Edit an Delete
     */
    override fun onDialogClick(buttonClick: String, dealData: Deal) {
        if (buttonClick == "Delete") {
            mFireBaseRepository.onDeleteDeals(
                dealData.deal_id!!,
                object : FirebaseNetworkCallBack {
                    override fun onSuccess(response: Any) {

                        mAdapter.deleteListItem(dealData.deal_id!!)
                        (fragmentManager?.fragments!![1] as BusinessHomeFragment).refreshDealsNearMe()
                        Toast.makeText(context, response as String, Toast.LENGTH_SHORT).show()

                    }

                    override fun onError(errorMessages: String) {
                        Toast.makeText(context, errorMessages, Toast.LENGTH_SHORT).show()
                    }
                }
            )

        } else if (buttonClick == "Edit") {

            val bundle = Bundle()
            bundle.putSerializable("editdeal", dealData)
            val frg = CreateDealFragment()
            frg.arguments = bundle
            //first we open create deal in this click
            mActivity.doFragmentTransaction(
                    enterAnimation = R.animator.slide_up,
                    popExitAnimation = R.animator.slide_down,
                    containerViewId = R.id.frameLayoutBusiness,
                    fragment = frg
            )

        }

    }

    /**
     *to open the dialogView in myDeals
     */

    override fun onMyDealMoreAction(myDealId: Deal, myDealDetail: Deal) {
        val dialog = MyDealActionsDialogFragment(myDealDetail, this)
        dialog.show((mActivity).supportFragmentManager, "Dialog")


    }


    /**
     * refreshing data in View Model
     */
    private fun showSwipeRefreshLoader() {
        swipeRefreshMyBusinessDeals?.post {
            if (null != swipeRefreshMyBusinessDeals) {
                swipeRefreshMyBusinessDeals!!.isRefreshing = true
            }
        }
    }

    private fun hideSwipeRefreshLoader() {
        Handler().postDelayed({
            if (null != swipeRefreshMyBusinessDeals && swipeRefreshMyBusinessDeals!!.isRefreshing) {
                swipeRefreshMyBusinessDeals!!.isRefreshing = false
            }
        }, 50)
    }


}