package co.framework.domain.model

import com.google.gson.annotations.SerializedName

data class GitData(
    val name: String,
    val id: String,
    val created_at: String,
    val html_url: String
)