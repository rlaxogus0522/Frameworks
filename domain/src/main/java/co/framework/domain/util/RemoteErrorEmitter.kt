package co.framework.domain.util

interface RemoteErrorEmitter {
    fun onError(msg : String)
    fun onError(errorType : ErrorType)
}