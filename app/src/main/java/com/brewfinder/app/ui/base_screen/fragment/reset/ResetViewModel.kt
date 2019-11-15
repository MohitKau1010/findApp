package com.brewfinder.app.ui.base_screen.fragment.reset

import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack

/**
 * @author Shubham
 * 5/10/19
 */
class ResetViewModel : BaseViewModel() {

    private val repo = FireBaseRepository()

    val mSuccess = MutableLiveData<String>()
    val mError = MutableLiveData<String>()


    fun sendEmail(email: String) {

        repo.sendEmailToEmail(email, object : FirebaseNetworkCallBack {
            override fun onSuccess(response: Any) {
                mSuccess.value = response.toString()
            }

            override fun onError(errorMessages: String) {
                mError.value = errorMessages
            }

        })

    }


}