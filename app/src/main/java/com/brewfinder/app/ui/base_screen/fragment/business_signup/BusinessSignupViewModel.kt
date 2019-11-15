package com.brewfinder.app.ui.base_screen.fragment.business_signup

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.BrewFinder
import com.brewfinder.app.data.model.User
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.Constants
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import com.example.mohitmvvmfirebase.utils.Utility
import com.google.firebase.auth.FirebaseUser

/**
 * @author Mohit
 * 1/10/19
 */

// m using two Fregment And One ViewModel

class BusinessSignupViewModel : BaseViewModel() {

    //email and password for the input
    var mid: String? = null
    var name: String? = ""  // bUser_name
    var email: String? = ""  //bEmail
    var password: String? = ""  //Bpasswrd
    var mobile: String? = ""  //Bpassword
    //////////////////////////////////
    var companyName: String? = null //company_name
    var textLocation: MutableLiveData<String> = MutableLiveData()
    var textOpenTime: String? = "11:00Am "
    var textCloseTime: String? = "- 12:00Pm"
    var mFileImage: Uri? = null
    var companyLatlng: String? = ""
    var mAllTaskDone = MutableLiveData<Boolean>()

    val mFirebaseUser = MutableLiveData<FirebaseUser>()
    val mGeneralError = MutableLiveData<String>()
    val emptyerror = MutableLiveData<Boolean>()
    val repository = FireBaseRepository()

    fun clearInitailValues() {
        name = ""
        email = ""  //bEmail
        password = ""  //Bpasswrd
        mobile = ""  //Bpassword
        companyName = ""
        emptyerror.value = false
        mGeneralError.value = ""

    }

    init {
        textLocation.value = "Tap to get location"
    }


    fun businessSignup() {
        if (companyName.isNullOrEmpty()) {
            emptyerror.value = true
            //mGeneralError.value="Enter company name"
        } else if (textLocation.value.isNullOrEmpty() || textLocation.value == "Tap to get location") {
            mGeneralError.postValue("Please Add Location")
        } else if (mFileImage == null) {
            mGeneralError.value = "Image is Not Selected"
        } else {
            if (name.isNullOrEmpty() || email.isNullOrEmpty() || mobile.isNullOrEmpty() || textOpenTime.isNullOrEmpty() || textCloseTime.isNullOrEmpty()) {
                mGeneralError.postValue("Fill All parameters")
            } else {
                // check network
                if (Utility.isNetworkAvailable(BrewFinder.applicationContext())) {
                    isShowLoader.value = true
                    //fist authSignup
                    repository.fireBaseSignUp(
                        email!!,
                        password!!,
                        object : FirebaseNetworkCallBack {
                            override fun onSuccess(response: Any) {
                                //here callback user returns FirebaseUser
                                mFirebaseUser.value = response as FirebaseUser
                                //val name = Utility.usernameFromEmail(response.email.toString())
                                storeBusinessLogo(response.uid)
//                            mid=response.uid
//                            createDBuser(response.uid, Constants.BUSINESS_TYPE)
                            }

                            override fun onError(errorMessages: String) {
                                isShowLoader.value = false
                                mGeneralError.value = errorMessages
                            }
                        })
                }
            }
        }
    }

    private fun createDBuser(uid: String, downlaodpath: String) {
        val user = User(
            id = uid,
            user_pic = "",
            busines_logo = downlaodpath,
            user_type = Constants.BUSINESS_TYPE,
            user_name = name,
            user_email = email,
            company_name = companyName.toString(),
            user_mobile = mobile.toString(),
            user_location = textLocation.value,
            open_hours = textOpenTime + textCloseTime,
            latlng = companyLatlng.toString()
        )


        repository.onCreateUser(uid, user, object : FirebaseNetworkCallBack {
            override fun onSuccess(response: Any) {
                name = ""  // bUser_name
                email = ""  //bEmail
                password = ""  //Bpasswrd
                mobile = ""  //Bpassword

                isShowLoader.value = false
                mAllTaskDone.value = true
            }

            override fun onError(errorMessages: String) {
                isShowLoader.value = false
                mGeneralError.value = errorMessages
            }
        })
    }

    fun storeBusinessLogo(uid: String) {
        if (mFileImage == null) {
            mGeneralError.value = "Image File Not Dew To Image is Not Selected"
        } else {
            repository.uploadToFireBaseStorage(mFileImage, object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    val mdownloadUrl = response as String
                    createDBuser(uid, mdownloadUrl)
                }

                override fun onError(errorMessages: String) {
                    isShowLoader.value = false
                    mGeneralError.value = errorMessages
                }
            })
        }
    }
}







