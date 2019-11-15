package com.brewfinder.app.ui.business.fragments.business_home.create_deals


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.User
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.business.activity.BusinessHomeActivity
import com.brewfinder.app.ui.user.fragments.home_fragment.deal_detail.DealDetailFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.showToast
import com.brewfinder.app.utils.startMyActivity
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_create_deal.*
import kotlinx.android.synthetic.main.toolbar_create_deal.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author Shubham
 * 16/10/19
 */
class CreateDealFragment : BaseFragment(), View.OnClickListener,
    CreateDealAdapter.AddImageListener {

    override val mViewModel: BaseViewModel?
        get() = viewModel


    private val mAdapter by lazy { CreateDealAdapter(mActivity, this, message) }

    var arrayList: ArrayList<Uri> =
        arrayListOf<Uri>()  //val list: ArrayList<String> = arrayListOf<String>()
    var viewModel = CreateDealViewModel()
    var spinData: String? = ""

    var mFileUri: Uri? = null
    var cal = Calendar.getInstance()

    var message = ""


    var userModel = User()
    var start_time: String? = null
    var end_time: String? = null
    private var dealModel: Deal? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_deal, container, false)

        return view
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrayList.clear()

        //for new deal// create deal
        message = ""

        tv_deal_start_time.setOnClickListener(this)
        tv_deal_end_time.setOnClickListener(this)
        tv_deal_valid_from.setOnClickListener(this)
        tv_deal_Valid_Until.setOnClickListener(this)
        toolbar_btn_Submit.setOnClickListener(this)
        back_arrow.setOnClickListener(this)

        arguments?.let {
            dealModel = it.getSerializable("editdeal") as Deal
            dealModel.apply {

                //edit profile cross
                message = "editdeal"

                title_center_text_home_tool.text = "EDIT DEAL"

                val timsplit = this!!.deal_time.toString()
                val time = timsplit.split("to")
                val startTime = time[0]
                val endTime = time[1]

                val datesplit = this.deal_date.toString()
                val date = datesplit.split("to")
                val starDate = date[0]
                val endDate = date[1]
                //Edit Deal and fix the pre filled data
                val myUri = Uri.parse(deal_image)

                mAdapter.updateList(myUri)
//                arrayList.add(deal_image)

                // set file uri in mViewModel
                viewModel.mfileUri = mFileUri
                viewModel.etStartDealTime = startTime
                viewModel.etEndDealTime = endTime
                viewModel.tv_start_date = starDate
                viewModel.tv_end_date = endDate
                viewModel.description = description.toString()
                viewModel.discount_offer = discount_offered.toString()

                tv_deal_start_time.text = startTime
                tv_deal_end_time.text = endTime
                tv_deal_Valid_Until.text = endDate
                tv_deal_valid_from.text = starDate
                et_description_data.setText(description)
                et_discount_create_deals.setText(discount_offered)
                et_deal_title.setText(deal_title)

            }
        }


        viewModel.error.observe(this, androidx.lifecycle.Observer {
            showToast(it)
        })
        viewModel.isDatabaseCreated.observe(this, androidx.lifecycle.Observer {
            activity!!.startMyActivity(BusinessHomeActivity())
            activity!!.finish()
        })


        val discountOffer = arrayOf("$", "%")

        val arrayAdapter = ArrayAdapter<String>(
            activity!!,
            android.R.layout.simple_spinner_dropdown_item,
            discountOffer
        )
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            @SuppressLint("SetTextI18n")
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                spinData = if (position == 0) {
                    "$"
                } else {
                    "%"
                }

//                var stringdiscount = et_discount_create_deals.text.toString().trim()
//
//                if (stringdiscount.contains("$")) {
//                    stringdiscount=stringdiscount.substring(0,stringdiscount.indexOf("$"))
////                    stringdiscount=stringdiscount.replace("$", spinData!!)
//                } else {
//                    stringdiscount=stringdiscount.substring(0,stringdiscount.indexOf("%"))
////                    stringdiscount=stringdiscount.replace("%", spinData!!)
//                }
//                et_discount_create_deals.setText(stringdiscount + spinData)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

//        et_discount_create_deals.setOnFocusChangeListener { view, b ->
//            if (b) {
//
//            } else {
//
//            }
//        }

        et_discount_create_deals.addTextChangedListener {
            et_discount_create_deals.append(spinData)
        }

        // set image adapter
        rvCreateDeals.adapter = mAdapter

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.tv_deal_start_time -> {
                clickTimePicker("starttime")
            }
            R.id.tv_deal_end_time -> {
                clickTimePicker("endtime")
            }
            R.id.tv_deal_valid_from -> {
                clickDatePicker("startDate")

            }
            R.id.tv_deal_Valid_Until -> {
                clickDatePicker("endDate")
            }
            R.id.toolbar_btn_Submit -> {
                if (mFileUri == null) {
                    showToast("Please add image")
                } else {
                    viewModel.description = et_description_data.text.toString()
                    viewModel.discount_offer = et_discount_create_deals.text.toString()
                    viewModel.etStartDealTime = tv_deal_start_time.text.toString()
                    viewModel.etEndDealTime = tv_deal_end_time.text.toString()
                    viewModel.tv_start_date = tv_deal_valid_from.text.toString()
                    viewModel.tv_end_date = tv_deal_Valid_Until.text.toString()
                    viewModel.dealTitle = et_deal_title.text.toString()
                    viewModel.addimageintoStroage() // then add datatime

                }
            }
            R.id.back_arrow -> {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }

        }


    }


    private fun pick_image() {
        // Pick an image from storageg
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 101)

    }

    private fun clickDatePicker(message: String) {
        //sdf.format(cal.getCreatedAt())

        // create an OnDateSetListener
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                //textview_date!!.text = sdf.format(cal.getCreatedAt())

                if (message == "startDate") {
                    tv_deal_valid_from.text = sdf.format(cal.time)
                    viewModel.tv_start_date = sdf.format(cal.time)


                } else {


                    val startDate = tv_deal_valid_from.text
                    if (startDate == "mm/dd/yyyy") {
                        //toast please select Start comment_date first
                        showToast("please select Start comment_date first")
                    } else {
                        if (startDate == sdf.format(cal.time)) {
                            tv_deal_Valid_Until.text = sdf.format(cal.time)
                            viewModel.tv_end_date = sdf.format(cal.time)
                        } else {
                            val parts = startDate.split("/")
                            val startdate = parts[0]
                            val startmonth = parts[1]
                            val startyear = parts[2]

                            val endDate = sdf.format(cal.time)
                            val eparts = endDate.split("/")
                            val enddate = eparts[0]
                            val endmonth = eparts[1]
                            val endyear = eparts[2]

                            if (startyear == endyear) {
                                if (startmonth == endmonth) {
                                    if (startdate == enddate) {
                                        tv_deal_Valid_Until.text = sdf.format(cal.time)
                                        viewModel.tv_end_date = sdf.format(cal.time)
                                    } else {
                                        if (startdate > enddate) {
                                            showToast("disable to select previous comment_date")
                                        } else {
                                            tv_deal_Valid_Until.text = sdf.format(cal.time)
                                            viewModel.tv_end_date = sdf.format(cal.time)
                                        }

                                    }
                                } else {
                                    if (startmonth < endmonth) {
                                        tv_deal_Valid_Until.text = sdf.format(cal.time)
                                        viewModel.tv_end_date = sdf.format(cal.time)
                                    } else
                                        showToast("disable to select previous comment_date")
                                }
                            } else {
                                if (startyear <= endyear) {
                                    tv_deal_Valid_Until.text = sdf.format(cal.time)
                                    viewModel.tv_end_date = sdf.format(cal.time)
                                } else
                                    showToast("disable to select previous comment_date")
                            }
                        }
                    }
                }
            }

        //get current createdAt
        val currentTimestamp = System.currentTimeMillis()

        val datePicker = DatePickerDialog(
            activity!!, dateSetListener,
            // set DatePickerDialog to point to today's comment_date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = currentTimestamp
        datePicker.show()
    }

    @SuppressLint("SetTextI18n")
    fun clickTimePicker(message: String) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        var hours: String
        var mints: String

        val tpd = TimePickerDialog(
            activity,
            TimePickerDialog.OnTimeSetListener(function = { _, h, m ->
                val AM_PM: String = if (h < 12) {
                    "AM"
                } else {
                    "PM"
                }
                hours = h.toString()
                mints = m.toString()

                if (hours.length == 1) {
                    hours = "0$hours"
                }
                if (mints.length == 1) {
                    mints = "0$mints"
                }
                //mStartTime.setText(hourOfDay + " : " + minute + " " + AM
                // _PM );
                if (message == "starttime") {
//                        start_time  //compare with start createdAt

                    tv_deal_start_time.text = "$hours:$mints$AM_PM"
                    viewModel.etStartDealTime = "$hours:$mints$AM_PM"
                } else {
//                        end_time   //compare with end createdAt

                    tv_deal_end_time.text = "$hours:$mints$AM_PM"
                    viewModel.etEndDealTime = "$hours:$mints$AM_PM"
                }

            }),
            hour,
            minute,
            false
        )
        tpd.show()
    }


    @SuppressLint("MissingSuperCall", "SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //galary
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                mFileUri = data?.data
                if (mFileUri != null) {

                    CropImage.activity(mFileUri)
                        .setAspectRatio(16, 9)
                        .start(mActivity, this)

                } else {
                    //Log.w(ContentValues.TAG, "File URI is null")
                    Toast.makeText(activity, "File uri is null.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Taking picture failed.", Toast.LENGTH_SHORT).show()
            }
        }

        //CROPING
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {

                mFileUri = result.uri

                /////

//                arrayList.add(mFileUri!!)

//                arrayList.trimToSize()
//                postion++
                // set Recycler View here
                //recycler View
                //set recycler view
                mAdapter.updateList(mFileUri!!)


                // set file uri in mViewModel
                viewModel.mfileUri = mFileUri
//            Toast.makeText(activity, "cROPING successfully.", Toast.LENGTH_SHORT).show()

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//            Exception error = result.getError();
                Toast.makeText(activity, "CROPPING FAILED failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onAddImage() {
        pick_image()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
                DealDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("editdeal", param1)
                    }
                }
    }
}


