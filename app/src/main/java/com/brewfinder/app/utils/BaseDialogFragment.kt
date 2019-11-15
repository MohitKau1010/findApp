package com.brewfinder.app.utils

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.brewfinder.app.R

/**
 * @author Rashmi
 * 25/10/19
 */

abstract class BaseDialogFragment : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(layoutId, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme)
        // making fragment dialog transparent and of fullscreen width
        val dialog = dialog
        if (null != dialog) {
            if (isFullScreenDialog) {
                dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
            } else {
                dialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        init()
    }

    abstract val isFullScreenDialog: Boolean
    abstract val layoutId: Int
    abstract fun init()
}