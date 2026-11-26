# Pendientes

Tareas abiertas de SubTrack, ordenadas por prioridad. Los IDs (`ACC-02`, `PRIV-01`…) son reglas de la base KMP;
el detalle y la evidencia están en [`auditorias/2026-11-26-base-kmp.md`](auditorias/2026-11-26-base-kmp.md).
Al cerrar una tarea, bórrala de aquí y anota la decisión en `DECISIONES.md` si hubo que elegir algo.

## Alta: antes del primer build para tienda

- [ ] **Login real** — Probar Google + Firestore con una cuenta real en un dispositivo: el usuario aparece en Authentication, sus datos en `users/{uid}`, y "Eliminar cuenta" borra ambos.
- [ ] **Publicar las páginas legales** — Revisar y desplegar la rama `feature/subtrack-app` del repo de Nekodojo (privacidad, términos, soporte y borrado de cuenta). Revisar los textos agregados además de los hechos de la app: derechos de la Ley 29733, ley peruana aplicable y uso de WhatsApp.
- [ ] **SEC-03** — Crear la llave de subida con el comando de "Publicación" en `CLAUDE.md`, poner `subtrack.signing.*` en `local.properties`, guardar el respaldo y activar Play App Signing.

## Media

- [ ] **ARQ-03** — Errores tipados (`DataError`) en la capa de datos, para que la UI distinga sin conexión de otros fallos (hoy muestra siempre el mensaje genérico).
- [ ] **LNK-01** — La política y los términos se abren en el navegador; la base pide WebView para páginas propias. Decidir y anotar.
- [ ] **ACC-04** — Permitir usar la app sin cuenta (los datos ya son locales), o anotar en `DECISIONES.md` por qué se exige.
- [ ] **AJU-01 / AJU-02 / AJU-03 / AJU-05** — Ajustes (privacidad y términos ya están): licencias, "Compartir la app" (hoja del sistema), "Valorar la app" (`market://details?id=com.gondroid.subtrack`) y contacto con destinatario + versión, modelo y Android en el cuerpo.
- [ ] **SEC-02** — Que el build funcione sin `google-services.json`: aplicar el plugin solo si existe y avisar con un warning.
- [ ] **UPD-01 / UPD-02 / UPD-04** — Versión mínima (`minVersionCode`) en un documento de Firestore, pantalla "Actualiza la app" que nunca bloquea sin red, y `schemaVersion` en el perfil `users/{uid}`.
- [ ] **DOC-01 / DOC-02 / DOC-03 / STK-02** — Crear `docs/DECISIONES.md` (solo Android, Hilt, Firebase en vez de Ktor) y `CHANGELOG.md` con la 0.1.0; README con capturas y el stack real (Kotlin 2.4.21, Hilt 2.60.1).
- [ ] **GIT-01 / GIT-02 / GIT-08 / PUB-16** — Flujo `release/0.1.0` → `master` con tag `v0.1.0`; borrar las ramas unidas `feature/pending-batch` y `chore/tech-debt`.
- [ ] **COD-02 / TST-06** — `ContrastTest` (4.5:1 texto, 3:1 elementos) y los 66 colores fijos de `feature/` pasados a tokens.
- [ ] **CI-01** — Hook `.githooks/pre-push` con `./gradlew check` y `git config core.hooksPath .githooks` anotado en el README.

## Baja

- [ ] **COD-06** — Quitar los `!!` de `PaymentHistoryRow.kt:38` y `GetInsightsUseCase.kt:22`.
- [ ] **TST-05** — Tests de ViewModel con Turbine (empezar por `OnboardingViewModel` y `CreateSubscriptionViewModel`).
- [ ] **COD-04** — Sacar la escritura del CSV de `SettingsScreen.kt:104` a un caso de uso.
- [ ] **COD-05** — Separar en archivos las secciones de las pantallas de más de 300 líneas (empezar por `DashboardScreen.kt`, 1004).
- [ ] **ARQ-01 / ARQ-02 / ARQ-05** — `UiState` + `StateFlow` en Settings, Profile, Referral y SubscriptionDetailRouter; catálogo de servicios por caso de uso; un `@ApplicationScope` de Hilt compartido.
- [ ] **COD-01 / A11Y-02 / I18N-02** — Literales y `contentDescription` (`STIconButton.kt:105-126`) a `strings.xml`; formateo de montos centralizado y testeado.
- [ ] **VER-01** — `versionName` / `versionCode` en `libs.versions.toml`.
- [ ] **PERF-01** — Medir tamaño del AAB y arranque en frío antes del primer release.
- [ ] **DOC-04 / SEC-05** — Capturas de tienda (`store-screenshots`) y regla en `.gitignore` para el material de marketing.
- [ ] **TST-07** — Preparar `MigrationTestHelper` antes de la versión 2 de la base de datos.

## Antes de activar una función planeada

- **Pro (pagos):** PAY-01 (Play Billing + RevenueCat), PAY-03 (preguntar por el derecho `premium`), PAY-04 ("Restaurar compras"), PAY-05 (sandbox), PAY-06 (privacidad).
- **Recordatorios (push):** PUSH-01 (FCM), PUSH-03 (canales con nombres claros), PUSH-04 (tocar la notificación abre la pantalla correcta).
- **Referidos (deep links):** DL-01 (App Links verificados con `assetlinks.json` en `subtrack.app`), DL-02 (URL → ruta testeada; una URL desconocida abre Inicio).

## Pruebas en dispositivo o consola (una sola sesión)

- [ ] Release con R8 en claro, oscuro, letra 2× y modo avión (TST-09, PUB-02, TRP-01).
- [ ] TalkBack por el flujo principal y zonas táctiles de 48 dp (A11Y-03, A11Y-04).
- [ ] Insets con gestos y con 3 botones (TIE-03); tablet ≥ 600 dp en horizontal (TIE-04).
- [ ] Negar notificaciones y seguir usando la app (PERM-02).
- [ ] Confirmar quién controla `subtrack.app` y si `/download` redirige por `User-Agent` (AJU-04).
- [ ] Play Console: Data safety (PUB-10), cuenta de prueba para la revisión (ACC-05) y Play App Signing (SEC-03).
