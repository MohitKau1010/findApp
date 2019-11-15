package com.brewfinder.app.ui.business.fragments.business_home.comment

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.data.model.CommentSection
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack

/**
 *@author Rashmi
 * 11/9/2019
 */
class CommentViewModel : BaseViewModel() {

    var mCommentResponse = MutableLiveData<List<CommentSection>>()
    var commentPosted = MutableLiveData<Boolean>()


    @SuppressLint("LogNotTimber")
    fun getDealsComments(dealID: String) {
        isShowLoader.value = true
        Log.d("dealIdCommentViewModel", dealID)
        FireBaseRepository().apply {
            getUserComments(dealID, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    // callBack.onSuccess(response)
                    mCommentResponse.postValue(response as List<CommentSection>)
                    isShowLoader.postValue(false)
                }

                override fun onError(errorMessages: String) {
                    //callBack.onError(errorMessages)
                    error.postValue(errorMessages)
                    isShowLoader.postValue(false)

                }
            })
        }
    }

    fun updateDealCommentList(dealId: String, commentSection: CommentSection) {
        isShowLoader.value = true
        FireBaseRepository().apply {
            updateDealComment(dealId, commentSection, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    commentPosted.value = true

                }

                override fun onError(errorMessages: String) {

                }

            })

        }
    }
}
