package co.framework.security.base

import android.content.Context
import org.jetbrains.anko.alert
import android.app.Activity
import android.util.Log
import android.webkit.WebView
import co.framework.security.base.common.RootingCheck.rootCheck
import co.framework.security.base.common.Utils
import co.framework.security.base.common.Utils.isRooted
import co.framework.security.base.response.CheckHashKeyData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@Suppress("UNCHECKED_CAST")
abstract class SettingBuilder<T : SettingBuilder<BaseSecuritySetting.Builder>>(private val context: Context) {
    private val TAG = SettingBuilder::class.simpleName
    private var checkRooting = false
    private var checkForgery = false


    /**
     * 루팅 감지
     */
    fun checkRooting(checkRooting : Boolean) : T {
        this.checkRooting = checkRooting
        return this as T
    }

    /**
     * 위변조 감지
     */
    fun checkForgery(checkForgery : Boolean) : T {
        this.checkForgery = checkForgery
        return this as T
    }


    private fun checkRooting() {
        /** 루팅 폰 체크 */
        if (rootCheck() || isRooted) {
            context.alert(title = "공지", message = "루팅된 단말기에서는 이용 하실 수 없습니다.") {
                isCancelable = false
                positiveButton("확인") { (ctx as Activity).finish() }
            }.show()
        }
        checkForgery()
    }

    private fun checkForgery() {
        /** 위변조 체크 */
        val key = Utils.getKey(context)

        try {
            FirebaseDatabase.getInstance().getReference("hashes").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val checkHashKey = dataSnapshot.getValue(CheckHashKeyData::class.java)

                    if (checkHashKey != null)
                        if(checkHashKey.forgery) {
                            if (!checkHashKey.hash1.equals(key,true) && !checkHashKey.hash2.equals(key, true)) {
                                context.alert(title = "공지", message = "비정상적으로 다운받은 앱입니다.") {
                                    isCancelable = false
                                    positiveButton("확인") { (ctx as Activity).finish() }
                                }.show()
                                return
                            }
                        }
                    else {
                        Log.d(TAG, "RealTime DB 해시값 null")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            })
        } catch(e: Exception) {
            Log.d(TAG, e.toString())
        }
    }


    protected fun check(){
        if(checkRooting){
            checkRooting()
        }else if(checkForgery){
            checkForgery()
        }
    }

}
