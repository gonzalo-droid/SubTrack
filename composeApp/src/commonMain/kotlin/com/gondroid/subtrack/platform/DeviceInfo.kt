package com.gondroid.subtrack.platform

/** "Google Pixel 9 · Android 16" / "iPhone · iOS 26.1", for support emails (AJU-02). */
expect fun deviceSummary(): String

/** Field of `config/app` with this platform's minimum build: `minVersionCode` (Android) or `minVersionIos`. */
expect val MIN_VERSION_FIELD: String
