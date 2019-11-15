package com.brewfinder.app.ui.user.fragments.setting_fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.brewfinder.app.R
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel

/**
 * A simple [Fragment] subclass.
 */
class UserSettingFragment : BaseFragment() {

    override val mViewModel: BaseViewModel?
        get() = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }


}
