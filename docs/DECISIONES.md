# Decisiones

Decisiones de producto y arquitectura que se apartan de la base KMP o que no se ven en el código.
Formato: fecha, decisión, por qué, y qué la haría cambiar.

## 2026-11-26 — Solo Android, sin Kotlin Multiplatform (STK-02, MOD-01)

- **Decisión:** un solo módulo `:app` en Android; sin `commonMain`, `composeApp` ni `iosApp`.
- **Por qué:** el mercado inicial (Perú) es mayoritariamente Android y la app aún no está publicada. Partir el código en KMP antes de validar el producto suma trabajo sin usuarios que lo pidan.
- **Cambiaría si:** se decide lanzar en iOS. La capa `domain` ya es Kotlin puro, así que es la primera candidata a `commonMain`.

## 2026-11-26 — Koin en vez de Hilt (ARQ-04)

- **Decisión:** se reemplazó Hilt por Koin 4.2 como primer paso de la migración a KMP (`docs/plans/2026-11-26-kmp-migration.md`).
- **Por qué:** Hilt solo funciona en Android y no es compatible con el plugin de biblioteca KMP de AGP 9; Koin es lo que pide la base.
- **Costo:** se pierde la verificación del grafo en compilación; una dependencia que falta aparece al abrir la pantalla, así que los flujos de Maestro la cubren.

## 2026-11-26 — Firebase en vez de un backend propio con Ktor (STK-02, TST-03)

- **Decisión:** Firebase Auth (Google con Credential Manager) y Firestore (`users/{uid}/…`) como espejo del store en memoria; Room es la caché offline. Sin Ktor ni API REST propia.
- **Por qué:** la app no procesa pagos ni necesita lógica de servidor todavía; Firestore da sincronización y modo offline sin mantener un servidor. Las reglas de seguridad viven en `firestore.rules`.
- **Cambiaría si:** hace falta lógica del lado del servidor que no quepa en Cloud Functions, o el costo de Firestore crece más que el de un backend propio.
