package com.framework.webview.dialog


import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.framework.webview.R
import com.framework.webview.common.Url
import com.framework.webview.common.Utils

class DevelopPopup(context: Context) : Dialog(context) {

    companion object {
        fun createDialog(context: Context, title: String, cancelable: Boolean): DevelopPopup {
            val customPopup = DevelopPopup(context)
            customPopup.setContentView(R.layout.dialog_develop_popup)

            val url1 = customPopup.findViewById<EditText>(R.id.et_url)
            val url2 = customPopup.findViewById<EditText>(R.id.et_url2)
            val url3 = customPopup.findViewById<TextView>(R.id.et_url3)

            val ck1 = customPopup.findViewById<CheckBox>(R.id.ck1)
            val ck2 = customPopup.findViewById<CheckBox>(R.id.ck2)
            val ck3 = customPopup.findViewById<CheckBox>(R.id.ck3)
            val ck_prod = customPopup.findViewById<CheckBox>(R.id.ck_api_prod)
            val ck_dev = customPopup.findViewById<CheckBox>(R.id.ck_api_dev)

            val reset = customPopup.findViewById<TextView>(R.id.btn_reset)


           ck1.setOnClickListener{
               ck2.isChecked = false
               ck3.isChecked = false
           }

            ck2.setOnClickListener{
                ck1.isChecked = false
                ck3.isChecked = false
            }

            ck3.setOnClickListener{
                ck1.isChecked = false
                ck2.isChecked = false
            }

            ck_dev.setOnClickListener{
                ck_prod.isChecked = false
            }

            ck_prod.setOnClickListener{
                ck_dev.isChecked = false
            }


            reset.setOnClickListener{
                url1.setText(Url.ACCESS_URL)
                url2.setText(Url.DEVELOP_URL)
                url3.setText(Url.ETC_URL)
            }

            if(Utils.getUrlPrefString(context,"url1")!=""){
                url1.setText(Utils.getUrlPrefString(context,"url1"))
            }else{
                url1.setText(Url.ACCESS_URL)
            }
            if(Utils.getUrlPrefString(context,"url2")!=""){
                url2.setText(Utils.getUrlPrefString(context,"url2"))
            }else{
                url2.setText(Url.DEVELOP_URL)
            }
            if(Utils.getUrlPrefString(context,"url3")!=""){
                url3.setText(Utils.getUrlPrefString(context,"url3"))
            }else{
                url3.setText(Url.ETC_URL)
            }



            val titletxt = customPopup.findViewById<TextView>(R.id.tv_dialog_title)
            titletxt.text = title
            val button = customPopup.findViewById<Button>(R.id.btn_login)
            button.setOnClickListener {
                customPopup.dismiss()
            }
            customPopup.setCancelable(cancelable)
            return customPopup
        }
    }



    fun onRightClick(customPopup : DevelopPopup, txt: String, clickListener: View.OnClickListener) {
        val button = customPopup.findViewById<Button>(R.id.btn_login)
        button.text = txt
        button.setOnClickListener(clickListener)
    }


    override fun onBackPressed() {
        if(this.isShowing) this.dismiss()
        super.onBackPressed()
    }

}