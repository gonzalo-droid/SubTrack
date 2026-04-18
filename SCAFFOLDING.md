# SCAFFOLDING.md — SubTrack

Estructura inicial del proyecto: paquetes, navegación, pantallas vacías y dependencias. Este archivo define el **esqueleto** antes de implementar features reales.

> **Versiones actualizadas · abril 2026** — AGP 9.1.1, Kotlin 2.2.10, Compose BOM 2026.03.00, Hilt 2.57.1

## Objetivo de esta fase

Tener el proyecto con:
- ✅ Todas las carpetas creadas
- ✅ Design system (tokens + componentes base) en código
- ✅ Navegación type-safe configurada
- ✅ Todas las pantallas declaradas (aunque sean vacías con un `Text("Dashboard")`)
- ✅ Hilt configurado
- ✅ Mock repository estructurado (sin data todavía)

**Criterio de éxito**: poder navegar entre todas las pantallas de la app, aunque estén vacías.

## ⚠️ Consideraciones críticas · AGP 9.1.1

Al usar **AGP 9.1.1** (stable abril 2026), hay que tener en cuenta:

- **Gradle 9.1+ obligatorio** (se recomienda Gradle 9.4.1+)
- **JDK 17 mínimo** para compilar
- **Kotlin 2.2.10** viene bundled con AGP 9.1.1 (aún así lo declaramos explícitamente para control de versión)
- **KSP debe coincidir con Kotlin**: si Kotlin es `2.2.10`, KSP es `2.2.10-2.0.2`
- **Hilt 2.57.1+** es obligatorio (Hilt Gradle Plugin requiere AGP 9.0+ desde 2.57.0)
- **Kotlin Compose Compiler plugin** reemplaza a `kotlinCompilerExtensionVersion` (desde Kotlin 2.0, ahora standard en 2.2)
- **Built-in Kotlin en AGP 9**: AGP 9 activa Kotlin built-in por defecto; mantenemos el plugin explícito para claridad
- **Max compileSdk/targetSdk**: API 37 (Android 15+)
- **Play Store 2026 requirement**: targetSdk 35+ obligatorio

## Dependencias · `gradle/libs.versions.toml`

```toml
[versions]
# Core toolchain
agp = "9.1.1"
kotlin = "2.2.10"
ksp = "2.2.10-2.0.2"

# AndroidX Core
coreKtx = "1.15.0"
lifecycleKtx = "2.8.7"
activityCompose = "1.9.3"

# Compose (BOM maneja versiones individuales)
composeBom = "2026.03.00"

# Navigation type-safe (Kotlin Serialization)
navigationCompose = "2.8.5"
kotlinxSerialization = "1.7.3"

# DI
hilt = "2.57.1"
hiltNavigationCompose = "1.2.0"

# Async
coroutines = "1.9.0"

# Image loading
coil = "2.7.0"

[libraries]
# Core AndroidX
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleKtx" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }

# Compose BOM (versiones gestionadas por el BOM, sin version.ref)
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-text-google-fonts = { group = "androidx.compose.ui", name = "ui-text-google-fonts" }
androidx-compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }

# Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
androidx-hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

# Coroutines
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }

# Image loading
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

## `gradle/wrapper/gradle-wrapper.properties`

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-9.4.1-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

## `build.gradle.kts` (project-level · root)

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
```

## `app/build.gradle.kts` (módulo)

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.gondroid.subtrack"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.gondroid.subtrack"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Image loading
    implementation(libs.coil.compose)
}
```

## Estructura completa de paquetes

```
app/src/main/java/com/gondroid/subtrack/
│
├── SubTrackApplication.kt              // @HiltAndroidApp
├── MainActivity.kt                      // Single activity con NavHost
│
├── core/
│   ├── designsystem/
│   │   ├── theme/
│   │   │   ├── Color.kt                // Todos los tokens de color
│   │   │   ├── Typography.kt           // Escala tipográfica (Downloadable Fonts)
│   │   │   ├── Shape.kt                // Radius tokens
│   │   │   ├── Spacing.kt              // Spacing tokens
│   │   │   ├── Elevation.kt            // Elevation tokens
│   │   │   └── Theme.kt                // SubTrackTheme composable
│   │   └── components/
│   │       ├── buttons/
│   │       │   ├── PrimaryButton.kt
│   │       │   ├── SecondaryButton.kt
│   │       │   └── IconButton.kt
│   │       ├── cards/
│   │       │   ├── SurfaceCard.kt
│   │       │   └── HeroCard.kt
│   │       ├── text/
│   │       │   ├── Eyebrow.kt
│   │       │   ├── AmountDisplay.kt
│   │       │   ├── StatusPill.kt
│   │       │   └── Badge.kt
│   │       ├── avatar/
│   │       │   ├── Avatar.kt
│   │       │   ├── AvatarStack.kt
│   │       │   └── ServiceLogo.kt
│   │       ├── input/
│   │       │   ├── STTextField.kt
│   │       │   ├── SegmentedSelector.kt
│   │       │   └── ToggleSwitch.kt
│   │       ├── indicators/
│   │       │   ├── ProgressDots.kt
│   │       │   └── ProgressBar.kt
│   │       └── layout/
│   │           ├── STScaffold.kt
│   │           └── STTopBar.kt
│   │
│   ├── navigation/
│   │   ├── Routes.kt                   // Sealed interface con destinos type-safe
│   │   ├── SubTrackNavHost.kt          // NavHost principal
│   │   └── BottomNavBar.kt             // Tab bar de 4 ítems
│   │
│   ├── util/
│   │   ├── Money.kt                    // Extension para formatear montos
│   │   ├── Date.kt                     // Helpers de fecha
│   │   └── Result.kt
│   │
│   └── di/
│       └── RepositoryModule.kt         // Binds Mock en fase 1
│
├── data/
│   ├── mock/
│   │   ├── MockSubscriptionRepository.kt
│   │   ├── MockMemberRepository.kt
│   │   ├── MockUserRepository.kt
│   │   └── MockData.kt                 // Data hardcodeada
│   └── (remote/ y local/ vacíos por ahora)
│
├── domain/
│   ├── model/
│   │   ├── Subscription.kt
│   │   ├── Member.kt
│   │   ├── Payment.kt
│   │   ├── User.kt
│   │   ├── Template.kt
│   │   └── enums/
│   │       ├── SubscriptionCategory.kt
│   │       ├── PaymentStatus.kt
│   │       ├── BillingCycle.kt
│   │       └── SplitType.kt
│   ├── repository/
│   │   ├── SubscriptionRepository.kt
│   │   ├── MemberRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── subscription/
│       │   ├── GetSubscriptionsUseCase.kt
│       │   ├── GetSubscriptionDetailUseCase.kt
│       │   ├── CreateSubscriptionUseCase.kt
│       │   └── DeleteSubscriptionUseCase.kt
│       ├── member/
│       │   ├── AddMemberUseCase.kt
│       │   ├── UpdateMemberUseCase.kt
│       │   ├── ArchiveMemberUseCase.kt
│       │   └── RequestExitUseCase.kt
│       └── user/
│           └── GetUserProfileUseCase.kt
│
└── feature/
    ├── onboarding/
    │   ├── OnboardingScreen.kt
    │   ├── OnboardingViewModel.kt
    │   └── components/
    │
    ├── auth/
    │   ├── AuthScreen.kt
    │   └── AuthViewModel.kt
    │
    ├── dashboard/
    │   ├── DashboardScreen.kt
    │   ├── DashboardViewModel.kt
    │   ├── DashboardUiState.kt
    │   └── components/
    │
    ├── subscriptionlist/
    │   ├── SubscriptionListScreen.kt
    │   └── SubscriptionListViewModel.kt
    │
    ├── subscriptiondetail/
    │   ├── admin/
    │   │   ├── AdminDetailScreen.kt
    │   │   └── AdminDetailViewModel.kt
    │   └── member/
    │       ├── MemberDetailScreen.kt
    │       └── MemberDetailViewModel.kt
    │
    ├── createsubscription/
    │   ├── CreateSubscriptionScreen.kt
    │   ├── CreateSubscriptionViewModel.kt
    │   └── steps/
    │       ├── ServiceStep.kt
    │       ├── DetailsStep.kt
    │       ├── MembersStep.kt
    │       └── SplitStep.kt
    │
    ├── members/
    │   ├── MembersViewModel.kt
    │   └── sheets/
    │       ├── AddMemberSheet.kt
    │       ├── EditMemberSheet.kt
    │       ├── RemoveMemberSheet.kt
    │       └── ExitRequestSheet.kt
    │
    ├── people/
    │   ├── PeopleScreen.kt
    │   └── PeopleViewModel.kt
    │
    └── profile/
        ├── ProfileScreen.kt
        ├── ProfileViewModel.kt
        ├── templates/
        │   ├── TemplatesScreen.kt
        │   └── EditTemplateScreen.kt
        └── referral/
            └── ReferralScreen.kt
```

## Navegación type-safe — `Routes.kt`

Con Navigation Compose 2.8+ y Kotlin Serialization, las rutas son objetos tipados:

```kotlin
package com.gondroid.subtrack.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    // Onboarding
    @Serializable data object Onboarding : Route
    @Serializable data object Auth : Route

    // Main (bottom nav)
    @Serializable data object Dashboard : Route
    @Serializable data object SubscriptionList : Route
    @Serializable data object People : Route
    @Serializable data object Profile : Route

    // Subscription flows
    @Serializable data class SubscriptionDetail(val id: String) : Route
    @Serializable data object CreateSubscription : Route

    // Profile sub-screens
    @Serializable data object Templates : Route
    @Serializable data class EditTemplate(val id: String? = null) : Route // null = new
    @Serializable data object Referral : Route
}
```

## NavHost principal

```kotlin
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun SubTrackNavHost(
    navController: NavHostController,
    startDestination: Route = Route.Onboarding
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Route.Onboarding> { OnboardingScreen(...) }
        composable<Route.Auth> { AuthScreen(...) }
        composable<Route.Dashboard> { DashboardScreen(...) }
        composable<Route.SubscriptionList> { SubscriptionListScreen(...) }
        composable<Route.People> { PeopleScreen(...) }
        composable<Route.Profile> { ProfileScreen(...) }
        composable<Route.SubscriptionDetail> { entry ->
            val args = entry.toRoute<Route.SubscriptionDetail>()
            // Decide si admin o member según el user
        }
        composable<Route.CreateSubscription> { CreateSubscriptionScreen(...) }
        composable<Route.Templates> { TemplatesScreen(...) }
        composable<Route.EditTemplate> { entry ->
            val args = entry.toRoute<Route.EditTemplate>()
            EditTemplateScreen(templateId = args.id, ...)
        }
        composable<Route.Referral> { ReferralScreen(...) }
    }
}
```

## Modelos de dominio (snapshot inicial)

```kotlin
// Subscription.kt
data class Subscription(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val brandColor: String, // hex
    val totalAmount: Double,
    val currency: String = "PEN",
    val cycle: BillingCycle,
    val cutoffDay: Int, // 1-31
    val ownerId: String,
    val isShared: Boolean,
    val category: SubscriptionCategory,
    val members: List<Member> = emptyList(),
    val archivedMembers: List<Member> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long
)

// Member.kt
data class Member(
    val id: String,
    val userId: String?, // null si no tiene la app
    val name: String,
    val phone: String,
    val profileLabel: String?, // "Hermana", "Perfil 2"
    val shareAmount: Double,
    val isArchived: Boolean = false,
    val currentStatus: PaymentStatus,
    val joinedAt: Long
)

// Payment.kt
data class Payment(
    val id: String,
    val subscriptionId: String,
    val memberId: String,
    val monthKey: String, // "2026-04"
    val amount: Double,
    val status: PaymentStatus,
    val paidAt: Long?,
    val proofUrl: String?,
    val note: String?
)

// Enums
enum class BillingCycle { MONTHLY, YEARLY, CUSTOM }
enum class PaymentStatus { PAID, PENDING, OVERDUE, LATE }
enum class SplitType { EQUAL, PERCENTAGE, FIXED }
enum class SubscriptionCategory { STREAMING, MUSIC, PRODUCTIVITY, CLOUD, AI, GAMING, NEWS, OTHER }
```

## Fuentes con Downloadable Fonts (Google Fonts)

En vez de descargar los `.ttf` manualmente e incluirlos en el APK, usamos Google Fonts Downloadable Fonts:
- **Ventaja**: no aumenta el tamaño del APK
- **Primera carga**: se descargan vía Google Play Services (cacheadas después)
- **Fallback**: SansSerif / Monospace si no hay internet la primera vez

```kotlin
// core/designsystem/theme/Typography.kt
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font as GoogleFontFont
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

val googleFontsProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val SpaceGrotesk = FontFamily(
    GoogleFontFont(GoogleFont("Space Grotesk"), googleFontsProvider, FontWeight.Normal),
    GoogleFontFont(GoogleFont("Space Grotesk"), googleFontsProvider, FontWeight.Medium),
    GoogleFontFont(GoogleFont("Space Grotesk"), googleFontsProvider, FontWeight.SemiBold),
    GoogleFontFont(GoogleFont("Space Grotesk"), googleFontsProvider, FontWeight.Bold)
)

val Geist = FontFamily(
    GoogleFontFont(GoogleFont("Geist"), googleFontsProvider, FontWeight.Normal),
    GoogleFontFont(GoogleFont("Geist"), googleFontsProvider, FontWeight.Medium),
    GoogleFontFont(GoogleFont("Geist"), googleFontsProvider, FontWeight.SemiBold),
    GoogleFontFont(GoogleFont("Geist"), googleFontsProvider, FontWeight.Bold)
)

val JetBrainsMono = FontFamily(
    GoogleFontFont(GoogleFont("JetBrains Mono"), googleFontsProvider, FontWeight.Normal),
    GoogleFontFont(GoogleFont("JetBrains Mono"), googleFontsProvider, FontWeight.Medium),
    GoogleFontFont(GoogleFont("JetBrains Mono"), googleFontsProvider, FontWeight.SemiBold)
)
```

Requiere agregar en `res/values/font_certs.xml` los certificados de Google Play Services Fonts (Claude Code lo genera). También declarar el provider en `AndroidManifest.xml`:

```xml
<meta-data
    android:name="preloaded_fonts"
    android:resource="@array/preloaded_fonts" />
```

## Plan de implementación — orden sugerido

### Milestone 1: Fundamentos (día 1-2)
1. Actualizar `gradle/libs.versions.toml` con las versiones mostradas arriba
2. Actualizar `build.gradle.kts` (project-level y app) con plugins y JDK 17
3. Actualizar `gradle-wrapper.properties` a Gradle 9.4.1
4. Configurar Hilt (`@HiltAndroidApp` en `SubTrackApplication`, `@AndroidEntryPoint` en `MainActivity`)
5. Agregar `android:name=".SubTrackApplication"` al `AndroidManifest.xml`
6. Crear carpetas de paquetes vacías
7. Configurar Downloadable Fonts (Google Fonts Provider + `font_certs.xml`)
8. Implementar `core/designsystem/theme/` completo (Color, Typography, Shape, Spacing, Theme)

### Milestone 2: Componentes base (día 2-3)
9. Implementar componentes atómicos en `core/designsystem/components/`:
    - Buttons (Primary, Secondary, Icon)
    - Cards (Surface, Hero)
    - Text (Eyebrow, AmountDisplay, StatusPill, Badge)
    - Avatar, AvatarStack, ServiceLogo
    - Inputs (TextField, SegmentedSelector, ToggleSwitch)
    - Indicators (ProgressDots, ProgressBar)
10. Crear una pantalla **ComponentGalleryScreen** (solo para preview, no en nav) que muestre todos los componentes para validar visualmente

### Milestone 3: Navegación + pantallas vacías (día 3-4)
11. Crear `Routes.kt` con todas las rutas
12. Crear `SubTrackNavHost.kt` con todos los destinos (cada uno renderiza solo `Text("Nombre de pantalla")`)
13. Implementar `BottomNavBar` con 4 tabs
14. Hacer que se pueda navegar entre todas las pantallas

### Milestone 4: Data layer mockeada (día 4-5)
15. Definir modelos en `domain/model/`
16. Definir interfaces en `domain/repository/`
17. Implementar `MockData.kt` con 3-5 suscripciones hardcodeadas (2 personales + 2 compartidas + 1 con archivados)
18. Implementar `MockSubscriptionRepository`, `MockMemberRepository`, `MockUserRepository`
19. Configurar `RepositoryModule` para bindear mocks

### Milestone 5: Primera pantalla real — Dashboard (día 5-7)
20. Use cases: `GetSubscriptionsUseCase`, `GetUserProfileUseCase`
21. `DashboardViewModel` + `DashboardUiState`
22. `DashboardScreen` completo con componentes del design system
23. Matching visual con `01_dashboard_dual.html`

## Archivos iniciales críticos que Claude Code debe generar primero

Cuando Claude Code arranque, debería generar en este orden:

1. `gradle/libs.versions.toml` actualizado
2. `gradle/wrapper/gradle-wrapper.properties` → Gradle 9.4.1
3. `build.gradle.kts` (project-level) con plugins
4. `app/build.gradle.kts` con JDK 17, compileSdk 37, minSdk 24
5. `SubTrackApplication.kt` con `@HiltAndroidApp`
6. `MainActivity.kt` con `@AndroidEntryPoint` y setup Compose
7. `AndroidManifest.xml` con `android:name=".SubTrackApplication"` y `preloaded_fonts` meta-data
8. `res/values/font_certs.xml` (certificados Google Play Services Fonts)
9. `res/values/preloaded_fonts.xml` (opcional, para preload)
10. `core/designsystem/theme/Color.kt` (todos los tokens)
11. `core/designsystem/theme/Typography.kt` (con Downloadable Fonts)
12. `core/designsystem/theme/Spacing.kt`, `Shape.kt`, `Elevation.kt`
13. `core/designsystem/theme/Theme.kt` con `SubTrackTheme` composable
14. `core/navigation/Routes.kt`
15. `core/navigation/SubTrackNavHost.kt` con todas las rutas (pantallas vacías)
16. `core/navigation/BottomNavBar.kt`
17. `core/di/RepositoryModule.kt` (vacío, preparado)
18. Archivos stub de cada pantalla con solo `Text("Screen X")`

Después de eso: componentes base uno por uno, y finalmente la primera pantalla real.

## Validación

Al final de cada milestone:
- ✅ El proyecto compila sin warnings
- ✅ La app abre sin crashear
- ✅ Las previews de Compose funcionan
- ✅ Se puede navegar entre las pantallas (aunque estén vacías)

Si algo falla, **no seguir al siguiente milestone**. Estabilizar primero.

## Notas de compatibilidad

| Elemento | Versión / Valor | Comentario |
|---|---|---|
| `minSdk` | 24 | Android 7.0+ (Nougat) · cubre ~98% del mercado LATAM |
| `targetSdk` | 37 | Android 15+ · requisito Play Store 2026 |
| `compileSdk` | 37 | Máximo soportado por AGP 9.1.1 |
| JDK compile | 17 (LTS) | Mínimo para AGP 9 · JDK 21 también funciona |
| Gradle | 9.4.1 | Versión recomendada para AGP 9.1.1 |
| Kotlin | 2.2.10 | Bundled en AGP 9.1.1 |
| KSP | 2.2.10-2.0.2 | Debe coincidir con Kotlin version |
| Compose BOM | 2026.03.00 | Último stable (marzo 2026) |
| Hilt | 2.57.1 | Requiere AGP 9.0+ · soporta KSP |

## Recursos de referencia

- AGP 9.1.1 release notes: https://developer.android.com/build/releases/agp-9-1-0-release-notes
- Kotlin 2.2: https://kotlinlang.org/docs/whatsnew22.html
- Compose BOM mapping: https://developer.android.com/jetpack/compose/bom/bom-mapping
- Hilt docs: https://developer.android.com/training/dependency-injection/hilt-android
- Downloadable Fonts: https://developer.android.com/develop/ui/views/text-and-emoji/downloadable-fonts