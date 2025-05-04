package com.example.botchat.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class HuggingFaceRequest(
    @SerializedName("inputs") val inputs: String,
    @SerializedName("parameters") val parameters: Map<String, Any>? = null,
    @SerializedName("stream") val stream: Boolean = false
)

@Keep
data class HuggingFaceParameters(
    val max_new_tokens: Int? = null,
    val temperature: Double? = null,
    val top_k: Int? = null,
    val top_p: Double? = null,
    val do_sample: Boolean? = null
)

@Keep
data class HuggingFaceResponse(
    val generated_text: String? = null,
    val error: String? = null,
    val warnings: List<String>? = null
) {
    constructor() : this(null, null, null) // Empty constructor for Gson
}