package com.brewfinder.app.ui.user.fragments.home_fragment.deals


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
import kotlinx.android.synthetic.main.fragment_deals.*


class DealsFragment : BaseFragment(),
    DealAdapterListLister, SwipeRefreshLayout.OnRefreshListener {

    private var mDealsViewModel: DealViewModel? = null
    override val mViewModel: BaseViewModel?
        get() = null
    private val mAdapter by lazy {
        DealsAdapter(
            this@DealsFragment
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // setRecyclerView()

        return inflater.inflate(R.layout.fragment_deals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // get viewModel
        mDealsViewModel = ViewModelProviders.of(mActivity).get(DealViewModel::class.java)

        swipetorefresh.setOnRefreshListener(this)

        setRecyclerView()

        mDealsViewModel?.mDealListResponse?.observe(this, Observer {
            swipetorefresh.isRefreshing = false
            if (it != null)
                mAdapter.updateList(it as ArrayList<Deal>)
            else
                showToast("data is null")
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
        mDealsViewModel?.updateDealFavoriteStatus(favouriteModel)
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

    fun setRecyclerView() {
        //
        swipetorefresh.isRefreshing = true

        mDealsViewModel?.getuserDB()
    }

    fun refreshListData() {
        mDealsViewModel?.getuserDB()
    }


}