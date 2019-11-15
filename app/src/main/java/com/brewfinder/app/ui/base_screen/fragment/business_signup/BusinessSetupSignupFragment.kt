package com.brewfinder.app.ui.base_screen.fragment.business_signup


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkCallingOrSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.brewfinder.app.R
import com.brewfinder.app.databinding.FragmentBusiSignupFrag2Binding
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.activity.getloaction.GetLocationActivity
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.base_screen.fragment.welcome.WelcomeFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.showToast
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_busi_signup__frag2.*
import java.util.*


class BusinessSetupSignupFragment : BaseFragment(), View.OnClickListener {

    override val mViewModel: BaseViewModel?
        get() = viewModel

    private val mPermissionRequest = 10
    private val mPickerRequestCode = 100
    private var mFileUri: Uri? = null
    private var permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var binding: FragmentBusiSignupFrag2Binding
    lateinit var viewModel: BusinessSignupViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_busi_signup__frag2,
            container,
            false
        )

//         mViewModel = ViewModelProviders.of(this).get(BusinessSignupViewModel::class.java)
        viewModel = ViewModelProviders.of(activity!!).get(BusinessSignupViewModel::class.java)


        binding.viewmodel2 = viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_Bcompany_name.hint = "Company Name"
        et_Bcompany_name.requestFocus()

        setObserver()
        disableView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                enableView()
            } else {
                requestPermissions(
                    permissions,
                    mPermissionRequest
                )
            }
        } else {
            enableView()
        }

        // set click listener
        go_to_signIn2.setOnClickListener(this)
        ll_image_pick.setOnClickListener(this)
        tv_open_time.setOnClickListener(this)
        tv_close_time.setOnClickListener(this)

    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    private fun setObserver() {

        viewModel.mAllTaskDone.observe(activity!!, androidx.lifecycle.Observer {
            if (it) {
                viewModel.clearInitailValues()

                (mActivity).doFragmentTransaction(
                    mActivity.supportFragmentManager, R.id.flWelcomeContainer,
                    WelcomeFragment(),
                    isAddFragment = false,
                    isAddToBackStack = false,
                    allowStateLoss = false
                )
            }
        })

        viewModel.emptyerror.observe(mActivity, androidx.lifecycle.Observer {
            if (it) {
                binding.etBcompanyName.error = "Please fill company name"
            }

        })

        viewModel.mGeneralError.observe(activity!!, androidx.lifecycle.Observer {

            if (it == "Image is Not Selected") {
                showToast("Please add image logo")
            } else if (it != "") {
                showToast(it)
            }


        })
    }


    private fun disableView() {
        ll_location_pick.visibility = View.VISIBLE
    }

    private fun enableView() {
        ll_location_pick.visibility = View.VISIBLE
        ll_location_pick.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.go_to_signIn2 -> {
                (activity as BaseActivity).doFragmentTransaction(
                    activity?.supportFragmentManager!!, R.id.flWelcomeContainer,
                    WelcomeFragment(),
                    isAddFragment = false,
                    isAddToBackStack = false,
                    allowStateLoss = false
                )
            }
            R.id.ll_image_pick -> {
                pickImage()
            }
            R.id.tv_open_time -> {
                clickTimePicker("opentime")
            }
            R.id.tv_close_time -> {
                clickTimePicker("closetime")
            }
            R.id.ll_location_pick -> {

                startActivityForResult(Intent(mActivity, GetLocationActivity::class.java), 78)

            }
        }
    }


    private fun pickImage() {
        // Pick an image from storage
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, mPickerRequestCode)


    }

    @SuppressLint("MissingSuperCall", "SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //galaxy
        if (requestCode == mPickerRequestCode) {
            if (resultCode == RESULT_OK) {
                mFileUri = data?.data
                if (mFileUri != null) {

                    CropImage.activity(mFileUri)
                        .setAspectRatio(1, 1)
                        .start(mActivity, this)
                } else {
                    Toast.makeText(activity, "File uri is null.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Taking picture failed.", Toast.LENGTH_SHORT).show()
            }
        }

        //CROPPING
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {

                mFileUri = result.uri

                viewModel.mFileImage = mFileUri

                tv_image_name.text = "CompanyLogo.jpeg"

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(activity, "cROPING FAILED failed.", Toast.LENGTH_SHORT).show()
            }
        }

        //search
        if (requestCode == 78) {
            viewModel.textLocation.value = data?.getStringExtra("address")
            viewModel.companyLatlng = data?.getStringExtra("latLong")

            tv_location.text = viewModel.textLocation.value


        }
    }

    @SuppressLint("SetTextI18n")
    fun clickTimePicker(message: String) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd =
            TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener(function = { _, h, m ->

                val mFormat: String = if (h < 12) {
                    "AM"
                } else {
                    "PM"
                }

                var hours: String = h.toString()
                var mints: String = m.toString()

                if (message == "opentime") {
                    if (hours.length == 1) {
                        hours = "0$hours"
                    }
                    if (mints.length == 1) {
                        mints = "0$mints"
                    }

                    tv_open_time.text =
                        "$hours:$mints$mFormat".trim()//String.format("%02d:%02d", hourOfDay, minute)
                    viewModel.textOpenTime = "$hours:$mints$mFormat".trim()
                } else {
                    if (hours.length == 1) {
                        hours = "0$hours"
                    }
                    if (mints.length == 1) {
                        mints = "0$mints"
                    }

                    tv_close_time.text = " - $hours:$mints$mFormat".trim()
                    viewModel.textCloseTime = " - $hours:$mints$mFormat".trim()
                }

            }), hour, minute, true)

        tpd.show()
    }


    @SuppressLint("WrongConstant")
    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(
                    activity!!,
                    permissionArray[i]
                ) == PackageManager.PERMISSION_DENIED
            )
                allSuccess = false
        }
        return allSuccess
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == mPermissionRequest) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false

                    val requestAgain =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                            permissions[i]
                        )

                    if (requestAgain) {
                        Toast.makeText(activity!!, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            activity!!,
                            "Go to settings and enable the permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (allSuccess) {
                enableView()
            }
        }
    }
}



