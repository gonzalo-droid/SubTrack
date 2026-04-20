package com.gondroid.subtrack.data.mock

import com.gondroid.subtrack.domain.model.ServiceTemplate
import com.gondroid.subtrack.domain.model.enums.SubscriptionCategory

object ServiceTemplates {

    val popular = listOf(
        ServiceTemplate("tpl_netflix",  "Netflix",           "#E50914", "N",  54.90, SubscriptionCategory.STREAMING),
        ServiceTemplate("tpl_spotify",  "Spotify",           "#1DB954", "S",  26.90, SubscriptionCategory.MUSIC),
        ServiceTemplate("tpl_hbo",      "HBO Max",           "#9A1FD4", "H",  34.90, SubscriptionCategory.STREAMING),
        ServiceTemplate("tpl_disney",   "Disney+",           "#113CCF", "D+", 33.90, SubscriptionCategory.STREAMING),
        ServiceTemplate("tpl_youtube",  "YouTube Premium",   "#FF0000", "YT", 31.90, SubscriptionCategory.STREAMING),
        ServiceTemplate("tpl_chatgpt",  "ChatGPT Plus",      "#10A37F", "AI", 74.90, SubscriptionCategory.AI),
        ServiceTemplate("tpl_prime",    "Prime Video",       "#00A8E1", "P",  29.90, SubscriptionCategory.STREAMING),
        ServiceTemplate("tpl_icloud",   "iCloud+",           "#007AFF", "iC", 13.00, SubscriptionCategory.CLOUD),
        ServiceTemplate("tpl_notion",       "Notion Plus",       "#000000", "N",  32.50, SubscriptionCategory.PRODUCTIVITY),
        ServiceTemplate("tpl_appletv",      "Apple TV+",         "#000000", "TV", 18.90, SubscriptionCategory.STREAMING),
        ServiceTemplate("tpl_applemusic",   "Apple Music",       "#FC3C44", "♫",  17.90, SubscriptionCategory.MUSIC),
        ServiceTemplate("tpl_crunchyroll",  "Crunchyroll",       "#F47521", "CR", 24.90, SubscriptionCategory.STREAMING),
        ServiceTemplate("tpl_claude",       "Claude Pro",        "#D97757", "C",  89.90, SubscriptionCategory.AI),
        ServiceTemplate("tpl_linkedin",     "LinkedIn Premium",  "#0A66C2", "in", 89.90, SubscriptionCategory.OTHER)
    )

    val other = ServiceTemplate(
        id = "tpl_other",
        name = "Otro",
        brandColor = "#888888",
        defaultLogoChar = "+",
        suggestedMonthly = null,
        category = SubscriptionCategory.OTHER,
        isPopular = false
    )
}
