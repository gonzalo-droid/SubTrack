package com.gondroid.subtrack.core.designsystem.components.avatar

import com.gondroid.subtrack.resources.Res
import com.gondroid.subtrack.resources.ic_service_anthropic
import com.gondroid.subtrack.resources.ic_service_applemusic
import com.gondroid.subtrack.resources.ic_service_appletv
import com.gondroid.subtrack.resources.ic_service_crunchyroll
import com.gondroid.subtrack.resources.ic_service_disneyplus
import com.gondroid.subtrack.resources.ic_service_hbomax
import com.gondroid.subtrack.resources.ic_service_icloud
import com.gondroid.subtrack.resources.ic_service_linkedin
import com.gondroid.subtrack.resources.ic_service_netflix
import com.gondroid.subtrack.resources.ic_service_notion
import com.gondroid.subtrack.resources.ic_service_openai
import com.gondroid.subtrack.resources.ic_service_prime
import com.gondroid.subtrack.resources.ic_service_spotify
import com.gondroid.subtrack.resources.ic_service_youtube

import org.jetbrains.compose.resources.DrawableResource

data class ServiceLogoAsset(
    val iconRes: DrawableResource,
    val brandColor: Long,
    val useWhiteBackground: Boolean = false,
    /** Share of the tile the icon fills; wide wordmarks need more to stay legible. */
    val iconScale: Float = 0.55f
)

object ServiceLogoRegistry {
    private val map: Map<String, ServiceLogoAsset> = mapOf(
        "tpl_netflix"    to ServiceLogoAsset(Res.drawable.ic_service_netflix,    0xFFE50914),
        "tpl_spotify"    to ServiceLogoAsset(Res.drawable.ic_service_spotify,    0xFF1DB954),
        "tpl_hbo"        to ServiceLogoAsset(Res.drawable.ic_service_hbomax,     0xFF9A1FD4),
        "tpl_youtube"    to ServiceLogoAsset(Res.drawable.ic_service_youtube,    0xFFFF0000),
        "tpl_chatgpt"    to ServiceLogoAsset(Res.drawable.ic_service_openai,     0xFF10A37F),
        "tpl_icloud"     to ServiceLogoAsset(Res.drawable.ic_service_icloud,     0xFF007AFF),
        "tpl_notion"     to ServiceLogoAsset(Res.drawable.ic_service_notion,     0xFF000000, useWhiteBackground = true),
        "tpl_appletv"    to ServiceLogoAsset(Res.drawable.ic_service_appletv,    0xFF000000, useWhiteBackground = true),
        "tpl_applemusic" to ServiceLogoAsset(Res.drawable.ic_service_applemusic, 0xFFFC3C44),
        "tpl_crunchyroll" to ServiceLogoAsset(Res.drawable.ic_service_crunchyroll, 0xFFF47521),
        "tpl_claude"     to ServiceLogoAsset(Res.drawable.ic_service_anthropic,  0xFFD97757),
        "tpl_linkedin"   to ServiceLogoAsset(Res.drawable.ic_service_linkedin,   0xFF0A66C2),
        "tpl_disney"     to ServiceLogoAsset(Res.drawable.ic_service_disneyplus, 0xFF113CCF, iconScale = 0.85f),
        "tpl_prime"      to ServiceLogoAsset(Res.drawable.ic_service_prime,      0xFF00A8E1),
    )

    fun get(templateId: String?): ServiceLogoAsset? = templateId?.let { map[it] }
}
