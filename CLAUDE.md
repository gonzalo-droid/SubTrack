# CLAUDE.md — SubTrack Android

Instrucciones para Claude Code al trabajar en este proyecto. Lee este archivo **antes** de cualquier cambio.

## Sobre el proyecto

**SubTrack** es una app Android para gestionar suscripciones personales y compartidas (Netflix, Spotify, etc.) con cobranza vía WhatsApp. El usuario puede ser **admin** (cobra a otros) o **miembro** (solo ve su parte). **No procesamos pagos**: solo coordinamos.

## Stack

- **Lenguaje**: Kotlin 2.0+
- **UI**: Jetpack Compose + Material 3
- **minSdk**: 24 · **targetSdk**: 34+ · **compileSdk**: 34+
- **Arquitectura**: Clean Architecture (data / domain / presentation) con MVVM
- **DI**: Hilt 2.60.1 (activo). Pantallas con `hiltViewModel()`; los ViewModels con argumentos de navegación usan assisted injection (`hiltViewModel<VM, VM.Factory>(creationCallback = { it.create(...) })`). No crear `ViewModelProvider.Factory` manuales.
- **Async**: Coroutines + Flow
- **Navegación**: Navigation Compose type-safe (con `@Serializable`)
- **Persistencia local**: Room (fase 2)
- **Backend**: Firebase, proyecto `subtrack-f4dd3` (activo): Auth con Google (Credential Manager) y Firestore (`users/{uid}/…`, reglas en `firestore.rules`) como espejo del store; Room es la caché offline. FCM y Functions, pendientes.
- **Post-clone**: baja `app/google-services.json` desde la consola de Firebase (está en `.gitignore` porque el repo es público).
- **Datos**: los repositorios (`data/repository/*RepositoryImpl`) trabajan sobre el store en memoria `AppStore`; `LocalStoreSync` lo persiste en Room fila por fila y `FirestoreSync` lo sube a Firestore. Los datos demo (`data/demo/DemoData`) solo existen en debug.

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
- `hiltViewModel()` para inyectar, no instanciar manualmente
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
- Todo texto visible al usuario va en `strings.xml` con nombre descriptivo (ej. `dashboard_greeting_format`, `subscription_shared_tag`)
- Español es el default (`values/`). Inglés en `values-en/`: todo string nuevo va en los dos archivos con el mismo `name`.

### 9. Commits atómicos
Si el usuario pide trabajar en una feature específica, haz commits pequeños y descriptivos. Un commit = un cambio lógico.

### 10. Idioma de comunicación
**Español** para conversación con el usuario. El código, nombres de funciones, variables y comentarios técnicos en inglés.

## Estructura de paquetes

```
com.gondroid.subtrack/
├── core/                          // Utilidades compartidas
│   ├── designsystem/              // Theme, colors, typography, components
│   ├── navigation/                // Routes, NavHost
│   ├── util/                      // Extensions, formatters
│   └── di/                        // Modules de Hilt globales
│
├── data/                          // Implementaciones de repositorios
│   ├── demo/                      // DemoData: datos de ejemplo (solo debug)
│   ├── remote/                    // Firebase: auth y sync con Firestore
│   ├── local/                     // Room DAO + entities
│   └── repository/                // Implementaciones de interfaces
│
├── domain/                        // Pure Kotlin, sin Android
│   ├── model/                     // Subscription, Member, Payment...
│   ├── repository/                // Interfaces
│   └── usecase/                   // Una clase por use case
│
└── feature/                       // Features, cada una con su UI
    ├── onboarding/
    ├── dashboard/
    ├── subscriptionlist/
    ├── subscriptiondetail/
    ├── createsubscription/
    ├── members/
    ├── people/
    └── profile/
```

## Flujo de decisión al trabajar

Cuando recibas una tarea:

1. **Lee** este archivo, `DESIGN_TOKENS.md` y `SCAFFOLDING.md` si no los tienes en contexto
2. **Confirma** que entendiste: resume en 2-3 líneas lo que vas a hacer
3. Si hay ambigüedad o decisión importante: **presenta opciones**
4. Implementa cambios mínimos y atómicos
5. **Valida**: compila, ejecuta previews si aplica, revisa que no rompiste nada
   - Unit tests: `./gradlew testDebugUnitTest`
   - Lint (debe quedar en 0 errores): `./gradlew lintDebug`
   - Screenshot tests: `./gradlew verifyRoborazziDebug` (regrabar con `recordRoborazziDebug` si el cambio visual es intencional)
   - E2E: `.maestro/run.sh` con el emulador abierto (limpia datos y fija español antes de cada flujo; los flujos lanzan la app con `fakeAuth: true`, que en debug salta el login real de Google) (si Maestro no conecta, desinstala `dev.mobile.maestro` y `dev.mobile.maestro.test` y reintenta)
6. Reporta con un resumen breve de archivos tocados y por qué

## Publicación (firma y respaldos)

- **Llave de subida** (SEC-03): vive fuera del repo, en `~/.config/subtrack-signing/upload.jks`, alias `upload`. Se creó una sola vez con:
  `keytool -genkeypair -v -keystore ~/.config/subtrack-signing/upload.jks -alias upload -keyalg RSA -keysize 4096 -validity 10000`
- `local.properties` (fuera de git) le da la ruta y las contraseñas al build de release: `subtrack.signing.storeFile`, `subtrack.signing.storePassword`, `subtrack.signing.keyAlias`, `subtrack.signing.keyPassword`. Sin ellas el release sale sin firmar y Gradle avisa.
- Play App Signing debe quedar activado en Play Console: Google guarda la llave de la app y la de subida se puede cambiar si se pierde.
- **Respaldo** (SEC-04), dónde va y no la llave en sí: la keystore y sus contraseñas se guardan en el gestor de contraseñas del dueño, con una copia del `.jks` fuera de este Mac; `app/google-services.json` se vuelve a bajar desde la consola de Firebase (proyecto `subtrack-f4dd3`).

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