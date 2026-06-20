# AI Tutor module for Science-Lab-Simulator

This module provides a Material 3 Jetpack Compose chat UI and a small, pluggable AI client to connect to AI APIs.

Quick setup

1. Include the module in your root settings.gradle (or settings.gradle.kts):

   include(":ai-tutor")
   project(":ai-tutor").projectDir = file("ai-tutor")

2. In your app module's dependencies add:

   implementation project(":ai-tutor")

3. Add an API key for local development. You can either:
   - Use EncryptedSharedPreferences at runtime (recommended)
   - Set the API key as a buildConfigField in your app's build.gradle for debug only

What I added
- A new Android library module `ai-tutor` with:
  - A Compose Material3 ChatScreen and SettingsScreen
  - A lightweight AiClient abstraction and a basic OkHttp-based HttpAiClient
  - Room entities and a DAO skeleton to persist conversations

Notes
- This module intentionally does not hard-code provider-specific logic. HttpAiClient posts JSON to a configurable baseUrl and uses an Authorization header. You can wire it to OpenAI, Anthropic, or an internal LLM gateway.
- After importing the module, open MainActivity and add a navigation route to the ChatScreen (an example in the README below).

