# DESIGN_TOKENS.md — SubTrack

Sistema de diseño de la app. **Fuente de verdad única**: todo color, tipografía, espaciado y componente se define aquí y se implementa en `core/designsystem/`.

## Filosofía visual

- **Dark-first premium**: negros reales (#050506), no grises. Estética tipo Apple Card.
- **Números como protagonistas**: los montos son el contenido principal.
- **Mínimo chrome**: sin bordes pesados, sin sombras fuertes. Profundidad por jerarquía y color.
- **Moderno e interactivo**: microinteracciones sutiles (spring, shimmer, pulse).

## Colores

### Base (backgrounds)

| Token | Hex | Uso |
|---|---|---|
| `bg_page` | `#050506` | Fondo externo (rara vez visible) |
| `bg_app` | `#0A0A0C` | Fondo principal de la app |
| `bg_surface` | `#131316` | Cards, rows, tiles |
| `bg_surface_hi` | `#1A1A1E` | Hover/pressed de surfaces |
| `bg_surface_el` | `#242428` | Elementos elevados (ej. segmented selector activo) |
| `bg_sheet` | `#18181C` | Bottom sheets |

### Bordes

| Token | Valor | Uso |
|---|---|---|
| `border_default` | `rgba(255,255,255,0.06)` | Borde sutil en cards |
| `border_strong` | `rgba(255,255,255,0.12)` | Borde en hover/focus |

### Texto

| Token | Hex | Uso |
|---|---|---|
| `text_primary` | `#F5F5F7` | Texto principal |
| `text_secondary` | `#9A9AA3` | Texto descriptivo |
| `text_tertiary` | `#5A5A63` | Labels, metadatos, captions |

### Acentos (estados y color semántico)

| Token | Hex | Significado |
|---|---|---|
| `accent_green` | `#32D74B` | Éxito, pagado, admin, CTA positivo |
| `accent_amber` | `#FFB340` | Atención, pendiente, Pro, advertencia |
| `accent_red` | `#FF453A` | Error, vencido, destructivo |
| `accent_blue` | `#0A84FF` | Links, info, miembro, neutral |
| `accent_purple` | `#BF5AF2` | Secondary, personal |
| `accent_pink` | `#FF375F` | Alertas especiales |
| `accent_cyan` | `#64D2FF` | Decorativo |

### Fondos tintados (para secciones/tarjetas de color)

Cada acento tiene su variante tintada (con 12% alpha aprox) para fondos de cards especiales:

| Token | Uso |
|---|---|
| `accent_green_bg` | `rgba(50, 215, 75, 0.12)` — fondo de tags "Pagado", "Admin" |
| `accent_amber_bg` | `rgba(255, 179, 64, 0.12)` — fondo de tags "Pendiente", "Pro" |
| `accent_red_bg` | `rgba(255, 69, 58, 0.12)` — fondo de tags "Vencido" |
| `accent_blue_bg` | `rgba(10, 132, 255, 0.12)` — fondo de tags "Miembro", insights |
| `accent_purple_bg` | `rgba(191, 90, 242, 0.12)` |

## Tipografía

### Familias

- **Display** (títulos, números grandes): **Space Grotesk**
- **Body** (texto regular): **Geist** (fallback a `SansSerif`)
- **Mono** (labels técnicos, eyebrows, montos compactos): **JetBrains Mono** (fallback a `Monospace`)

Las fuentes se cargan vía `res/font/` (descargar de Google Fonts).

### Escala tipográfica

| Token | Family | Size | Weight | Letter Spacing | Uso |
|---|---|---|---|---|---|
| `displayXL` | Space Grotesk | 56sp | 700 | -0.04em | Hero amounts (onboarding, success) |
| `displayL` | Space Grotesk | 42sp | 700 | -0.04em | Hero amounts (dashboard) |
| `displayM` | Space Grotesk | 32sp | 700 | -0.03em | Section heroes (wizard titles) |
| `displayS` | Space Grotesk | 26sp | 700 | -0.03em | Screen titles |
| `headlineL` | Space Grotesk | 22sp | 700 | -0.02em | Page titles, hero secundarios |
| `headlineM` | Space Grotesk | 18sp | 700 | -0.02em | Section titles |
| `headlineS` | Space Grotesk | 15sp | 700 | -0.02em | Card titles, sheet titles |
| `titleL` | Space Grotesk | 13sp | 600 | -0.01em | Row names, list items |
| `titleM` | Geist | 13sp | 500 | -0.01em | Settings titles |
| `bodyL` | Geist | 14sp | 400 | -0.01em | Body text principal |
| `bodyM` | Geist | 13sp | 400 | -0.01em | Body text secundario |
| `bodyS` | Geist | 12sp | 400 | -0.01em | Captions, subtítulos |
| `bodyXS` | Geist | 11sp | 400 | 0 | Metadata inline |
| `mono` | JetBrains Mono | 11sp | 500 | 0.02em | Montos, versiones, IDs |
| `monoS` | JetBrains Mono | 10sp | 600 | 0.15em | Eyebrows, labels (UPPERCASE) |
| `monoXS` | JetBrains Mono | 9sp | 600 | 0.18em | Tags, status pills (UPPERCASE) |

## Espaciado (8pt base, múltiplos de 4)

```kotlin
object Spacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 12.dp
    val base = 16.dp
    val l = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val huge = 48.dp
}
```

**Padding típico de pantallas**: `20.dp` horizontal, `16.dp` vertical.
**Gap entre secciones**: `24.dp`.
**Gap entre items de lista**: `6-8.dp`.

## Radius

```kotlin
object Radius {
    val xs = 6.dp      // Chips, tags
    val s = 8.dp       // Pills pequeñas
    val m = 10.dp      // Buttons pequeños
    val base = 12.dp   // Inputs, rows
    val l = 14.dp      // Cards pequeñas
    val xl = 18.dp     // Cards medianas
    val xxl = 24.dp    // Hero cards, sheets
    val huge = 32.dp   // Screen corners (phone mockup)
    val circle = 999.dp // Pills circulares (tags)
}
```

## Elevación (sombras)

La app es dark premium: **usar sombras con mucha mesura**. Preferir jerarquía por color/brillo antes que por sombra.

```kotlin
object Elevation {
    val none = 0.dp
    val subtle = 2.dp       // Avatars, pills elevados
    val card = 8.dp         // Cards hover
    val sheet = 20.dp       // Bottom sheets
    val modal = 40.dp       // Modales importantes
}
```

## Iconografía

- **Librería**: `androidx.compose.material.icons.Outlined` + `Rounded` para tabs activos
- **Tamaños**:
    - `icon_xs` = 12.dp (inline en texto)
    - `icon_s` = 14.dp (botones pequeños)
    - `icon_base` = 16.dp (tab bar, row indicators)
    - `icon_m` = 20.dp (headers)
    - `icon_l` = 24.dp (primary actions)
    - `icon_xl` = 32.dp (hero icons)

## Componentes base a implementar

Estos son los átomos del design system. Se implementan **primero** en `core/designsystem/components/`.

### Botones

1. **`PrimaryButton`** — Fondo `text_primary` (blanco), texto negro. Variante `.accent(color)` para verde/rojo/etc.
2. **`SecondaryButton`** — Fondo `bg_surface`, borde sutil, texto blanco.
3. **`IconButton`** — Cuadrado 36.dp, ícono centrado.
4. **`FloatingAction`** — Pill grande con ícono a la derecha (cobranza bar).

### Cards

1. **`SurfaceCard`** — base para todo contenedor. `bg_surface` + `border_default` + radius `l`.
2. **`HeroCard`** — con gradiente radial sutil, para headers destacados.
3. **`StatusCard`** — con tinte de color (green/amber/red/blue) para mensajes contextuales.

### Elementos de texto

1. **`Eyebrow`** — monoXS uppercase con letter-spacing ancho.
2. **`AmountDisplay`** — display tipográfico de montos con currency prefix pequeño.
3. **`StatusPill`** — chip de estado (Pagado / Pendiente / Vencido) con color semántico.
4. **`Badge`** — tag pequeño (PRO, ADMIN, NUEVO).

### Avatares

1. **`Avatar`** — círculo con iniciales, gradiente de color por hash del nombre.
2. **`AvatarStack`** — pila de 3-4 avatares con solapamiento.
3. **`ServiceLogo`** — logo de suscripción con color de marca.

### Inputs

1. **`TextField`** — input con label arriba y placeholder.
2. **`SegmentedSelector`** — selector tipo iOS con fondo deslizante.
3. **`ToggleSwitch`** — estilo iOS 22.dp alto con spring animation.

### Indicadores

1. **`ProgressDots`** — dots para wizard/onboarding, el activo se expande.
2. **`ProgressBar`** — barra lineal con shimmer.
3. **`LoadingSpinner`** — spinner circular con gradient.

## Motion / Animaciones

### Curvas estándar

```kotlin
object Easing {
    val standard = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
    val spring = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    val emphasized = CubicBezierEasing(0.2f, 0f, 0f, 1f)
}
```

### Duraciones

- **micro** = 150ms (tap feedback, color change)
- **short** = 250ms (hover, state change)
- **medium** = 400ms (sheet appear, card expand)
- **long** = 600ms (page transition, complex animation)

### Motion patterns

1. **Sheet appear**: `slideInVertically(spring)` desde bottom, 500ms
2. **List item enter**: `fadeIn + slideInVertically` con stagger (delay 50ms por item)
3. **Pulse indicator**: `scale(1.0 → 1.05 → 1.0)` infinito, 2000ms
4. **Shimmer**: gradient translation de `-100%` a `100%`, 2500ms linear infinite
5. **Press state**: `scale(1.0 → 0.97)` 100ms, vuelve en 150ms

## Moneda y formatos

- **Default currency**: PEN (`S/`)
- **Formato**: `S/ 54.90` (con espacio entre símbolo y monto)
- **Helper**: `Double.asMoney()` extension retorna `String`
- **Fechas**: formato corto `17 abr`, largo `17 de abril, 2026`

## Localización

- **Default**: es-PE (español Perú)
- **Fallback**: en
- **Todo string** va en `strings.xml` con prefijo por feature:
    - `dashboard_*` para pantalla de inicio
    - `subscription_*` para detalles
    - `member_*` para gestión de miembros
    - `onboarding_*` para flow inicial
    - `common_*` para textos compartidos (botones, acciones comunes)

## Accesibilidad

- **Contraste mínimo**: WCAG AA (4.5:1 para texto normal, 3:1 para texto grande)
- **Touch target mínimo**: 44dp x 44dp
- **Content descriptions**: obligatorio en todo ícono sin texto
- **Semantics**: usar `Modifier.semantics {}` para estados custom (ej. tabs, toggles)

## Referencia: componentes que deben verse idénticos al mockup

Al implementar pantallas, **comparar con los mockups HTML** en `/mockups/`:

- `01_dashboard_dual.html` — Pantalla de inicio con dual hero
- `02_detail_admin.html` — Detalle de suscripción compartida (admin)
- `03_detail_member.html` — Vista del miembro no-admin
- `04_create_wizard.html` — Wizard de crear suscripción
- `05_members_sheets.html` — Bottom sheets de gestión de miembros
- `06_profile.html` — Perfil con alias, plantillas, referidos
- `07_onboarding.html` — Flujo de onboarding de 7 pantallas

Los mockups son la fuente de verdad visual. Si algo es ambiguo, preguntar antes de interpretar.