# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - <!-- TODO: fecha de release -->

First version: Android and iOS from one Kotlin Multiplatform codebase.

### Added
- Personal and shared subscriptions with members, cutoff day on a 7-column grid and a "Who pays?" step in the create wizard
- Collecting over WhatsApp: per-member and bulk reminders with editable message templates
- People screen with "Owe me / I owe" tabs; people from paused subscriptions stay listed, dimmed
- Member payment history with punctuality, pause/edit/delete menu in the subscription detail
- Google sign-in and Firestore sync (`users/{uid}`), Room as the offline cache; demo data only in debug builds
- Real sign-out and account deletion (cloud data first, then the account), with links to privacy and terms
- Settings: export as a CSV attachment, notifications and per-app language, share the app, rate the app, open source licenses and a contact mail with version and device
- English translation and per-app language (Spanish by default)
- Notification permission asked right after the first subscription, never at launch
- iOS app (iPhone, iOS 16+) with Firebase Auth, Firestore and Google Sign-In through Swift bridges
- Minimum supported version in `config/app` with an "Update the app" screen that never blocks offline
- `schemaVersion` in the user document so future data changes stay compatible
- Own pages (privacy, terms) open in an in-app browser: Custom Tabs on Android, Safari view on iOS
- Clear "no connection" message when sign-in, sign-out or account deletion fail for lack of network

### Changed
- Kotlin Multiplatform with Compose Multiplatform: `androidApp`, `composeApp` and `iosApp` modules
- Dependency injection with Koin instead of Hilt
- Room KMP with `sqlite-bundled`, DataStore Preferences core; Room writes only the rows that changed
- Fonts (Space Grotesk, Geist, JetBrains Mono) bundled with the app instead of downloaded
- Red, blue, purple and pink accents use high-contrast variants so text passes WCAG AA on every surface
- Dates, month names and amounts follow the app language through shared formatters
- Service grid adapts to tablets instead of stretching three huge tiles
- Release builds signed with the upload key from `local.properties`; the build runs without `google-services.json`

### Fixed
- Apple TV+, Apple Music and Crunchyroll showed each other's logos
- The bulk reminder used the default templates instead of the user's edited ones
- Exception text could reach the user; screens now show a generic or no-connection message
- Android backups no longer copy the app's local data (phones and amounts of other people)
- Admin badge used a fixed id instead of the signed-in user
- Debt counted differently across screens; one definition now
- Large fonts broke the dashboard and the bottom bar; touch targets below 48 dp; missing TalkBack labels
- `text_tertiary` contrast raised to WCAG AA
