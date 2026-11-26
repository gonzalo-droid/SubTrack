# PLAN — Propuesta UX/UI (D1 B · D2 B · D3 A · D4 A · D5 A · D6 A)

Rutas relativas a `app/src/main/java/com/gondroid/subtrack/` (`S/`). Cada tanda = commits atómicos, build + unit tests verdes y sin errores de lint nuevos antes de la siguiente.

## Baseline (2026-11-25, antes de tocar código)
- `assembleDebug` OK · `testDebugUnitTest` OK (solo `ExampleUnitTest`)
- `lintDebug` **ya rojo**: 13 errores `LocalContextGetResourceValueCall` (Toasts en AdminDetail, AliasEdit, EditProfile, EditTemplate, Referral, Templates) + 124 warnings
- Sin ktlint/detekt/Roborazzi/E2E. Gate adaptado: lint sin errores nuevos (meta: 13 → 0 tras Tanda 2), JUnit para lógica nueva, primer flujo Maestro en `.maestro/`.

## Tanda 1 — Arreglos sin decisión
- `AndroidManifest.xml`: `POST_NOTIFICATIONS`.
- `strings.xml`: comillas en valores con espacio al borde (líneas 39, 40, 421, 423, 427, 429, 436, 462); la 462 pasa a `buildAnnotatedString` en `FirstSubscriptionScreen.kt:101`.
- `<plurals>` + `pluralStringResource`: servicios/deben, días de corte, meses, personas (bulk), pendientes, perfiles, pagos a tiempo; `ReferralScreen.kt:581` ("Hace N días").
- Nuevo `S/domain/util/CutoffDates.kt` (puro, `Calendar` — minSdk 24 sin desugaring): `nextCutoff`, `daysUntil`; recorta al último día del mes y cruza de año. Reemplaza la lógica duplicada en `MockSubscriptionRepository.kt:26` y `DashboardScreen.kt:836` y corrige `CutoffPill` (`AdminDetailScreen.kt:448`); meses a `string-array`.
- Salidas duplicadas: quitar link "Ir al inicio" de `OnboardingSuccessScreen.kt:139`; texto correcto en éxito del asistente ("Listo" en vez de "Ir al inicio" cuando vuelve atrás).
- **Aceptación**: "3 servicios · 2 deben" / "1 servicio · 1 debe"; corte día 5 visto el 25-nov → "5 dic"; día 31 en nov → 30-nov; dic→ene OK. Tests JUnit de `CutoffDates`.

## Tanda 2 — Contraste y accesibilidad
- `text_tertiary` → `#8A8A93` en `core/designsystem/theme/Color.kt:20` y `DESIGN_TOKENS.md:38`.
- 48 dp: `STIconButton` con `minimumInteractiveComponentSize()`, pill 28 dp de AdminDetail, tabs de Suscripciones, links "Saltar".
- TalkBack: `contentDescription` en iconos accionables (X del asistente primero); filas con `semantics(mergeDescendants = true)` + `Role.Button`.
- 15 Toast → Snackbar: `SnackbarHost` en el `Scaffold` de `MainActivity.kt:80` vía `LocalSnackbarHostState`; `WhatsAppHelper` devuelve resultado y textos a strings.xml. Snackbar con tokens existentes (`bg_surface_el`, `text_primary`), sin tokens nuevos.
- **Aceptación**: lint 13 → 0 errores; ningún `Toast` en el código; ningún icono accionable sin descripción.

## Tanda 3 — Asistente de creación
- `WizardStep`: SERVICE → WHO_PAYS → DETAILS → (MEMBERS → SPLIT). Personal 3 pasos, compartida 5. Paso de pasos como función pura testeable.
- Nuevo `steps/WhoPaysStep.kt` ("Solo yo" / "La comparto"); quitar toggle de `MembersStep`.
- `DetailsStep`: grilla 7 columnas (`chunked(7)`, celdas 48 dp alto) + "Próximo: 15 dic".
- `startWithShared=true` realmente preselecciona compartida; `popUpTo<Route.CreateSubscription>` en `SubTrackNavHost`.
- **Aceptación**: personal 1/3→3/3 crea; compartida 1/5→5/5 crea; atrás desde DETAILS vuelve a WHO_PAYS; flujo Maestro `create_shared.yaml`.

## Tanda 4 — Detalle admin, onboarding, dashboard
- Menú ⋮ (`DropdownMenu`): Eliminar con confirmación vía `DeleteSubscriptionUseCase` (ya existe) + evento `Deleted`; Editar/Pausar según decisiones abajo.
- `CobranzaBar`: "Cobrar a Ana" con 1 pendiente, plural en los demás.
- Onboarding: "Agregar otra" y "Compartir" abren el asistente (false/true) encima del Dashboard vía `AppStateViewModel`; `OnboardingFlow` deja de leer `MockDataStore` (use case).
- D2: el permiso sale del inicio del onboarding y se pide justo tras crear la primera suscripción (tanto en el onboarding como en el asistente), controlado por `UserPreferences.notificationPermissionRequested`; `onGrant` solo si se concede.
- Dashboard con `fontScale >= 1.3`: héroes en columna y total en dos líneas.

## Tanda 5 — Personas y FAB
- People: título, `SegmentedSelector` "Te deben / Debes", excluir al usuario actual; nuevo `GetMyDebtsUseCase` (con test JUnit y repo falso).
- FAB en Dashboard (Suscripciones ya lo tiene).

## Flujos que pueden romperse
Onboarding completo · crear suscripción personal · detalle admin (cobro WhatsApp, bulk) · detalle miembro · Personas · plantillas/alias/referidos (Toast→Snackbar).

## Decisiones (Gonzalo, 2026-11-25)
1. Familiar → campo `ServiceTemplate.suggestsShared`, marcado en el mock.
2. Editar → reusar el asistente con ruta `EditSubscription(id)` + `UpdateSubscriptionUseCase` (Tanda 4).
3. Pausar → incluir: `Subscription.isPaused`, `PauseSubscriptionUseCase`, chip "Pausada", fuera de totales y cortes (Tanda 4).
4. Bugs extra incluidos:
   - Tanda 1: `NotificationsPermissionScreen` llama `onGrant` aunque se niegue; bulk "0 personas" (`AdminDetailScreen.kt:110`); `adminName = ownerId` (`AdminDetailScreen.kt:180`); `ReferralScreen.kt:135` WhatsApp con número vacío; `MemberDetailScreen.kt:112` `onBack()` en composición.
   - Tanda 5: definición única de deuda (OVERDUE + LATE + PENDING vencido) y `GetDebtorsUseCase` filtrando `ownerId == yo`.
