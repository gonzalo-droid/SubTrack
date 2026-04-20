package com.gondroid.subtrack.core.designsystem.components.avatar

import androidx.annotation.DrawableRes
import com.gondroid.subtrack.R

data class ServiceLogoAsset(
    @DrawableRes val iconRes: Int,
    val brandColor: Long,
    val useWhiteBackground: Boolean = false
)

object ServiceLogoRegistry {
    private val map: Map<String, ServiceLogoAsset> = mapOf(
        "tpl_netflix"    to ServiceLogoAsset(R.drawable.ic_service_netflix,    0xFFE50914),
        "tpl_spotify"    to ServiceLogoAsset(R.drawable.ic_service_spotify,    0xFF1DB954),
        "tpl_hbo"        to ServiceLogoAsset(R.drawable.ic_service_hbomax,     0xFF9A1FD4),
        "tpl_youtube"    to ServiceLogoAsset(R.drawable.ic_service_youtube,    0xFFFF0000),
        "tpl_chatgpt"    to ServiceLogoAsset(R.drawable.ic_service_openai,     0xFF10A37F),
        "tpl_icloud"     to ServiceLogoAsset(R.drawable.ic_service_icloud,     0xFF007AFF),
        "tpl_notion"     to ServiceLogoAsset(R.drawable.ic_service_notion,     0xFF000000, useWhiteBackground = true),
        "tpl_appletv"    to ServiceLogoAsset(R.drawable.ic_service_appletv,    0xFF000000, useWhiteBackground = true),
        "tpl_applemusic" to ServiceLogoAsset(R.drawable.ic_service_applemusic, 0xFFFC3C44),
        "tpl_crunchyroll" to ServiceLogoAsset(R.drawable.ic_service_crunchyroll, 0xFFF47521),
        "tpl_claude"     to ServiceLogoAsset(R.drawable.ic_service_anthropic,  0xFFD97757),
        "tpl_linkedin"   to ServiceLogoAsset(R.drawable.ic_service_linkedin,   0xFF0A66C2),
    )

    fun get(templateId: String?): ServiceLogoAsset? = templateId?.let { map[it] }
}
