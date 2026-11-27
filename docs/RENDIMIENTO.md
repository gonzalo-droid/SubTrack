# Rendimiento por versión (PERF-01)

Se mide antes de cada release, con el build de release (R8) en el mismo emulador, para ver si algo crece sin querer.
Arranque en frío: `adb shell am force-stop` y `am start -W`, cinco veces; se anota la mediana de las corridas 2 a 5
(la primera incluye la compilación inicial del código tras instalar).

| Versión | Fecha | AAB | APK universal | Arranque en frío (mediana) | Primer arranque tras instalar | Equipo | Datos |
|---|---|---|---|---|---|---|---|
| 0.1.0 (1) | 2026-11-27 | 14.6 MB | 8.6 MB | 0.39 s | 1.10 s | Emulador Pixel 10 Pro (Android 16) | Sin red propia: solo Firebase Auth y Firestore (cambios fila por fila) |

## iOS

Pendiente: medir el `.ipa` de un archive de Release y el arranque en un iPhone real cuando la app esté registrada en Firebase.

## Notas

- El AAB pesa más que el APK porque trae `sqlite-bundled` para todas las arquitecturas; Play entrega a cada equipo solo la suya.
- El emulador corre en un Mac con Apple Silicon: los tiempos sirven para comparar versiones, no como valor de un teléfono real.
