package co.framework.data.util

import android.util.Log
import co.framework.domain.util.ErrorType
import co.framework.domain.util.RemoteErrorEmitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

abstract class BaseRepository {
    companion object {
        private const val TAG = "BaseRemoteRepository"
        private const val MESSAGE_KEY = "message"
        private const val ERROR_KEY = "error"
    }

    suspend inline fun <T> safeCallApi(emitter: RemoteErrorEmitter, crossinline callFunction : suspend () -> T) : T? {
        return try{
            val myObject = withContext(Dispatchers.IO){callFunction.invoke()}
            myObject
        }catch (e : Exception){
            withContext(Dispatchers.Main){
                e.printStackTrace()
                Log.e("BaseRemoteRepository", e.localizedMessage, e.cause)
                when(e) {
                    is HttpException -> {
                        if(e.code() == 401) emitter.onError(ErrorType.SESSION_EXPIRED)
                        else {
                            val body = e.response()?.errorBody()
                            emitter.onError(getErrorMessage(body))
                        }
                    }
                    is SocketTimeoutException -> emitter.onError(ErrorType.TIMEOUT)
                    is IOException -> emitter.onError(ErrorType.NETWORK)
                    else -> emitter.onError(ErrorType.UNKNOWN)
                }
            }
            null
        }
    }



    fun getErrorMessage(responseBody : ResponseBody?) : String {
        return try{
            val jsonObject = JSONObject(responseBody!!.string())
            when{
                jsonObject.has(MESSAGE_KEY) -> jsonObject.getString(MESSAGE_KEY)
                jsonObject.has(ERROR_KEY) -> jsonObject.getString(ERROR_KEY)
                else -> "Something wrong happened"
            }
        }catch (e : Exception){
            e.printStackTrace()
            "Something wrong happened"
        }
        
    }

}