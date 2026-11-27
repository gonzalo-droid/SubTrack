# Plan: SubTrack a Kotlin Multiplatform (Android + iOS)

Fecha: 2026-11-26. Base: BASE-CONOCIMIENTO-KMP del 2026-11-25 (modo Arrancar aplicado a una migración).

## Decisiones tomadas

| Tema | Decisión |
|---|---|
| Estrategia | En este repo, rama `feature/kmp`, por fases; Android verde al cerrar cada fase |
| Firebase en iOS | Puente nativo (PLT-02): interfaz chica en `commonMain`, SDK Android de Firebase en `androidMain`, Swift + Firebase/GoogleSignIn por SPM en `iosApp` |
| Bundle ID iOS | `com.gondroid.subtrack` (igual que el `applicationId`) |
| Stack | El de la base (1.1) y el catálogo de Cazadores: Compose Multiplatform 1.12, Koin 4.2, Room KMP + `sqlite-bundled`, DataStore KMP, lifecycle/navigation de JetBrains, kotlinx-datetime |

## Estado de partida (inventario)

- 230 archivos Kotlin, 22.4 mil líneas: `feature/` 15.3 mil, `core/` 3.8 mil, `data/` 1.7 mil, `domain/` 1.4 mil.
- `domain/`: Kotlin puro salvo `javax.inject.Inject` (48 archivos). Es lo primero que pasa a `commonMain`.
- APIs de JVM que no existen en `commonMain`: `Calendar` (9), `Locale` (7), `Date`/`SimpleDateFormat` (3), `UUID` (2), `TimeUnit` (2), `File` (1), `String.format` (25 archivos), `System.currentTimeMillis` (14).
- Específico de Android que pide `expect/actual` o una interfaz (PLT-01): Credential Manager (`core/auth`), Intents de WhatsApp/compartir/tienda (`core/util/WhatsAppHelper.kt`, 13 pantallas), `FileProvider` del CSV, permiso de notificaciones, QR con ZXing (`core/util/QrGenerator.kt`), Google Fonts descargables (`theme/Typography.kt`), Coil 2.
- Recursos: 454 strings + plurales en `values/` y `values-en/`, 16 drawables (logos de servicios).
- Tests: unitarios con Robolectric (Room en memoria), Roborazzi y 7 flujos Maestro.

## Estructura final (MOD-01, MOD-02)

```
androidApp/   Application, MainActivity, manifest, firma, R8, google-services
composeApp/   biblioteca KMP (com.android.kotlin.multiplatform.library)
  commonMain/   core, data, domain, ui (antes feature/), platform, di/Koin.kt
  androidMain/  actuals: Firebase, Credential Manager, Intents, DataStore/Room builders
  iosMain/      actuals + interfaces que implementa Swift (FirebaseBridge, GoogleSignInBridge)
  commonTest/   tests de dominio, mappers, ViewModels (Turbine)
  iosTest/      tests de Room real
  schemas/      esquemas Room exportados
iosApp/       host SwiftUI + SPM (Firebase, GoogleSignIn) + Config.xcconfig / Secrets.xcconfig
```

## Fases

Cada fase es un commit o varios atómicos, y termina con `testDebugUnitTest`, `lintDebug`, `verifyRoborazziDebug` y Maestro verdes en Android.

1. **Módulos.** Crear `androidApp` (entrada) y `composeApp` (biblioteca, por ahora con solo el target Android), y mover todo el código a `composeApp/src/androidMain` sin cambiar la lógica. Mismo `applicationId` y misma base de datos, para que una instalación existente siga funcionando.
2. **DI y dominio.** Hilt → Koin (4 módulos, ARQ-04; `koinViewModel` con parámetros en lugar de assisted injection). `domain/` a `commonMain`: quitar `@Inject`, `UUID` → `kotlin.uuid.Uuid`, fechas con kotlinx-datetime. Tests de dominio a `commonTest`.
3. **Datos.** Room KMP con `sqlite-bundled` y el mismo esquema (versión 1, sin migración); DataStore KMP; `AppStore`, `LocalStoreSync` y los mappers a `commonMain`. `FirestoreSync` pasa a común sobre una interfaz `RemoteDocuments` (get/set/delete/batch de mapas), y `AuthRepository` sobre `AuthBridge`. Implementación Android con el SDK actual. `AppResult`/`DataError` (ARQ-03) se agrega aquí.
4. **UI.** `feature/` → `commonMain/ui/screens`, `core/designsystem` → `ui/`. Strings y plurales a `composeResources` (los dos idiomas), drawables a `composeResources/drawable`, fuentes Space Grotesk y Geist empaquetadas como TTF. Coil 3. Navegación de JetBrains, con los enums de las rutas como `String` (TRP-04). `ExternalActions` (WhatsApp, compartir, tienda, correo, navegador) y `NotificationPermission` detrás de interfaces. QR común (ver dependencias abiertas). Previews en `androidMain`.
5. **iOS.** Targets `iosArm64` + `iosSimulatorArm64`, `iosApp` con SwiftUI mínimo, `MainViewController`, `initKoin` desde Swift con los puentes (ARQ-06), `FirebaseBridge.swift` y `GoogleSignInBridge.swift` por SPM, `Info.plist` (URL scheme de GoogleSignIn), `PrivacyInfo.xcprivacy`, `checkIosVersion` (VER-02). Probar en el simulador y corregir TRP-02 (hojas) y TRP-05 (barra de estado).

## Dependencias nuevas (además del stack de la base)

- **kotlinx-datetime**: reemplaza `Calendar`/`SimpleDateFormat`. Cazadores ya la usa.
- **QR** (decidido): `expect/actual`, ZXing en Android y CoreImage `CIQRCodeGenerator` en iOS. Sin dependencia nueva.
- **Coil 3** en lugar de Coil 2 (KMP).
- **Fuentes** (decidido): los TTF de Space Grotesk y Geist (licencia OFL, pesos 400–700) se bajan de Google Fonts y quedan en `composeResources/font`.

## Riesgos

- Robolectric y Roborazzi solo corren en Android: los tests de pantalla siguen en `androidUnitTest`.
- Room KMP no corre en tests JVM host para iOS: los tests de BD de iOS van al simulador (`iosTest`).
- Material 3 multiplataforma tiene bugs propios en iOS (hojas, foco, teclado): TRP-02.
- Login real de Google en iOS necesita registrar la app iOS en Firebase (`GoogleService-Info.plist`) y un client ID de iOS: lo hace Gonzalo en la consola o yo desde su Chrome con su OK.
- El diff es enorme (casi todos los archivos se mueven). Para que la revisión sea posible, los movimientos van en commits separados de los cambios de lógica.
