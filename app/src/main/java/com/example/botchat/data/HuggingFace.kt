package com.example.botchat.data
data class HuggingFaceRequest(
    val inputs: String,
    val parameters: HuggingFaceParameters? = null
)

data class HuggingFaceParameters(
    val max_new_tokens: Int? = null,
    val temperature: Double? = null,
    val top_k: Int? = null,
    val top_p: Double? = null,
    val do_sample: Boolean? = null
)
data class HuggingFaceResponse(
    val generated_text: String? = null,
    val error: String? = null // For handling errors
)