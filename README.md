# SubTrack

**Gestiona tus suscripciones compartidas y cobra vía WhatsApp — sin procesar pagos.**

SubTrack es una app Android para organizar suscripciones como Netflix, Spotify o YouTube Premium entre amigos y familia. El admin cobra a los miembros a través de WhatsApp; la app solo coordina, no mueve dinero.

> Mercado objetivo: LATAM (foco Perú · moneda S/ PEN)

---

## Características principales

| Feature | Estado |
|---|---|
| Onboarding de 7 pasos | ✅ |
| Dashboard con héroe dual (personal + compartidas) | ✅ |
| Lista de suscripciones con filtros | ✅ |
| Detalle de suscripción (vista admin y miembro) | ✅ |
| Wizard de creación multi-paso | ✅ |
| Gestión de personas/contactos | ✅ |
| Perfil + aliases + plantillas WhatsApp | ✅ |
| Programa de referidos (1 invitado activo = 1 mes Pro) | ✅ UI |
| Backend Firebase (Auth, Firestore, FCM) | 🔜 Fase 2 |
| Room para persistencia local | 🔜 Fase 2 |

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.2 |
| UI | Jetpack Compose + Material 3 |
| Arquitectura | Clean Architecture · MVVM |
| DI | Hilt 2.56 |
| Async | Coroutines + Flow |
| Navegación | Navigation Compose type-safe (`@Serializable`) |
| Fonts | Google Downloadable Fonts (Space Grotesk, Geist, JetBrains Mono) |
| QR | ZXing Core |
| Imágenes | Coil |
| Preferencias | DataStore |
| Backend (fase 2) | Firebase Auth · Firestore · Cloud Functions · FCM |
| Persistencia local (fase 2) | Room |
| minSdk | 24 |
| targetSdk / compileSdk | 37 |

---

## Cómo ejecutar el proyecto

```bash
# Clonar
git clone <repo-url>
cd SubTrack

# Abrir en Android Studio Ladybug o superior
# Seleccionar dispositivo o emulador API 24+
# Run → app
```

No se requiere configuración de Firebase para la Fase 1: todos los datos vienen de `MockRepository`.

---

## Estructura del proyecto

```
app/src/main/java/com/gondroid/subtrack/
├── core/
│   ├── designsystem/      # Tema, colores, tipografía, componentes reutilizables
│   ├── navigation/        # Routes, NavHost, BottomNavBar
│   ├── preferences/       # DataStore (onboarding completado, usuario)
│   ├── di/                # Módulos Hilt
│   └── util/              # Formatters, extensiones, generación QR
│
├── domain/
│   ├── model/             # Subscription, Member, Payment, User, …
│   ├── repository/        # Interfaces de repositorio (pure Kotlin)
│   └── usecase/           # Un caso de uso por archivo
│
├── data/
│   └── mock/              # MockRepository con datos hardcodeados (Fase 1)
│
└── feature/
    ├── onboarding/        # Flujo 7 pasos
    ├── auth/              # Pantalla de autenticación
    ├── dashboard/         # Héroe dual + insights + próximos pagos
    ├── subscriptionlist/  # Lista con filtros
    ├── subscriptiondetail/# Vista admin y miembro
    ├── createsubscription/# Wizard multi-paso
    ├── people/            # Contactos
    └── profile/           # Perfil, aliases, plantillas, referidos
```

Ver [ARCHITECTURE.md](ARCHITECTURE.md) para diagramas detallados de capas, navegación y flujo de datos.

---

## Modelo de negocio

- **Free**: hasta 2 suscripciones compartidas
- **Pro**: ilimitadas + estadísticas avanzadas
- **Referidos**: 1 invitado activo = 1 mes Pro gratis para ambos

---

## Design System

El sistema de diseño completo está documentado en [DESIGN_TOKENS.md](DESIGN_TOKENS.md):

- Paleta dark-first (negros verdaderos, no grises)
- Tipografía: Space Grotesk (display) · Geist (body) · JetBrains Mono (técnico)
- Grilla de 8pt · Radios definidos · Elevaciones semánticas
- Motion: spring curves + duraciones estandarizadas

---

## Roadmap de milestones

| Milestone | Descripción | Estado |
|---|---|---|
| M1 | Scaffolding · Design System · Navegación | ✅ |
| M2 | Todas las pantallas UI con MockData | ✅ |
| M3 | Firebase Auth + Firestore | 🔜 |
| M4 | Lógica de pagos + WhatsApp | 🔜 |
| M5 | Notificaciones FCM + Pro/Freemium | 🔜 |

---

## Licencia

Privado — todos los derechos reservados © 2024 Gonzalo Lozg
