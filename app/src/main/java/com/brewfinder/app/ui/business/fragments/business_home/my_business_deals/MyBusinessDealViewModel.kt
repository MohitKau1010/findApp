package com.brewfinder.app.ui.business.fragments.business_home.my_business_deals

import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack

/**
 *@author Rashmi
 * 9/28/2019
 */
class MyBusinessDealViewModel : BaseViewModel() {


    var mDealListResponse = MutableLiveData<List<Deal>>()

    //getting user's CreatedDeals
    fun getMyDeals(userId: String, isShowSwipeRefresh: Boolean) {

        isShowSwipeRefreshLoader.value = isShowSwipeRefresh
        FireBaseRepository().apply {
            getMyDealDB(userId, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    // callBack.onSuccess(response)
                    mDealListResponse.postValue(response as List<Deal>)
                    isShowSwipeRefreshLoader.value = false

                }

                override fun onError(errorMessages: String) {
                    //callBack.onError(errorMessages)
                    error.postValue(errorMessages)
                    isShowSwipeRefreshLoader.value = false
                }
            })
        }
    }
}

