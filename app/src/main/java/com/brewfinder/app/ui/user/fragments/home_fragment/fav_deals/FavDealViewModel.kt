package com.brewfinder.app.ui.user.fragments.home_fragment.fav_deals

import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.Favourite
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack

class FavDealViewModel : BaseViewModel() {


    var mFirebaseRepository = FireBaseRepository()
    val arrayList = arrayListOf<Deal>()
    val favarrayList = arrayListOf<Deal>()

    var mFavDealListResponse = MutableLiveData<List<Deal>>()


    fun getUserDB() {
        mFirebaseRepository.getUserDealDB(object : FirebaseNetworkCallBack {
            override fun onSuccess(response: Any) {
                arrayList.clear()
                arrayList.addAll(response as Collection<Deal>)

//                arrayList=response
                if (arrayList.size == 0) {
                    mFavDealListResponse.value = null
                } else {
                    //set recycler view
//                    mAdapter = DealsAdapter(arrayList, this@DealsFragment)
                    mFavDealListResponse.value = arrayList

//                    for (i in 0..arrayList.size){
//                        if(arrayList[i].is_fav=="true"){
//                            favarrayList.add(arrayList[i])
//                            mFavDealListResponse.value = favarrayList
//                        }
//                    }
//                    mAdapter.updateList(arrayList)
//                    arrayList.clear()

                }
            }

            override fun onError(errorMessages: String) {
                error.value = errorMessages
                error.value = errorMessages
            }

        })
    }

    //  calling fun of firebase repository class to update favourite status in DB
    fun updateDealFavoriteStatus(DealId: Favourite) {

        FireBaseRepository().apply {
            updateFavouriteDeal(DealId, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                }

                override fun onError(errorMessages: String) {

                }

            })

        }
    }

}