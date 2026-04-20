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
        ServiceTemplate("tpl_notion",   "Notion Plus",       "#000000", "N",  32.50, SubscriptionCategory.PRODUCTIVITY)
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
