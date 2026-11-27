package com.gondroid.subtrack.core.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink

private val LINK = Regex("""<a href="([^"]+)">(.*?)</a>""")

/** Plain text with `<a href="…">…</a>` links (the only HTML the strings use) as clickable links. */
fun textWithLinks(html: String, linkStyles: TextLinkStyles? = null): AnnotatedString = buildAnnotatedString {
    var last = 0
    LINK.findAll(html).forEach { match ->
        append(html.substring(last, match.range.first))
        withLink(LinkAnnotation.Url(match.groupValues[1], linkStyles)) { append(match.groupValues[2]) }
        last = match.range.last + 1
    }
    append(html.substring(last))
}
