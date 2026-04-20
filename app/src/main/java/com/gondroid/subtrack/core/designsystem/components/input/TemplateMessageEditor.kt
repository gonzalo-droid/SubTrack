package com.gondroid.subtrack.core.designsystem.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gondroid.subtrack.core.designsystem.theme.AccentGreen
import com.gondroid.subtrack.core.designsystem.theme.AccentGreenBg
import com.gondroid.subtrack.core.designsystem.theme.BorderDefault
import com.gondroid.subtrack.core.designsystem.theme.Geist
import com.gondroid.subtrack.core.designsystem.theme.JetBrainsMono
import com.gondroid.subtrack.core.designsystem.theme.SubTrackShapes
import com.gondroid.subtrack.core.designsystem.theme.SubTrackTheme
import com.gondroid.subtrack.core.designsystem.theme.TextPrimary
import com.gondroid.subtrack.core.designsystem.theme.TextTertiary

private val TOKEN_REGEX = Regex("""\{[a-z_]+\}""")

private val bodyStyle = TextStyle(
    fontFamily = Geist,
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal,
    color = TextPrimary
)

private val variableSpan = SpanStyle(
    fontFamily = JetBrainsMono,
    fontSize = 11.sp,
    fontWeight = FontWeight.Medium,
    color = AccentGreen,
    background = Color(0x1F32D74B)
)

private class VariableVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val annotated = buildAnnotatedString {
            append(text.text)
            TOKEN_REGEX.findAll(text.text).forEach { match ->
                addStyle(variableSpan, match.range.first, match.range.last + 1)
            }
        }
        return TransformedText(annotated, OffsetMapping.Identity)
    }
}

/**
 * Protects variable tokens: if a change would break a {token}, the token is deleted atomically.
 */
fun protectVariableTokens(old: TextFieldValue, new: TextFieldValue): TextFieldValue {
    if (new.text == old.text) return new
    val deleted = old.text.length > new.text.length
    if (!deleted) return new

    val oldText = old.text
    // Find if the edit lands inside a token in the old text
    val cursorInOld = old.selection.start
    val match = TOKEN_REGEX.findAll(oldText).find { it.range.contains(cursorInOld - 1) || it.range.contains(cursorInOld) }
    if (match != null) {
        // Remove the entire token atomically
        val newText = oldText.removeRange(match.range.first, match.range.last + 1)
        val newCursor = match.range.first
        return TextFieldValue(text = newText, selection = TextRange(newCursor))
    }
    return new
}

@Composable
fun TemplateMessageEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    maxChars: Int = 500,
    minHeight: Dp = 130.dp
) {
    val transformation = remember { VariableVisualTransformation() }

    BasicTextField(
        value = value,
        onValueChange = { new ->
            if (new.text.length <= maxChars) {
                onValueChange(protectVariableTokens(value, new))
            }
        },
        textStyle = bodyStyle,
        cursorBrush = SolidColor(AccentGreen),
        visualTransformation = transformation,
        keyboardOptions = KeyboardOptions.Default,
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight)
            .clip(SubTrackShapes.base)
            .background(AccentGreen.copy(alpha = 0.03f))
            .border(1.dp, AccentGreen.copy(alpha = 0.15f), SubTrackShapes.base)
            .padding(12.dp),
        decorationBox = { innerTextField ->
            Box {
                if (value.text.isEmpty()) {
                    androidx.compose.material3.Text(
                        text = "Escribe tu mensaje aquí…",
                        style = bodyStyle.copy(color = TextTertiary)
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0C)
@Composable
private fun TemplateMessageEditorPreview() {
    SubTrackTheme {
        TemplateMessageEditor(
            value = TextFieldValue("Hola {nombre}! 👋 Recuerda pagar {servicio} — S/{monto}. Gracias!"),
            onValueChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
