package com.example.botchat.data

import androidx.annotation.Keep

@Keep
data class HuggingFaceRequest(
    val inputs: String,
    val parameters: HuggingFaceParameters? = null
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