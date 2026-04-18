# CLAUDE.md — SubTrack Android

Instrucciones para Claude Code al trabajar en este proyecto. Lee este archivo **antes** de cualquier cambio.

## Sobre el proyecto

**SubTrack** es una app Android para gestionar suscripciones personales y compartidas (Netflix, Spotify, etc.) con cobranza vía WhatsApp. El usuario puede ser **admin** (cobra a otros) o **miembro** (solo ve su parte). **No procesamos pagos**: solo coordinamos.

## Stack

- **Lenguaje**: Kotlin 2.0+
- **UI**: Jetpack Compose + Material 3
- **minSdk**: 24 · **targetSdk**: 34+ · **compileSdk**: 34+
- **Arquitectura**: Clean Architecture (data / domain / presentation) con MVVM
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Navegación**: Navigation Compose type-safe (con `@Serializable`)
- **Persistencia local**: Room (fase 2)
- **Backend**: Firebase (Auth, Firestore, Functions, FCM) — se integra en **fase 2**
- **Fase 1 actual**: UI completa con `MockRepository` que devuelve datos hardcodeados

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

### 4. Mock primero, real después
Durante la fase 1, **todo dato viene de `MockRepository`**. No crees integraciones con Firebase aún, no crees llamadas de red. El flujo debe funcionar 100% con datos hardcodeados. Esto permite probar UX sin backend.

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
- Español es el default. Inglés se agrega después.

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
│   ├── mock/                      // MockRepository + mock data (FASE 1)
│   ├── remote/                    // Firebase sources (FASE 2)
│   ├── local/                     // Room DAO + entities (FASE 2)
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
6. Reporta con un resumen breve de archivos tocados y por qué

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