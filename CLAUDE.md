# CLAUDE.md — SubTrack (Android + iOS, Kotlin Multiplatform)

Instrucciones para Claude Code al trabajar en este proyecto. Lee este archivo **antes** de cualquier cambio.

## Sobre el proyecto

**SubTrack** es una app Android e iOS (Kotlin Multiplatform + Compose Multiplatform) para gestionar suscripciones personales y compartidas (Netflix, Spotify, etc.) con cobranza vía WhatsApp. El usuario puede ser **admin** (cobra a otros) o **miembro** (solo ve su parte). **No procesamos pagos**: solo coordinamos.

## Stack

Sigue la base KMP (skill `base-kmp-audit`); las desviaciones están en `docs/DECISIONES.md`. Plan de la migración: `docs/plans/2026-11-26-kmp-migration.md`.

- **Lenguaje**: Kotlin 2.4 · **UI**: Compose Multiplatform 1.12 + Material 3 (`org.jetbrains.compose.*`)
- **Android**: minSdk 24 · targetSdk/compileSdk 37 · **iOS**: 16.0+ (iPhone)
- **Módulos** (MOD-01): `androidApp` (entrada Android: Application, MainActivity, manifest, firma, R8), `composeApp` (biblioteca KMP con todo el código: `commonMain`, `androidMain`, `iosMain`) e `iosApp` (host SwiftUI + SPM)
- **Arquitectura**: Clean Architecture (data / domain / presentation) con MVVM
- **DI**: Koin 4.2 (BOM). Módulos comunes en `di/Koin.kt` (`dataModule`, `domainModule`, `uiModule`) más un `platformModule` por plataforma (`Koin.android.kt`, `Koin.ios.kt`). Pantallas con `koinViewModel()`; los ViewModels con argumentos de navegación usan `koinViewModel<VM>(parameters = { parametersOf(...) })` y se registran con `viewModel { (arg: T) -> VM(arg, get(), …) }`. Una clase nueva se registra en su módulo; `KoinModulesTest` verifica el grafo.
- **Async**: Coroutines + Flow · **Fechas**: kotlinx-datetime (nunca `java.util.Calendar`)
- **Navegación**: Navigation Compose multiplataforma type-safe (con `@Serializable`)
- **Persistencia local**: Room KMP + `sqlite-bundled`; DataStore Preferences (`USER_PREFS_FILE`)
- **Imágenes**: Coil 3 con red Ktor (OkHttp en Android, Darwin en iOS)
- **Backend**: Firebase, proyecto `subtrack-f4dd3`: Auth con Google y Firestore (`users/{uid}/…`, reglas en `firestore.rules`) como espejo del store; Room es la caché offline. El código común usa `platform/RemoteDocuments` y `platform/AuthBridge`; Android los implementa con el SDK de Firebase (`androidMain/platform`) e iOS con Swift (`iosApp/iosApp/NativeBridges.swift`, PLT-02). FCM y Functions, pendientes.
- **Plataforma** (PLT-01): todo lo nativo va detrás de una interfaz o un `expect` en `commonMain/platform`: `ExternalActions` (WhatsApp, compartir, correo, ajustes), `rememberGoogleSignIn`, `rememberNotificationPermissionRequest`, `qrCodeImage`, `rememberImagePicker`.
- **Post-clone**:
  - Android: baja `androidApp/google-services.json` desde la consola de Firebase (está en `.gitignore` porque el repo es público).
  - iOS: baja `iosApp/iosApp/GoogleService-Info.plist` (app iOS `com.gondroid.subtrack` en el mismo proyecto) y copia `iosApp/Configuration/Secrets.xcconfig.example` a `Secrets.xcconfig` con tu `TEAM_ID` y el `REVERSED_CLIENT_ID`. Sin el plist la app abre, pero el login y la sincronización fallan.
- **Datos**: los repositorios (`data/repository/*RepositoryImpl`) trabajan sobre el store en memoria `AppStore`; `LocalStoreSync` lo persiste en Room fila por fila y `FirestoreSync` lo sube a Firestore. Los datos demo (`data/demo/DemoData`) solo existen en debug (`AppEnvironment.isDebug`, que fija cada punto de entrada).

## Reglas de comportamiento obligatorias

### 1. Presenta opciones antes de cambiar código
Para cualquier decisión no trivial (nueva feature, cambio de arquitectura, nueva dependencia, decisión de UX), presenta **2-3 opciones con pros/cons** antes de implementar. No asumas. Si la decisión es trivial (rename, fix tipo, import missing), procede sin preguntar.

### 2. Sigue el diseño establecido
El sistema de diseño está en `DESIGN_TOKENS.md`. **No inventes colores, tamaños, ni tipografías nuevas** sin consultar. Si necesitas un token que no existe, propónlo primero.

### 3. Respeta la Clean Architecture
- `domain` no depende de nada externo (ni Android SDK, ni Firebase, ni Room)
- `data` implementa interfaces definidas en `domain`
- `presentation` depende de `domain` a través de use cases
- **Nunca** hacer que ViewModels accedan directo al repositorio — siempre vía use cases

### 4. Datos: un solo camino
Todo dato pasa por los repositorios sobre `AppStore`; la persistencia (Room) y la nube (Firestore) se sincronizan desde ahí. No agregues llamadas de red directas desde features ni repositorios paralelos. La app debe seguir funcionando sin conexión.

### 5. Compose idiomático
- Preview obligatorio para cada Composable público (al menos uno en light + uno en dark si aplica)
- `koinViewModel()` para inyectar, no instanciar manualmente
- `collectAsStateWithLifecycle()` siempre — nunca `collectAsState()`
- Estado mínimo: un `UiState` data class por pantalla con `loading`, `data`, `error`

### 6. Nombres consistentes
- Screens: `{Feature}Screen` (ej. `DashboardScreen`, `SubscriptionDetailScreen`)
- ViewModels: `{Feature}ViewModel`
- UI States: `{Feature}UiState`
- Use Cases: verbo + sustantivo (ej. `GetSubscriptionsUseCase`, `AddMemberUseCase`)
- Destinos de navegación: objetos `@Serializable` en `navigation/Routes.kt`

### 7. Sin código muerto
No dejes clases, imports o dependencies sin usar. Si algo queda para después, marca con `// TODO: [feature/fase]` explícito.

### 8. Strings
- Todo texto visible al usuario va en `composeApp/src/commonMain/composeResources/values/strings.xml` con nombre descriptivo (ej. `dashboard_greeting_format`) y se usa con `stringResource(Res.string.x)`.
- Español es el default (`values/`). Inglés en `values-en/`: todo string nuevo va en los dos archivos con el mismo `name`.
- Formato de Compose Multiplatform: solo `%1$s` / `%1$d` con posición, sin comillas envolventes ni `\'` (se muestran tal cual).

### 9. Commits atómicos
Si el usuario pide trabajar en una feature específica, haz commits pequeños y descriptivos. Un commit = un cambio lógico.

### 10. Idioma de comunicación
**Español** para conversación con el usuario. El código, nombres de funciones, variables y comentarios técnicos en inglés.

## Estructura de paquetes

```
composeApp/src/commonMain/kotlin/com/gondroid/subtrack/
├── App.kt, AppStateViewModel.kt   // Raíz de la UI (la montan MainActivity y MainViewController)
├── core/                          // Utilidades compartidas
│   ├── designsystem/              // Theme, colors, typography (fuentes en composeResources/font), components
│   ├── navigation/                // Routes, NavHost
│   ├── config/                    // AppInfo (generado por Gradle), AppEnvironment
│   └── util/                      // Formateadores (fechas, montos), WhatsAppHelper, log
├── data/                          // demo/, local/ (Room), remote/ (sync Firestore), repository/
├── domain/                        // Kotlin puro: model/, repository/, usecase/, util/
├── platform/                      // Interfaces y expect que implementa cada plataforma
├── di/                            // Módulos de Koin
└── feature/                       // Pantallas por feature (onboarding, dashboard, …)
composeApp/src/androidMain/        // actuals Android: Firebase, Credential Manager, Intents, ZXing
composeApp/src/iosMain/            // actuals iOS, puentes que implementa Swift, MainViewController, IosEntry
iosApp/                            // Xcode: iOSApp.swift, NativeBridges.swift, Config.xcconfig
```

## Flujo de decisión al trabajar

Cuando recibas una tarea:

1. **Lee** este archivo y `DESIGN_TOKENS.md` si no los tienes en contexto (`SCAFFOLDING.md` y `ARCHITECTURE.md` son históricos, de antes de KMP)
2. **Confirma** que entendiste: resume en 2-3 líneas lo que vas a hacer
3. Si hay ambigüedad o decisión importante: **presenta opciones**
4. Implementa cambios mínimos y atómicos
5. **Valida**: compila, ejecuta previews si aplica, revisa que no rompiste nada
   - Unit tests: `./gradlew :composeApp:testAndroidHostTest` (JVM) y `./gradlew :composeApp:iosSimulatorArm64Test` (`commonTest` en iOS)
   - Lint (debe quedar en 0 errores): `./gradlew :androidApp:lintDebug` (revisa también `composeApp`)
   - Screenshot tests: `./gradlew :composeApp:verifyRoborazziAndroidHostTest` (regrabar con `recordRoborazziAndroidHostTest` si el cambio visual es intencional)
   - E2E: `.maestro/run.sh` con el emulador abierto (limpia datos y fija español antes de cada flujo; los flujos lanzan la app con `fakeAuth: true`, que en debug salta el login real de Google) (si Maestro no conecta, desinstala `dev.mobile.maestro` y `dev.mobile.maestro.test` y reintenta)
   - iOS: `cd iosApp && xcodebuild -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 17' -derivedDataPath build/DerivedData CODE_SIGNING_ALLOWED=NO build`; para entrar sin cuenta, lanza con `-fakeAuth` (solo Debug)
   - Versión: `./gradlew check` falla si `iosApp/Configuration/Config.xcconfig` no repite la de `libs.versions.toml` (VER-02)
6. Reporta con un resumen breve de archivos tocados y por qué

## Publicación (firma y respaldos)

- **Llave de subida** (SEC-03): vive fuera del repo, en `~/.config/subtrack-signing/upload.jks`, alias `upload`. Se creó una sola vez con:
  `keytool -genkeypair -v -keystore ~/.config/subtrack-signing/upload.jks -alias upload -keyalg RSA -keysize 4096 -validity 10000`
- `local.properties` (fuera de git) le da la ruta y las contraseñas al build de release: `subtrack.signing.storeFile`, `subtrack.signing.storePassword`, `subtrack.signing.keyAlias`, `subtrack.signing.keyPassword`. Sin ellas el release sale sin firmar y Gradle avisa.
- Play App Signing debe quedar activado en Play Console: Google guarda la llave de la app y la de subida se puede cambiar si se pierde.
- **Respaldo** (SEC-04), dónde va y no la llave en sí: la keystore y sus contraseñas se guardan en el gestor de contraseñas del dueño, con una copia del `.jks` fuera de este Mac; `androidApp/google-services.json` se vuelve a bajar desde la consola de Firebase (proyecto `subtrack-f4dd3`).

## Nunca hagas esto

- Agregar dependencias sin consultar
- Inventar iconos/colores/tipografías fuera del design system
- Copiar y pegar sin adaptar al contexto
- Crear abstracciones prematuras (interfaces que solo tienen una implementación sin razón)
- Modificar código que no tiene relación directa con la tarea
- Escribir código en inglés en comentarios o UI dirigidos al usuario final

## Contexto de producto clave

- **Mercado**: LATAM, foco Perú (moneda S/ PEN)
- **Idioma default**: Español
- **Competencia referente**: Splitwise (pero enfocado solo en suscripciones)
- **Diferenciador**: integración WhatsApp para cobranza + sin procesamiento de pagos
- **Modelo de negocio**: Freemium. Free hasta 2 compartidas, Pro ilimitado + stats avanzadas
- **Referidos**: 1 invitado activo = 1 mes Pro gratis para ambos