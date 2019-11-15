package com.brewfinder.app.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *@author Rashmi
 * 10/1/2019
 */
open class BaseViewModel : ViewModel() {

    val error = MutableLiveData<String>()
    val isShowLoader = MutableLiveData<Boolean>()
    val isShowSwipeRefreshLoader = MutableLiveData<Boolean>()


}