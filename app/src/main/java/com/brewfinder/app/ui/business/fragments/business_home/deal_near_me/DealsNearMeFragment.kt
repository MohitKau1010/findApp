package com.brewfinder.app.ui.business.fragments.business_home.deal_near_me

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.Favourite
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.business.fragments.business_home.comment.CommentFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.deal_detail.DealDetailFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import com.brewfinder.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_deals_near_me.*


/**
 *@author Rashmi
 * 10/8/2019
 */

class DealsNearMeFragment : BaseFragment(), DealNearMeItemListener, SwipeRefreshLayout.OnRefreshListener {

    var mFireBaseRepository = FireBaseRepository()
    private var mDealsNearMeViewModel: DealNearMeViewModel? = null
    private val mAdapter: DealsNearMeAdapter by lazy { DealsNearMeAdapter(mActivity, this) }
    override val mViewModel: BaseViewModel?
        get() = null

    /**
     * to get UI from your fragment
     * returning view to get UI
     */

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_deals_near_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_deals_near_me.layoutManager = LinearLayoutManager(mActivity)
        rv_deals_near_me.adapter = mAdapter

        swipeRefreshDealsNearMe.setOnRefreshListener(this)

        // get viewModel
        mDealsNearMeViewModel = ViewModelProviders.of(mActivity).get(DealNearMeViewModel::class.java)


        observeData()

        // get deal data
        mDealsNearMeViewModel?.getUserDeals(true)

    }


    /**
     * observing deal data
     */
    private fun observeData() {


        mDealsNearMeViewModel?.mDealListResponse?.observe(this, Observer {
            if (it != null)
                mAdapter.updateList(it)
        })
        mDealsNearMeViewModel?.error?.observe(this, Observer {
            if (it.equals("list is empty", true)) {
                mAdapter.updateList(ArrayList())
            }
        })

        mDealsNearMeViewModel?.isShowSwipeRefreshLoader?.observe(this, Observer {
            if (it != null && it) showSwipeRefreshLoader() else hideSwipeRefreshLoader()
        })


    }

    /**
     * to refresh the screen
     */
    override fun onRefresh() {
        mDealsNearMeViewModel?.getUserDeals(false)
    }

    /**
     * updating data of favourite in viewModel
     * @param favouriteModel is giving the Favourite's Model
     */
    override fun onClickHeart(favouriteModel: Favourite) {

        if (favouriteModel.is_fav == "true") {

            Toast.makeText(context, "Favourite Added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Favourite Removed", Toast.LENGTH_SHORT).show()
        }

        mDealsNearMeViewModel?.updateDealFavoriteStatus(favouriteModel)
    }

    /**
     * Callback method of DialogView used at MyBusinessDeals
     * @param buttonClick getting Delete and Edit flags so that button can perform accordingly
     * @param dealId performs action according to dealID
     */
    override fun onDialogClick(buttonClick: String, dealId: String) {
        if (buttonClick == "Delete") {
            mFireBaseRepository.onDeleteDeals(dealId,
                    object : FirebaseNetworkCallBack {
                        override fun onSuccess(response: Any) {
                            mAdapter.deleteListItem(dealId)
                            showToast(response as String)
                        }

                        override fun onError(errorMessages: String) {
                            showToast(errorMessages)
                        }

                    })
        }
    }

    /**
     * clicking on comment icon ,opening a new fragment
     */
    override fun onClickComment(dealId: String) {
        val bundle = Bundle()
        bundle.putSerializable("deal", dealId)
        val frg = CommentFragment()
        frg.arguments = bundle

        mActivity.doFragmentTransaction(
                containerViewId =
                R.id.frameLayoutBusiness,
                fragment = frg,
                isAddToBackStack = true,
                isAddFragment = true
        )

    }


    /**
     * opening intent by clicking share icon
     */
    override fun onClickShare(uri: String) {

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Hey There? Be There..LOL :-)")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share text to.."))
    }


    /**
     *to get dealDetail
     */
    override fun onClickDealDetail(dealModel: Deal) {
        val bundle = Bundle()
        bundle.putSerializable("deal", dealModel)
        val frg = DealDetailFragment()
        frg.arguments = bundle

        mActivity.doFragmentTransaction(
                containerViewId =
                R.id.frameLayoutBusiness,
                fragment = frg,
                isAddToBackStack = true,
                isAddFragment = true
        )
    }


    /**
     * refreshing data in View Model
     */
    private fun showSwipeRefreshLoader() {
        swipeRefreshDealsNearMe?.post {
            if (null != swipeRefreshDealsNearMe) {
                swipeRefreshDealsNearMe!!.isRefreshing = true
            }
        }
    }

    private fun hideSwipeRefreshLoader() {
        Handler().postDelayed({
            if (null != swipeRefreshDealsNearMe && swipeRefreshDealsNearMe!!.isRefreshing) {
                swipeRefreshDealsNearMe!!.isRefreshing = false
            }
        }, 50)
    }


    fun refreshListData() {
        mDealsNearMeViewModel?.getUserDeals(false)
    }

}

