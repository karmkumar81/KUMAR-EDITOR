package com.example.kumareditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kumareditor.data.ActiveToolTab
import com.example.kumareditor.ui.theme.KumarBorder
import com.example.kumareditor.ui.theme.KumarBorderLight
import com.example.kumareditor.ui.theme.KumarCardBg
import com.example.kumareditor.ui.theme.KumarCardHover
import com.example.kumareditor.ui.theme.KumarPrimary
import com.example.kumareditor.ui.theme.KumarTextMuted
import com.example.kumareditor.ui.theme.KumarTextPrimary
import com.example.kumareditor.ui.theme.KumarTextSecondary

data class ToolItem(
    val tab: ActiveToolTab,
    val icon: String,
    val label: String
)

@Composable
fun ToolsRow(
    activeTab: ActiveToolTab,
    onTabSelected: (ActiveToolTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tools = listOf(
        ToolItem(ActiveToolTab.IMPORT, "📥", "Import Files"),
        ToolItem(ActiveToolTab.SPLIT, "✂️", "Split"),
        ToolItem(ActiveToolTab.AUDIO, "🎵", "Audio"),
        ToolItem(ActiveToolTab.TEXT, "📝", "Text"),
        ToolItem(ActiveToolTab.MEDIA, "🖼️", "Media"),
        ToolItem(ActiveToolTab.EFFECTS, "✨", "Effects"),
        ToolItem(ActiveToolTab.TRANSITIONS, "⚡", "Transitions"),
        ToolItem(ActiveToolTab.AI_TOOLS, "🤖", "AI Tools")
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(KumarCardBg)
            .border(width = 0.5.dp, color = KumarBorder)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tools.forEach { tool ->
            val isSelected = activeTab == tool.tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) KumarPrimary.copy(alpha = 0.25f) else KumarCardHover)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) KumarPrimary else KumarBorderLight,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onTabSelected(tool.tab) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("tool_btn_${tool.label.lowercase().replace(" ", "_")}"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tool.icon,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = tool.label,
                        color = if (isSelected) KumarTextPrimary else KumarTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
