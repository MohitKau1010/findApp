package com.brewfinder.app.ui.base_screen.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.MyCustomLoader

/**
 * @author Rashmi
 * 28/9/19
 */
abstract class BaseFragment : androidx.fragment.app.Fragment() {

    private val mMyCustomLoader by lazy { MyCustomLoader(activity) }


//    fun showToast(message: String) {
//        Toast.makeText(BrewFinder.applicationContext(), message, Toast.LENGTH_LONG).show()
//    }

//    fun  showToast(message: String = "", resId: Int = 0) {
//        Toast.makeText(BrewFinder.applicationContext(), if (message.isEmpty()) getString(resId) else message, Toast.LENGTH_LONG).show()
//    }

    lateinit var mActivity: BaseActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as BaseActivity

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel?.isShowLoader?.observe(this, Observer {
            if (it) {
                mMyCustomLoader.showProgressDialog(mActivity)
            } else {
                mMyCustomLoader.dismissProgressDialog()
            }
        })

    }

    abstract val mViewModel: BaseViewModel?
}




