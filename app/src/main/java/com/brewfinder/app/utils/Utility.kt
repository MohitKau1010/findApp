package com.example.mohitmvvmfirebase.utils


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils

object Utility {


    fun Context.showDialog(activity: Activity?, message: String?) {
        val dialagBuilder = AlertDialog.Builder(activity)
        dialagBuilder.setMessage(message)
                .setCancelable(true)
                .setNegativeButton("ok",
                        DialogInterface.OnClickListener { dialag, id ->
                            run { dialag.cancel() }
                        })
        //.setPositiveButton("Ok",DialogInterface.OnClickListener({dialogInterface, i -> run {  }  }))
        // create dialog box
        val alert = dialagBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Alert!!")
        // show alert dialog
        alert.show()
    }


    // get user_name Through Email
    fun usernameFromEmail(email: String): String {
        return if (email.contains("@")) {
            email.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        } else {
            email
        }
    }
//
//    //validation start comment_date to end comment_date
//    fun datePickerValidation(startDate:String,endDate:String) :String{
//        var cal = Calendar.getInstance()
//
//        val myFormat = "dd/MM/yyyy" // mention the format you need
//        val sdf = SimpleDateFormat(myFormat, Locale.US)
//
//        if(startDate=="dd/mm/yyyy"){
//            //toast please select Start comment_date first
//            return "please select Start comment_date first"
//        }else{
//            if(startDate==sdf.format(cal.createdAt)){
//                return sdf.format(cal.createdAt)
//            }else{
//                val parts = startDate.split("/")
//                val startdate=parts[0]
//                val startmonth=parts[1]
//                val startyear=parts[2]
//
////                val endDate=sdf.format(cal.createdAt)
//                val eparts = endDate.split("/")
//                val enddate=eparts[0]
//                val endmonth=eparts[1]
//                val endyear=eparts[2]
//
//                if(startyear==endyear){
//                    if(startmonth==endmonth){
//                        if(startdate==enddate){
//                            return sdf.format(cal.createdAt)
//                        }else{
//                            if(startdate>enddate){
//                                return "disable to select previous comment_date"
//                            }else{
//                                return sdf.format(cal.createdAt)
//                            }
//
//                        }
//                    }else{
//                        if(startmonth<endmonth){
//                            return sdf.format(cal.createdAt)
//                        }else
//                            return "disable to select previous comment_date"
//                    }
//                }else{
//                    if(startyear<=endyear){
//                        return sdf.format(cal.createdAt)
//                    }else
//                        return "disable to select previous comment_date"
//                }
//            }
//        }
//
//    }

    //to check if email is valid or not
    fun isValidEmail(target: CharSequence): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    //check internet Avalable or not
    @Suppress("NAME_SHADOWING")
    fun isNetworkAvailable(context: Context?): Boolean {
        var context = context
        if (context == null && context != null) {
            context = context
        }
        var connMgr: ConnectivityManager? = null
        if (context != null) {
            connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        var networkInfo: NetworkInfo? = null
        if (connMgr != null) {
            networkInfo = connMgr.activeNetworkInfo
        }
        return networkInfo != null && networkInfo.isConnected
    }


}