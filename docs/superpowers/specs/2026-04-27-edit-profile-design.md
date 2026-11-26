# Edit Profile Flow — Design Spec

**Fecha:** 2026-04-27
**Fase:** 1 (mock, sin backend)
**Feature:** Editar perfil de usuario desde ProfileScreen

---

## Resumen

Pantalla completa (`EditProfileScreen`) que permite al usuario editar su nombre, teléfono y foto de perfil. El email es solo lectura porque proviene del proveedor de autenticación (Google o Apple). Se accede tocando el botón "Editar" en el hero de `ProfileScreen`.

---

## Campos

| Campo | Editable | Detalle |
|---|---|---|
| Foto de perfil | Sí | Android photo picker (PickVisualMedia). URI local, sin upload en fase 1. |
| Nombre | Sí | Texto libre, obligatorio, no puede estar vacío. |
| Teléfono | Sí | Teclado numérico. Formato libre, sin validación estricta en fase 1. |
| Email | No | Solo lectura. Badge con ícono candado + nombre del proveedor (Google / Apple). Texto de ayuda: "Gestionado por tu cuenta de Google/Apple". |

---

## UX / Navegación

- Entrada: botón "Editar" en hero de `ProfileScreen` → navega a `Route.EditUser`
- Salida exitosa: toast "Perfil actualizado" → `popBackStack()`
- Salida cancelada: flecha atrás → `popBackStack()` sin guardar
- `ProfileScreen` se actualiza automáticamente al volver porque `ProfileViewModel` y `EditProfileViewModel` colectan del mismo `MutableStateFlow<User>` en `MockDataStore`

### Top bar
- Izquierda: botón `STIconButton` con `ArrowBack` → `onBack()`
- Centro: título "Editar perfil" (`headlineL`, `TextPrimary`)
- Derecha: texto "Guardar" (`AccentGreen`, `bodyS bold`) → llama a `save()`

---

## Diseño visual

Pantalla de scroll vertical (`Column` + `verticalScroll`). Sigue el mismo estilo que `AliasEditScreen`.

### Sección avatar
- `Avatar` grande 88dp centrado, con `imageUrl` si hay foto seleccionada o iniciales si no
- Overlay circular `AccentBlue` con ícono de lápiz, posicionado `bottom-end`
- Label "Cambiar foto" (`bodyS`, `AccentBlue`) debajo del avatar
- Sub-label "Toca el avatar para elegir" (`monoXS`, `TextTertiary`)
- Toca avatar o label → lanza photo picker

### Campos de texto
Usan `STTextField` con labels en `Eyebrow` (`monoXS`, `TextTertiary`):
- **Nombre**: `KeyboardType.Text`, `ImeAction.Next`
- **Teléfono**: `KeyboardType.Phone`, `ImeAction.Done`

### Campo email (solo lectura)
- Container: `BgSurface` con borde `BorderDefault`, opacidad reducida
- Color texto: `TextTertiary` (indica no editable)
- Badge a la derecha: ícono 🔒 + texto "Google" o "Apple" en `monoXS`
- Helper text debajo: `TextTertiary`, `monoXS`

### Botón guardar
- `STButton` primario, ancho completo, al final del scroll
- Texto: "Guardar cambios"
- Deshabilitado si no hay cambios respecto al estado inicial (`hasChanges = false`)

---

## Arquitectura (Clean Architecture + MVVM)

### Capa domain

**Modificación menor al modelo `User`:** añadir `authProvider: AuthProvider = AuthProvider.GOOGLE` como campo con default. En fase 1 el mock usa `GOOGLE`. En fase 2, Firebase Auth provee el valor real via `FirebaseUser.providerData`.

**Nuevo:** `domain/usecase/user/UpdateUserUseCase.kt`
```kotlin
class UpdateUserUseCase @Inject constructor(private val repository: UserRepository) {
    suspend operator fun invoke(user: User): Result<Unit> = repository.updateUser(user)
}
```

### Capa presentation

**Nuevos archivos en** `feature/profile/editprofile/`:

**`EditProfileUiState.kt`**
```kotlin
sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState
    data class Success(val data: EditProfileData) : EditProfileUiState
}

data class EditProfileData(
    val name: String,
    val phone: String,
    val email: String?,
    val authProvider: AuthProvider,   // GOOGLE | APPLE — derivado del User en fase 1
    val imageUrl: String?,
    val hasChanges: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: String? = null
)

enum class AuthProvider { GOOGLE, APPLE }
```

**`EditProfileViewModel.kt`**
- Colecta `GetCurrentUserUseCase()` → popula draft inicial
- `_draft: MutableStateFlow<EditProfileData?>` para cambios locales
- `_events: MutableSharedFlow<EditProfileEvent>` para `Saved`
- Funciones: `onNameChange`, `onPhoneChange`, `onPhotoSelected(uri: String)`, `save()`
- `save()`: valida nombre no vacío → llama `UpdateUserUseCase` → emite `EditProfileEvent.Saved`

**`EditProfileViewModelFactory.kt`**
- Construye manualmente con `MockUserRepository()`, `GetCurrentUserUseCase`, `UpdateUserUseCase`

**`EditProfileScreen.kt`**
- Lanza `PickVisualMedia` con `rememberLauncherForActivityResult`
- `LaunchedEffect` para colectar eventos → toast + `onBack()`
- Composable `EditProfileContent` separado para previews

### Modificaciones a archivos existentes

| Archivo | Cambio |
|---|---|
| `Avatar.kt` | Añadir `imageUrl: String?` — si no es null muestra `AsyncImage` (Coil) en lugar de iniciales |
| `Routes.kt` | Añadir `@Serializable data object EditUser : Route` |
| `SubTrackNavHost.kt` | Registrar `composable<Route.EditUser>` con `onBack` |
| `ProfileScreen.kt` | Reemplazar el TODO de `onNavigateToEditUser` con `navController.navigate(Route.EditUser)` |
| `ProfileScreen.kt` | Pasar `imageUrl = data.user.profileImageUrl` al `Avatar` del hero |

---

## Validaciones (fase 1)

- **Nombre vacío**: error inline debajo del campo, botón Guardar no se activa
- **Teléfono**: sin validación estricta, cualquier texto es válido
- **Foto**: solo se acepta content URI local; si el picker se cancela, se mantiene la foto anterior

---

## Strings nuevos (`strings.xml`)

```xml
<string name="edit_profile_title">Editar perfil</string>
<string name="edit_profile_save">Guardar</string>
<string name="edit_profile_save_button">Guardar cambios</string>
<string name="edit_profile_change_photo">Cambiar foto</string>
<string name="edit_profile_change_photo_hint">Toca el avatar para elegir</string>
<string name="edit_profile_field_name">NOMBRE</string>
<string name="edit_profile_field_phone">TELÉFONO</string>
<string name="edit_profile_field_email">EMAIL</string>
<string name="edit_profile_email_managed_google">Gestionado por tu cuenta de Google</string>
<string name="edit_profile_email_managed_apple">Gestionado por tu cuenta de Apple</string>
<string name="edit_profile_name_error">El nombre no puede estar vacío</string>
<string name="edit_profile_saved_toast">Perfil actualizado</string>
<string name="edit_profile_provider_google">Google</string>
<string name="edit_profile_provider_apple">Apple</string>
```

---

## Archivos tocados — resumen

```
domain/model/User.kt                                  (modificado — authProvider: AuthProvider)
domain/usecase/user/UpdateUserUseCase.kt              (nuevo)
feature/profile/editprofile/EditProfileUiState.kt     (nuevo)
feature/profile/editprofile/EditProfileViewModel.kt   (nuevo)
feature/profile/editprofile/EditProfileViewModelFactory.kt (nuevo)
feature/profile/editprofile/EditProfileScreen.kt      (nuevo)
core/designsystem/components/avatar/Avatar.kt         (modificado — imageUrl)
core/navigation/Routes.kt                             (modificado — EditUser)
core/navigation/SubTrackNavHost.kt                    (modificado — composable EditUser)
feature/profile/ProfileScreen.kt                      (modificado — conectar TODO + imageUrl en Avatar)
res/values/strings.xml                                (modificado — strings nuevos)
```

---

## Fuera de scope (fase 1)

- Upload real de foto a Firebase Storage
- Verificación de número de teléfono (SMS OTP)
- Cambio de proveedor de autenticación
- Eliminar foto de perfil (volver a iniciales)
