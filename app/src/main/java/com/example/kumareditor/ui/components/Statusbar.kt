package com.example.kumareditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kumareditor.ui.theme.KumarBorder
import com.example.kumareditor.ui.theme.KumarHeaderBg
import com.example.kumareditor.ui.theme.KumarPrimary
import com.example.kumareditor.ui.theme.KumarTextPrimary
import com.example.kumareditor.ui.theme.KumarTextSecondary

@Composable
fun Statusbar(
    statusMessage: String,
    exportProgress: Float?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(KumarHeaderBg)
            .border(width = 0.5.dp, color = KumarBorder)
    ) {
        if (exportProgress != null) {
            LinearProgressIndicator(
                progress = { exportProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = KumarPrimary,
                trackColor = Color.Transparent
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = statusMessage,
                color = KumarTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "KUMAR v1.0 • Ready",
                color = KumarTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}
