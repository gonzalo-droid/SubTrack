# Decisiones

Decisiones de producto y arquitectura que se apartan de la base KMP o que no se ven en el código.
Formato: fecha, decisión, por qué, y qué la haría cambiar.

## 2026-11-26 — Kotlin Multiplatform para Android e iOS (MOD-01, STK-02)

- **Decisión:** SubTrack pasa a KMP + Compose Multiplatform con `androidApp`, `composeApp` e `iosApp`, siguiendo la base. Reemplaza la decisión anterior del mismo día ("solo Android").
- **Por qué:** el dueño decidió publicar también en iOS; una sola base de código mantiene iguales la lógica y la UI.
- **Cómo:** migración en este repo por fases, con Android verde en cada una (`docs/plans/2026-11-26-kmp-migration.md`).

## 2026-11-26 — Firebase en iOS con puentes Swift (PLT-02)

- **Decisión:** el código común usa dos interfaces chicas, `RemoteDocuments` (leer, listar y escribir en lote) y `AuthBridge`; Android las implementa con el SDK de Firebase y iOS con Swift (`NativeBridges.swift`), pasando los datos como JSON. Google Sign-In en iOS también va por Swift (paquete GoogleSignIn).
- **Por qué:** es el patrón de la base para SDKs que solo existen en Swift; evita una librería de terceros que envuelva Firebase (GitLive) y deja toda la lógica de `FirestoreSync` en común.
- **Costo:** un poco de Swift por mantener; el JSON pierde tipos especiales de Firestore (Timestamp), que la app no usa.

## 2026-11-26 — Paquetes `feature/` y `core/designsystem` en vez de `ui/` (MOD-02)

- **Decisión:** se conserva la estructura de paquetes que ya tenía la app (`feature/<pantalla>`, `core/designsystem`, `core/navigation`) en lugar de `ui/screens`, `ui/theme` y `ui/navigation`.
- **Por qué:** renombrar 200 archivos no cambia el comportamiento y haría ilegible el historial de la migración; las capas siguen separadas igual.
- **Cambiaría si:** se arma una plantilla común con otras apps del estudio.

## 2026-11-26 — Fuentes empaquetadas, Coil 3 con Ktor y QR nativo

- **Decisión:** Space Grotesk, Geist y JetBrains Mono van como TTF variables en `composeResources/font` (licencias OFL en `docs/licenses`); las imágenes remotas se cargan con Coil 3 + `coil-network-ktor3` (Ktor está en el stack de la base); el QR se genera con ZXing en Android y CoreImage en iOS (`expect fun qrCodeImage`).
- **Por qué:** Google Fonts descargables y ZXing solo existen en Android; Coil 3 necesita un cliente de red en iOS para la foto de perfil de Google.

## 2026-11-26 — iOS 16 como mínimo

- **Decisión:** `IPHONEOS_DEPLOYMENT_TARGET = 16.0`.
- **Por qué:** abrir los ajustes de notificaciones de la app (`UIApplicationOpenNotificationSettingsURLString`) necesita iOS 16, y la app aún no tiene usuarios en versiones anteriores.

## 2026-11-26 — Koin en vez de Hilt (ARQ-04)

- **Decisión:** se reemplazó Hilt por Koin 4.2 como primer paso de la migración a KMP (`docs/plans/2026-11-26-kmp-migration.md`).
- **Por qué:** Hilt solo funciona en Android y no es compatible con el plugin de biblioteca KMP de AGP 9; Koin es lo que pide la base.
- **Costo:** se pierde la verificación del grafo en compilación; `KoinModulesTest` la recupera en los tests y Maestro cubre el arranque.

## 2026-11-26 — Firebase en vez de un backend propio con Ktor (STK-02, TST-03)

- **Decisión:** Firebase Auth (Google con Credential Manager) y Firestore (`users/{uid}/…`) como espejo del store en memoria; Room es la caché offline. Sin Ktor ni API REST propia.
- **Por qué:** la app no procesa pagos ni necesita lógica de servidor todavía; Firestore da sincronización y modo offline sin mantener un servidor. Las reglas de seguridad viven en `firestore.rules`.
- **Cambiaría si:** hace falta lógica del lado del servidor que no quepa en Cloud Functions, o el costo de Firestore crece más que el de un backend propio.
