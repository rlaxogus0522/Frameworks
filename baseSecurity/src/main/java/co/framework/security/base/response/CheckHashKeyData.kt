package co.framework.security.base.response

data class CheckHashKeyData(
    val forgery : Boolean = false,
    val hash1 : String = "",
    val hash2 : String = ""
)
