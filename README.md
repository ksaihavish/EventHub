# EventHub 🗓️

EventHub is a modern, offline-capable Android application built with Kotlin designed to help you easily manage, organize, and view your personal events.

## Features ✨
- **Offline First**: All events are stored locally so you can access them without an internet connection.
- **Cloud Sync**: Integrated with Firebase Firestore. Your events automatically sync in real-time across devices whenever you are online.
- **Interactive Map**: View all your scheduled events geographically on an interactive map using OSMDroid.
- **AI Event Assistant**: Features a built-in chatbot powered by Google's Gemini AI to help you plan itineraries or write event descriptions.
- **Premium UI**: Uses a beautiful cinematic dark theme (Navy and Copper) with fluid Material components.

## Getting Started 🚀

To run this project locally, you will need to provide your own API keys.

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ksaihavish/EventHub.git
   ```
2. **Add Gemini AI Key:**
   Open the `local.properties` file in the root of the project (create one if it doesn't exist) and add your Gemini API Key:
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```
3. **Add Firebase:**
   Create a Firebase project, enable **Firestore**, and place your `google-services.json` file inside the `app/` directory.

4. **Build and Run** on your Android Studio emulator or physical device.

## Tech Stack 🛠️
- **Language**: Kotlin
- **Database**: Firebase Firestore & SharedPreferences (Local Caching)
- **Maps**: OSMDroid
- **AI Integration**: Google Generative AI SDK (Gemini)
- **UI**: XML Layouts & Material Components
