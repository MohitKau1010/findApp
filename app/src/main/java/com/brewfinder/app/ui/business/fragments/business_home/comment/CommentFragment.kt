package com.brewfinder.app.ui.business.fragments.business_home.comment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brewfinder.app.data.model.CommentSection
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.business.activity.BusinessHomeActivity
import com.brewfinder.app.ui.user.activity.HomeUserActivity
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.Constants
import com.brewfinder.app.utils.showToast
import kotlinx.android.synthetic.main.fragment_business_chat.*


/**
 *@author Rashmi
 * 11/1/2019
 */
class CommentFragment : BaseFragment(), View.OnClickListener {
    var dealId: String = " "
    var mCommentSection = CommentSection()
    private var mCommentViewModel: CommentViewModel? = null

//    private val mCommentAdapter: CommentAdapter by lazy { CommentAdapter(mActivity) }

    override val mViewModel: BaseViewModel?
        get() = mCommentViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(com.brewfinder.app.R.layout.fragment_business_chat, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dealId = arguments?.getSerializable("deal") as String

        rv_Business_Chat.layoutManager = LinearLayoutManager(mActivity)
        rv_Business_Chat.adapter = CommentAdapter(mActivity, ArrayList())
        btn_send.setOnClickListener(this)

        if (PrefrenceUtil(activity!!).userProfile?.user_type == Constants.USER_TYPE)
            (activity as HomeUserActivity).toggleBottomNavigation(false)
        else
            (activity as BusinessHomeActivity).toggleBottomNavigation(false)

        // get viewModel
        mCommentViewModel = ViewModelProviders.of(this).get(CommentViewModel::class.java)

        mCommentViewModel?.getDealsComments(dealId)
        observeData()

    }

    /**
     * destroying BottomNavigationView when removed from related fragment it could be USER or BusinessOwner
     */
    override fun onDestroyView() {
        super.onDestroyView()
        if (PrefrenceUtil(activity!!).userProfile?.user_type == Constants.USER_TYPE)
            (activity as HomeUserActivity).toggleBottomNavigation(true)
        else
            (activity as BusinessHomeActivity).toggleBottomNavigation(true)
//        mCommentViewModel?.mCommentResponse?.postValue(ArrayList())


    }

    //observing data
    private fun observeData() {

        /// observe if comment is posted
        mCommentViewModel?.commentPosted?.observe(this, Observer {
            if (it) {
                // mCommentViewModel?.getDealsComments(dealId)
//                (rv_Business_Chat.adapter as CommentAdapter).clearList()


            }
        })
        mCommentViewModel?.mCommentResponse?.observe(this, Observer {
            if (it != null)
                (rv_Business_Chat.adapter as CommentAdapter).updateList(it)
            runLayoutAnimation(rv_Business_Chat)
        })
        mCommentViewModel?.error?.observe(this, Observer {
            if (it.equals("list is empty", true)) {
                (rv_Business_Chat.adapter as CommentAdapter).updateList(ArrayList())
            }
        })

    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, com.brewfinder.app.R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    //setting clickListener on a button
    override fun onClick(v: View?) {
        (activity as BaseActivity).hideKeyboard(activity!!)
        when (v?.id) {
            com.brewfinder.app.R.id.btn_send -> {
                mCommentSection.comment = editTextComment.text.toString().trim()
                if (mCommentSection.comment.isEmpty()) {
                    showToast("Nothing to post")
                } else {
                    PrefrenceUtil(mActivity).userProfile?.apply {
                        mCommentSection.commentUserId = PrefrenceUtil(mActivity).userId!!
                        mCommentSection.commentUserName = if (user_type == Constants.USER_TYPE) this.user_name else this.company_name
                        mCommentSection.commentUserProfile = if (user_type == Constants.USER_TYPE) this.user_pic else this.busines_logo
                        mCommentSection.createdAt = System.currentTimeMillis().toString()
                        mCommentViewModel?.updateDealCommentList(dealId, mCommentSection)
                    }
                    editTextComment.setText("")
                }
            }
        }
    }
}