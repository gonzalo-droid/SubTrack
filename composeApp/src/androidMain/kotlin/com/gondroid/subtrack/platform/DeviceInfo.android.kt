package com.gondroid.subtrack.platform

import android.os.Build

actual fun deviceSummary(): String = "${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE}"
