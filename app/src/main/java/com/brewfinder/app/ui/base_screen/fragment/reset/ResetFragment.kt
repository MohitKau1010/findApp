package com.brewfinder.app.ui.base_screen.fragment.reset


import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.brewfinder.app.R
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.welcome.WelcomeActivity
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.startMyActivityWithNewTask
import kotlinx.android.synthetic.main.fragment_reset.*


/**
 * @author Shubham
 * 28/9/19
 */
class ResetFragment : BaseFragment(), View.OnClickListener {
    override val mViewModel: BaseViewModel?
        get() = mResetviewModel
    //Making a Variable for Reset View Model
    private lateinit var mResetviewModel: ResetViewModel

    //Function for assing values to the View Model Variable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mResetviewModel = ViewModelProviders.of(this).get(ResetViewModel::class.java)

    }

    //Function for Inflate the Reset Fragment
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reset, container, false)
        return view

    }

    //Function for Saved Instance State on Button click and apply Observer for checking the Status
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSubmit.setOnClickListener(this)

        mResetviewModel.mSuccess.observe(this, Observer {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            activity?.startMyActivityWithNewTask(WelcomeActivity())
//            activity?.startMyActivityWithNewTask(BusinessHomeActivity())
        })
        mResetviewModel.mError.observe(this, Observer {
            Toast.makeText(activity, "" + it, Toast.LENGTH_SHORT).show()
        })
    }


    override fun onClick(view: View?) {
        (activity as BaseActivity).hideKeyboard(activity!!)
        when (view?.id) {
            R.id.btnSubmit -> {
                val emailtext = etSubmitEmail.text.toString().trim()

                if (emailtext.isEmpty()) {
                    Toast.makeText(activity!!, "Please enter email", Toast.LENGTH_SHORT).show()
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailtext).matches()) {
                    Toast.makeText(activity!!, "Please enter valid email", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    mResetviewModel.sendEmail(emailtext)
                    //activity?.startMyActivityWithNewTask(WelcomeActivity())


                }

            }
        }
    }

}
