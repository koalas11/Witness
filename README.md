# Witness

> Final project for the *Android Programming Project (APP)* – Università della Svizzera italiana, Fall 2025
---

**Witness** is a mobile and wearable application developed for the *Mobile and Wearable Computing* course (Università della Svizzera italiana, Fall 2025).  
The project provides a personal security system to assist users in emergency or unsafe situations.

## Team
- **Desantis Amedeo**
- **Lesinigo Simone**
- **Sanvito Marco**

## Key Features
- Continuous **audio** and **GPS location** recording when activated
- Automatic **SMS and email alerts** sent to trusted contacts every 5 minutes with updated location
- **Immediate loud alarm sound** to draw attention and discourage threatening behavior
- Data stored **locally** and on **Google Drive** to ensure persistence and availability
- Map visualization of recorded locations and playback of captured audio
- Audio processing with **LLM** for segmentation, summarization, emotion detection, and translation
- Companion app for **Wear OS** to start/stop recording and trigger alarms remotely

## Sensors & Actuators
- **Sensors:** Microphone, GPS, Fingerprint Scanner / FaceID
- **Actuators:** Vibration, Speaker

## Code architecture overview

### High-level overview
- Purpose: share domain and data logic across platforms while keeping platform UIs native and thin.
- Main components: shared KMP modules (domain & data), platform modules/services (adapters), and UI modules (platform-specific).

### Architecture and layers
- **UI layer**
    - Platform-specific UIs (Android Compose, Wear UI, iOS SwiftUI).
    - Observes ViewModel state and sends user events to ViewModels.
- **ViewModel layer**
    - Exposes StateFlows to UI, collects Flows from UseCases/Repositories.
    - Responsible for UI state orchestration and thin transformations.
    - Created through factories that obtain dependencies from the AppContainer (no global singletons).
- **UseCase / Domain layer**
    - Encapsulates business processes and orchestration (e.g., emergency recording, contact notifications, Google integration, LLM summarization).
    - Coordinates platform services, repositories, and lower-level APIs.
- **Repository layer**
    - Abstracts concrete data sources (Room, DataStore, network).
    - Returns `Result<T>` wrappers and `Flow<T>` streams; maps exceptions into unified error types.
- **Data sources**
    - Room DB for recording metadata (entities + DAOs).
    - DataStore (serializer-backed) for settings, contacts, and OAuth/profile.
    - Ktor HTTP clients for network and authenticated Google APIs.
- **Platform services / modules (adapters)**
    - Platform-specific features implemented as modules (interface + platform implementation).
    - Injected via the AppContainer into UseCases and other consumers.

### Services / Modules (design & responsibilities)
Services (modules) are first-class adapters that provide platform behavior required by domain logic. They isolate platform code and make business logic testable.

Common modules found in the project (conceptual list):
- `VibrationModule` — device vibration control (short/long patterns).
- `SoundAlertModule` — play system sounds/alerts.
- `AudioRecorderModule` — record audio (start/stop, file management).
- `AudioPlayerModule` — playback control for recorded audio.
- `GeoRecordingModule` — obtain current location and manage GPS path collection.
- `RecordingServiceHandler` — start/stop foreground recording service (platform-specific lifecycle).
- `EmergencyContactModule` — send SMS/emails or invoke contact channels.
- `Google integration services` — authenticated calls for Gmail, Drive, Profile, token refresh.
- `LLM/Gemini wrapper` — call LLM endpoints for summarization and return structured results.

Design notes:
- Each module exposes a minimal interface consumed by UseCases.
- Platform implementations live in platform source sets and are wired in the platform `AppContainer`.
- UseCases never call platform APIs directly; they call module interfaces.

### Dependency Injection (App container pattern)
- `AppContainer` (interface) defines app-level dependencies (repositories, HTTP clients, use cases).
- `AppContainerImpl` wires DataStore, Room DB, Ktor clients, repositories and UseCases.
- `PlatformAppContainer` extends `AppContainer` with platform modules (vibration, audio, geo, service handler).
- Each platform (Android, iOS, Wear) provides a concrete container (e.g., `AndroidAppContainer`) that supplies platform module implementations.
- Consumers (ViewModels, UseCases) get dependencies from the container via factory/creation-extras (no global mutable singletons).

### Data flow example: emergency recording
1. UI triggers `startEmergencyRecording()` on ViewModel.
2. ViewModel calls `EmergencyRecordingUseCase.startEmergencyRecording()`.
3. UseCase reads settings from `SettingsRepository` (DataStore), triggers `VibrationModule`, requests location via `GeoRecordingModule`, and starts foreground recording via `RecordingServiceHandler`.
4. UseCase uses `EmergencyContactsRepository` and `EmergencyContactModule` (or `GoogleIntegrationUseCase`) to notify contacts by SMS/email periodically if configured.
5. Repositories persist metadata to Room (DAO); LLM summary calls (via `GeminiApiUseCase`) produce summaries saved back by repositories.
6. State and events flow back to ViewModel via `Flow`/`StateFlow` and are observed by the UI.

### Error handling
- Layers return `Result<T>` and `ResultError` (sealed) types to normalize success/error handling.
- Exceptions are caught at repository/use case boundaries and mapped to `ResultError`.
- ViewModels expose loading / success / error states to the UI via sealed interfaces.

### Simple diagram
```
[ UI (Compose / SwiftUI) ]
            ↓
[ ViewModel (StateFlows) ]
            ↓
[ UseCase / Domain logic ] 
            ↓
[ Repositories (Result / Flow) ] 
            ↓
[ Data sources: Room / DataStore / Ktor ]
            ↑
[ Platform Modules / Services (vibration, audio, geo, contacts) ]
```

## Screenshots

### Mobile App

<div style="text-align:center;">
  <img src="screenshots/home_screen.png" alt="Home Screen" style="width:45%; max-width:600px;" />
  <img src="screenshots/recording_info.png" alt="Recording Info" style="width:45%; max-width:600px;" />
</div>

<div style="text-align:center;">
  <img src="screenshots/recording_info_llm.png" title="App - Recording Info LLM Screen" alt="Recording Info LLM" width="45%"/>
  <img src="screenshots/google_profile.png" title="App - Google Profile Screen" alt="Google Profile" width="45%"/>
</div>

### Wearable App

<div style="text-align:center;">
  <img src="screenshots/wearable_home.png" title="Wearable App - Home Screen" alt="Wearable Home" width="45%"/>
  <img src="screenshots/wearable_whistle.png" title="Wearable App - Whistle Screen" alt="Wearable Whistle" width="45%"/>
</div>
