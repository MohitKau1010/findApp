package com.brewfinder.app.ui.business.fragments.business_home.deal_near_me

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.Favourite
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack


/**
 *@author Rashmi
 * 10/17/2019
 */
class DealNearMeViewModel : BaseViewModel() {

    var mDealListResponse = MutableLiveData<List<Deal>>()


    /**
     * calling function of fireBase repository class "getUserDealDB" to get the user's status in DB
     * @param isShowSwipeRefresh to refresh the screen
     * @return Return data with listener to calling view based on result
     */
    fun getUserDeals(isShowSwipeRefresh: Boolean) {
        isShowSwipeRefreshLoader.value = isShowSwipeRefresh
        FireBaseRepository().apply {
            getUserDealDB(object : FirebaseNetworkCallBack {
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

    /**
     *  calling function of fireBase repository class "UpdateFavouriteDeal" to update favourite status in DB
     *  @param DealId is giving the favourite's DealId whose status needs to be updated
     *  @return Return data with listener to calling view based on result
     */
    fun updateDealFavoriteStatus(DealId: Favourite) {
        FireBaseRepository().apply {
            updateFavouriteDeal(DealId, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    Log.d("Added", "")

                }

                override fun onError(errorMessages: String) {
                    Log.d("Added", "")
                }

            })

        }
    }


}