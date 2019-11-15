package com.brewfinder.app.ui.base_screen.fragment.login

import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import com.google.firebase.auth.FirebaseUser

/**
 *@author Rashmi
 * 9/28/2019
 */
class LoginViewModel : BaseViewModel() {


    val userMutable = MutableLiveData<FirebaseUser>()
    private val signInUser = MutableLiveData<FirebaseUser>()


    fun signInUser(email: String, pass: String) {
        isShowLoader.value = true
        FireBaseRepository().apply {
            fireBaseUser(email, pass, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    // callBack.onSuccess(response)
                    userMutable.postValue(response as FirebaseUser)
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


    fun onLoggedIn() = signInUser

}