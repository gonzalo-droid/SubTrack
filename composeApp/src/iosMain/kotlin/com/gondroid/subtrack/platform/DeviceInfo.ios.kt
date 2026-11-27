package com.gondroid.subtrack.platform

import platform.UIKit.UIDevice

actual fun deviceSummary(): String =
    "${UIDevice.currentDevice.model} · ${UIDevice.currentDevice.systemName} ${UIDevice.currentDevice.systemVersion}"
