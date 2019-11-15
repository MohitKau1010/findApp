package com.brewfinder.app.ui.user.fragments.home_fragment.fav_deals


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.Favourite
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.business.fragments.business_home.comment.CommentFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.deal_detail.DealDetailFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_deals.rv_deals
import kotlinx.android.synthetic.main.fragment_deals.swipetorefresh
import kotlinx.android.synthetic.main.fragment_fav_deals.*


class FavDealsFragment : BaseFragment(), FavDealAdapterListLister,
    SwipeRefreshLayout.OnRefreshListener {


    override val mViewModel: BaseViewModel?
        get() = null

    private var mFavDealsViewModel: FavDealViewModel? = null
    private val mAdapter by lazy { FavDealsAdapter(this@FavDealsFragment) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav_deals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        iv_back.setOnClickListener {
            //            (fragmentManager?.fragments!![0] as UserHomeFragment).refreshDealFragment()
            mActivity.onBackPressed()
        }


        // get viewModel
        mFavDealsViewModel = ViewModelProviders.of(mActivity).get(FavDealViewModel::class.java)

        swipetorefresh.setOnRefreshListener(this)

        setRecyclerView()

        mFavDealsViewModel?.mFavDealListResponse?.observe(this, Observer {
            swipetorefresh.isRefreshing = false
            if (it != null) {
                mAdapter.updateList(it as ArrayList<Deal>)
                rv_deals.visibility = View.VISIBLE
                tv_nodata.visibility = View.GONE
            } else {
                rv_deals.visibility = View.GONE
                tv_nodata.visibility = View.VISIBLE
            }
//            showToast("data is null")

        })

        rv_deals.adapter = mAdapter


    }


    override fun onRefresh() {
        setRecyclerView()
        swipetorefresh.isRefreshing = false
    }

    override fun onClickItem(msg: String) {

        showToast(msg)

    }

    override fun onClickHeart(favouriteModel: Favourite) {

        if (favouriteModel.is_fav == "true") {
            Toast.makeText(context, "Favourite Added", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Favourite Removed", Toast.LENGTH_SHORT).show()
        }
        mFavDealsViewModel?.updateDealFavoriteStatus(favouriteModel)
    }

    override fun onClickShare(uri: String) {

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Hey There? Be There..LOL :-)")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share text to.."))
    }

    override fun onClickComment(dealId: String) {

        val bundle = Bundle()
        bundle.putSerializable("deal", dealId)
        val frg = CommentFragment()
        frg.arguments = bundle

        mActivity.doFragmentTransaction(
            containerViewId = R.id.frame_layout,
            fragment = frg,
            isAddToBackStack = true,
            isAddFragment = true
        )

    }


    override fun onDealDetail(dealModel: Deal) {
//        showToast(DealID)

        val bundle = Bundle()
        bundle.putSerializable("deal", dealModel)
        val frg = DealDetailFragment()
        frg.arguments = bundle

        mActivity.doFragmentTransaction(
            containerViewId =
            R.id.frame_layout,
            fragment = frg,
            isAddToBackStack = true,
            isAddFragment = true
        )
    }

    private fun setRecyclerView() {
        //
        swipetorefresh.isRefreshing = true

        mFavDealsViewModel?.getUserDB()
    }


}