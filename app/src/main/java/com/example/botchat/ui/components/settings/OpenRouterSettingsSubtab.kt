package com.example.botchat.ui.components.settings

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.botchat.ui.theme.*

@Composable
fun OpenRouterSettingsSubTab(
    apiKey: String,
    selectedModel: String,
    showApiKey: Boolean,
    onApiKeyChange: (String) -> Unit,
    onSelectedModelChange: (String) -> Unit,
    onApiKeyVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    //Open router
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "OpenRouter Configuration",
            style = MaterialTheme.typography.titleLarge.copy(
                color = if (MaterialTheme.colorScheme.background == MidnightBlack) PureWhite else SlateBlack,
                fontSize = 18.sp
            )
        )
        ApiKeyInput(
            apiKey = apiKey,
            showApiKey = showApiKey,
            onApiKeyChange = onApiKeyChange,
            onVisibilityToggle = onApiKeyVisibilityToggle,
            label = "OpenRouter API Key"
        )
        ModelSelectionInput(
            selectedModel = selectedModel,
            onModelChange = onSelectedModelChange,
            models = listOf(
                "google/gemma-3-12b-it:free",
                "mistralai/mixtral-8x7b-instruct",
                "meta-llama/llama-3-8b-instruct",
                "anthropic/claude-3-opus",
                "openai/gpt-4-turbo",
                "meta-llama/llama-3-70b-instruct",
                "mistralai/mixtral-8x22b-instruct",
                "xai/grok-3-405b",
                "google/gemini-pro-1.5",
                "cohere/command-r-plus",
                "databricks/dbrx-instruct",
                "deepmind/sparrow-7b",
                "alibaba/qwen-72b-instruct",
                "huggingface/zephyr-7b-beta",
                "tencent/llm-100b",
                "baidu/ernie-4.0",
                "openai/gpt-3.5-turbo",
                "google/palm-2-llm",
                "meta-llama/llama-2-13b-chat",
                "mistralai/mistral-7b-instruct",
                "eleutherai/gpt-j-6b",
                "stabilityai/stable-lm-3b",
                "nvidia/nemotron-70b",
                "aws/bedrock-titan",
                "intel/neural-chat-7b",
                "deepseek/rag-66b-instruct",
                "deepseek/seed-7b",
                "deepseek/pro-128b"
            )
        )
    }
}