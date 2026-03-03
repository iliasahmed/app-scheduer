# App Scheduler (Android)

A simple Android app that lets users schedule installed apps to launch at a specific time.

## Features
- Schedule any installed app to open at a chosen time  
- Update or cancel existing schedules  
- Support multiple schedules (no time conflicts)  
- Track execution status (completed / failed)  
- View history of executed schedules  

## Screenshots

Here are some screenshots:

<p align="center">
  <img src="screenshots/home.png" alt="Home Screen" width="40%" />
  <img src="screenshots/completed.png" alt="Home Screen" width="40%" />
  <img src="screenshots/pick_app.png" alt="Pick App Screen" width="40%" />
  <img src="screenshots/schedule_time.png" alt="Schedule Time Screen" width="40%" />
</p>

## Tech Stack
- Kotlin  
- MVVM + Clean Architecture  
- Room (local database)  
- WorkManager (background scheduling)  
- Hilt (dependency injection)  
- Coroutines + Flow  

## Architecture
The project follows MVVM with Clean Architecture:
- **Presentation**: UI + ViewModels  
- **Domain**: Use cases + models  
- **Data**: Repository, DAO, Room entities

## Techincal Notes & Research
### Background Activity Launch Restrictions (Android 10+)
- One challenge related to Android background execution restrictions. Starting from Android 10 (API 29), the system limits apps from launching activities while running in the background to prevent unexpected interruptions for users.
- According to the Android Developer documentation, background activity launches are only allowed under specific conditions, such as when the app has a visible window or when the user has recently interacted with it.
- In a production-grade solution, requesting the SYSTEM_ALERT_WINDOW (“Draw over other apps”) permission would be a more reliable approach, as it allows the app to handle background launches more consistently.

### Success Detection Idea Using UsageStatsManager
- Android does not provide a direct callback to confirm whether a scheduled app was successfully brought to the foreground after triggering an Intent. Because of this limitation, I researched a potential approach to verify execution success.
- One possible solution is using the UsageStatsManager API to monitor foreground app changes. After launching the scheduled app, the system could observe usage events for a short verification window (for example, up to 10 seconds). If the target package appears as the most recent foreground activity during that period, the schedule can be marked as SUCCESS. If it does not appear within the verification window, the schedule can be recorded as FAILED.This approach would require the user to grant Usage Access permission (PACKAGE_USAGE_STATS).

## Testing
- Unit tests for ViewModels, UseCases, Repository, and Worker  
- Coroutine test utilities and MockK  

## How to Run
1. Clone the project  
2. Open in Android Studio  
3. Build & Run on an emulator or real device  
