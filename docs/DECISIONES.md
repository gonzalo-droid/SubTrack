# Decisiones

Decisiones de producto y arquitectura que se apartan de la base KMP o que no se ven en el código.
Formato: fecha, decisión, por qué, y qué la haría cambiar.

## 2026-11-26 — Solo Android, sin Kotlin Multiplatform (STK-02, MOD-01)

- **Decisión:** un solo módulo `:app` en Android; sin `commonMain`, `composeApp` ni `iosApp`.
- **Por qué:** el mercado inicial (Perú) es mayoritariamente Android y la app aún no está publicada. Partir el código en KMP antes de validar el producto suma trabajo sin usuarios que lo pidan.
- **Cambiaría si:** se decide lanzar en iOS. La capa `domain` ya es Kotlin puro, así que es la primera candidata a `commonMain`.

## 2026-11-26 — Hilt en vez de Koin (STK-02, ARQ-04)

- **Decisión:** inyección con Hilt 2.60.1; ViewModels con argumentos usan assisted injection.
- **Por qué:** al ser solo Android, Hilt da verificación en compilación y la integración oficial con `hiltViewModel()`. Koin es la opción de la base porque funciona en KMP.
- **Cambiaría si:** el proyecto pasa a KMP.

## 2026-11-26 — Firebase en vez de un backend propio con Ktor (STK-02, TST-03)

- **Decisión:** Firebase Auth (Google con Credential Manager) y Firestore (`users/{uid}/…`) como espejo del store en memoria; Room es la caché offline. Sin Ktor ni API REST propia.
- **Por qué:** la app no procesa pagos ni necesita lógica de servidor todavía; Firestore da sincronización y modo offline sin mantener un servidor. Las reglas de seguridad viven en `firestore.rules`.
- **Cambiaría si:** hace falta lógica del lado del servidor que no quepa en Cloud Functions, o el costo de Firestore crece más que el de un backend propio.
