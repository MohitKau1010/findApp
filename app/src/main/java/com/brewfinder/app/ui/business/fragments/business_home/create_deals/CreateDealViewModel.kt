package com.brewfinder.app.ui.business.fragments.business_home.create_deals

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.brewfinder.app.BrewFinder
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.User
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack


/**
 * @author Shubham
 * 12/10/19
 */
class CreateDealViewModel : BaseViewModel() {


    var mFirebaseRepository = FireBaseRepository()

    val isDatabaseCreated = MutableLiveData<Boolean>()

    var etStartDealTime = ""
    var etEndDealTime = ""

    var tv_start_date = ""
    var tv_end_date = ""

    var description = ""
    var discount_offer = ""
    var dealTitle = ""
    var mfileUri: Uri? = null


    var useridpref: String = ""
    var userModel: User? = null


    fun createDeals(dealImage: String) {

        if (etStartDealTime.isEmpty() || etEndDealTime.isNullOrEmpty() || tv_start_date.isNullOrEmpty() || tv_end_date.isNullOrEmpty() || description.isNullOrEmpty() || discount_offer.isNullOrEmpty() || dealTitle.isNullOrEmpty()) {
            error.value = "Please Fill All Fields"
            isShowLoader.postValue(false)
        } else {
//            val currentUser = mFireBaseRepository.currentUser()
            //val user =  mFireBaseRepository.returnFirebaseUserFromDB(currentUser?.uid.toString())

            //saves user Data
            PrefrenceUtil(BrewFinder.applicationContext()).apply {
                useridpref = userId.toString()
                userModel = userProfile
            }
            //get current createdAt
            val currentTimestamp = System.currentTimeMillis()

            val randomNumber = (1000..9999).random()
            val dealModel = Deal(
                createdAt = currentTimestamp.toString(),
                deal_user_name = userModel?.user_name,
                deal_image = dealImage,
                comments = "",
                deal_date = tv_start_date + "to" + tv_end_date,
                deal_time = etStartDealTime + "to" + etEndDealTime,
                deal_id = "",
                is_fav = "false",
                is_follow = "false",
                description = description,
                discount_offered = discount_offer,
                deal_companyName = userModel?.company_name,
                deal_company_logo = userModel?.busines_logo,
                deal_latlng = userModel?.latlng,
                redeem_code = "$randomNumber$discount_offer",
                deal_title = dealTitle
            )


            mFirebaseRepository.onCreateDeals(
                dealModel,
                object : FirebaseNetworkCallBack {
                    override fun onSuccess(response: Any) {
                        isShowLoader.postValue(false)
                        isDatabaseCreated.value = true

                    }

                    override fun onError(errorMessages: String) {
                        Toast.makeText(
                            BrewFinder.applicationContext(),
                            errorMessages,
                            Toast.LENGTH_SHORT
                        ).show()
                        isShowLoader.value = false
                    }

                })
        }

    }

    fun addimageintoStroage() {
        isShowLoader.value = true
        // viewModel.createDeals()

        mFirebaseRepository.uploadToFireBaseStorage(mfileUri,
            object : FirebaseNetworkCallBack {
                override fun onSuccess(response: Any) {
                    createDeals(response as String)
                }

                override fun onError(errorMessages: String) {
                    isShowLoader.value = false
                    error.value = errorMessages
                }

            })

    }
}