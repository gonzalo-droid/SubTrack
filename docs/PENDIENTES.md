# Pendientes

Tareas abiertas de SubTrack, ordenadas por prioridad. Los IDs (`ACC-02`, `PRIV-01`…) son reglas de la base KMP;
el detalle y la evidencia están en [`auditorias/2026-11-26-base-kmp.md`](auditorias/2026-11-26-base-kmp.md).
Al cerrar una tarea, bórrala de aquí y anota la decisión en `DECISIONES.md` si hubo que elegir algo.

## Alta: antes del primer build para tienda

- [ ] **ACC-02** — Cerrar sesión de verdad (`FirebaseAuth.signOut`, limpiar `AppStore` y Room; hoy el diálogo de `ProfileScreen.kt:124-140` no hace nada) y borrar la cuenta desde la app: Auth, `users/{uid}` en Firestore y datos locales. Página web para pedir el borrado (Data safety).
- [ ] **PRIV-03 / PUB-15 / AJU-01** — Escribir y publicar la política de privacidad y los términos (cuenta de Google, teléfonos de los miembros, Firestore). Enlazarlos en Ajustes y en el texto del onboarding.
- [ ] **SEC-03 / SEC-04 / PUB-09** — Keystore de subida fuera del repo (`~/.config/subtrack-signing/`), `signingConfig` de release leído de `local.properties`, Play App Signing activado y el respaldo anotado (dónde están la keystore, `google-services.json` y las credenciales; nunca las llaves).
- [ ] **PRIV-01** — Decidir qué entra en el respaldo de Android y escribir `data_extraction_rules.xml` / `backup_rules.xml` (hoy son la plantilla comentada).
- [ ] **UIS-02 / ARQ-03** — No mostrar el texto de excepciones al usuario (`AdminDetailScreen.kt:145`, `MemberDetailViewModel`, `AdminDetailViewModel`). Errores tipados con mensajes de `strings.xml`.
- [ ] **Login real** — Probar Google + Firestore con una cuenta real en un dispositivo: el usuario aparece en Authentication y sus datos en `users/{uid}`.

## Media

- [ ] **ACC-04** — Permitir usar la app sin cuenta (los datos ya son locales), o anotar en `DECISIONES.md` por qué se exige.
- [ ] **AJU-01 / AJU-02 / AJU-03 / AJU-05** — Ajustes: licencias, "Compartir la app" (hoja del sistema), "Valorar la app" (`market://details?id=com.gondroid.subtrack`) y contacto con destinatario + versión, modelo y Android en el cuerpo.
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

- **Pro (pagos):** PAY-01 (Play Billing + RevenueCat), PAY-03 (preguntar por el derecho `premium`; borrar `AppStore.isProUser`, que no se usa), PAY-04 ("Restaurar compras"), PAY-05 (sandbox), PAY-06 (privacidad).
- **Recordatorios (push):** PUSH-01 (FCM), PUSH-03 (canales con nombres claros), PUSH-04 (tocar la notificación abre la pantalla correcta).
- **Referidos (deep links):** DL-01 (App Links verificados con `assetlinks.json` en `subtrack.app`), DL-02 (URL → ruta testeada; una URL desconocida abre Inicio).

## Pruebas en dispositivo o consola (una sola sesión)

- [ ] Release con R8 en claro, oscuro, letra 2× y modo avión (TST-09, PUB-02, TRP-01).
- [ ] TalkBack por el flujo principal y zonas táctiles de 48 dp (A11Y-03, A11Y-04).
- [ ] Insets con gestos y con 3 botones (TIE-03); tablet ≥ 600 dp en horizontal (TIE-04).
- [ ] Negar notificaciones y seguir usando la app (PERM-02).
- [ ] Confirmar quién controla `subtrack.app` y si `/download` redirige por `User-Agent` (AJU-04).
- [ ] Play Console: Data safety (PUB-10), cuenta de prueba para la revisión (ACC-05) y Play App Signing (SEC-03).
