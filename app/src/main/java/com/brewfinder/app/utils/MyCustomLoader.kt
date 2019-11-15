package com.brewfinder.app.utils


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


class MyCustomLoader(private val mContext: Context?) {

    private var mDialog: Dialog? = null

    fun showToast(contentMsg: String) {
        if (null != mContext) {
            Toast.makeText(mContext, contentMsg, Toast.LENGTH_SHORT).show()
        }
    }

    fun showAlertDialog(title: String, message: String, positiveButtonText: String, negativeButtonText: String, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(mContext!!)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, okListener)
                .setNegativeButton(negativeButtonText, cancelListener)
                .create()
                .show()
    }

    fun showSnackBar(view: View?, contentMsg: String) {
        if (null != mContext && null != view) {
            Snackbar.make(view, contentMsg, Snackbar.LENGTH_LONG)
                    .setAction(
                            mContext.getString(com.brewfinder.app.R.string.action_okay)
                    ) { }
                    .setActionTextColor(ContextCompat.getColor(mContext, com.brewfinder.app.R.color.colorPrimary))
                    .show()
        }
    }

    fun showProgressDialog(context: Context) {
//
        mDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val view = (mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(com.brewfinder.app.R.layout.dialog_progress_loader, null)
            setContentView(view)
            setCancelable(false)
            show()
        }
    }

    fun dismissProgressDialog() {
        if (null != mContext && null != mDialog && mDialog!!.isShowing) {
            mDialog?.dismiss()
        }
    }

}