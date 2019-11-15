package com.brewfinder.app.ui.base_screen.fragment.register

import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.data.model.User
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import com.google.firebase.auth.FirebaseUser

/**
 *@author Rashmi
 * 10/1/2019
 */
class RegisterViewModel : BaseViewModel() {

    val userMutable = MutableLiveData<FirebaseUser>()
    val isUserCreated = MutableLiveData<Boolean>()  //for database

    //creating Account
    fun signUpUser(email: String, pass: String) {
        FireBaseRepository().apply {
            fireBaseSignUp(email, pass, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    userMutable.postValue(response as FirebaseUser)
                }

                override fun onError(errorMessages: String) {
                    error.postValue(errorMessages)
                }
            })
        }
    }

    //Saving user data to database
    fun dataBaseUser(firebaseAuthId: String, _user: User) {
        FireBaseRepository().apply {
            onCreateUser(firebaseAuthId, _user, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    isUserCreated.postValue(true)
                }

                override fun onError(errorMessages: String) {
                    isUserCreated.postValue(false)
                }

            })
        }
    }

    fun dataBase() = isUserCreated
}