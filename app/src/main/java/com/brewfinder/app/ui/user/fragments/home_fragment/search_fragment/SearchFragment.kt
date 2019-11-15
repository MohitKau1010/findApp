package com.brewfinder.app.ui.user.fragments.home_fragment.search_fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.deal_detail.DealDetailFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.Constants
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import kotlinx.android.synthetic.main.search_fragment.*

class SearchFragment : BaseFragment() {
    override val mViewModel: BaseViewModel?
        get() = null

    private lateinit var viewModel: SearchViewModel
    private var mDealList = arrayListOf<Deal>()
    var galaxies = arrayListOf<String>()

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        /**
         *
         */
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserDAta()

        searchBar.isSearchEnabled
        searchBar.isSpeechModeEnabled
        searchBar.enableSearch()

    }

    private fun getUserDAta() {
        galaxies.clear()

        FireBaseRepository().apply {
            getMuserDealDB(object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {

                    mDealList.clear()
                    mDealList.addAll(response as Collection<Deal>)

                    galaxies.clear()
                    for (i in 0 until mDealList.size) {
                        galaxies.add(mDealList[i].deal_title.toString())
                    }

                    initAllWidget()

                }

                override fun onError(errorMessages: String) {
                    Toast.makeText(mActivity, errorMessages, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    private fun initAllWidget() {

        //ADAPTER
        val adapter = ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, galaxies)
        mListView.adapter = adapter

        //SEARCH BAR TEXT CHANGE LISTENER
        searchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //SEARCH FILTER
                adapter.filter.filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })


        //LIST VIEW ITEM CLICKED
        mListView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, i, _ ->

                if (Constants.BUSINESS_TYPE == PrefrenceUtil(mActivity).userProfile?.user_type) {
                    val bundle = Bundle()
                    bundle.putSerializable("deal", mDealList[i])
                    val frg = DealDetailFragment()
                    frg.arguments = bundle

                    mActivity.doFragmentTransaction(
                        containerViewId =
                        R.id.frameLayoutBusiness,
                        fragment = frg,
                        isAddToBackStack = true,
                        isAddFragment = true
                    )
                } else {
                    val bundle = Bundle()
                    bundle.putSerializable("deal", mDealList[i])
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

//                    Toast.makeText(mActivity, adapter.getItem(i)!!.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}