# BotChat: AI Chat Assistant

![BotChat Logo](https://via.placeholder.com/150?text=BotChat)
*Work in Progress: An Android app for AI-driven conversations using cloud APIs, local servers, or offline LLMs.*

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/your-repo/botchat/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2.0.0-orange.svg)](https://developer.android.com/jetpack/compose)

BotChat is an open-source AI chat app for Android, enabling users to engage with AI via OpenRouter or HuggingFace APIs, a local server, or offline LLMs. Select from various language models and enjoy a modern, responsive UI built with Jetpack Compose.

<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/25dca1f4-8647-41d0-abe5-08be38e3c5e7" width="220"></td>
    <td><img src="https://github.com/user-attachments/assets/5c6e251c-b1b6-4ab6-bc11-21e23db8e1e9" width="220"></td>
    <td><img src="https://github.com/user-attachments/assets/db5062a5-d753-4519-940d-32f3525bdad4" width="220"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/7218c424-1471-44a1-94ed-7761dc2ec4bf" width="220"></td>
    <td><img src="https://github.com/user-attachments/assets/ee5ace20-6d7c-4e3f-8caa-21e964e068ab" width="220"></td>
    <td><img src="https://github.com/user-attachments/assets/0e030a55-1094-435a-b074-45f08c5376c0" width="220"></td>
  </tr>
</table>

> **Note**: This project is under active development. Contributions are encouraged!

## Features

- **Cloud AI**:
  - Integrate with OpenRouter or HuggingFace APIs using your keys.
  - Access LLMs like LLaMA, Mistral, or GPT-based models.
- **Local Server**:
  - Connect to a custom REST API server hosting your LLM.
  - Perfect for privacy or specialized AI setups.
- **Offline LLMs**:
  - Run lightweight models (e.g., MobileBERT, DistilBERT) on-device.
  - Offline inference without internet.
- **Model Choice**:
  - Pick your preferred LLM for customized responses.
  - Switch between cloud, local, or offline modes seamlessly.
- **Sleek UI**:
  - Crafted with Jetpack Compose for a smooth, responsive interface.
  - Offers dark/light themes with gradient styling.

## Installation

### Prerequisites
- **Android Device**: API 24 (Nougat) or higher.
- **Development Setup**:
  - Android Studio Koala (2024.1.1) or later.
  - Kotlin 2.0.0, Gradle 8.0+.
- **API Keys** (optional):
  - [OpenRouter API Key](https://openrouter.ai).
  - [HuggingFace API Key](https://huggingface.co).
- **Local Server** (optional): REST API (e.g., Flask) with an LLM.
- **Offline Models** (optional): 4GB+ RAM for on-device inference.

### Steps
1. **Clone the Repo**:
   ```bash
   git clone [https://github.com/your-repo/botchat.git](https://github.com/your-repo/botchat.git)
   cd botchat
