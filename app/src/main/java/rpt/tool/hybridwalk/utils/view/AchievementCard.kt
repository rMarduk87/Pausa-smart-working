package rpt.tool.hybridwalk.utils.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import rpt.tool.hybridwalk.R
import rpt.tool.hybridwalk.utils.data.appmodels.AchievementComplex
import androidx.compose.ui.res.stringResource

@Composable
fun AchievementCard(achievement: AchievementComplex, isEarned: Boolean) {
    val context = LocalContext.current

    // Gestione colori in stile Compose, con fallback di sicurezza se l'hex è malformato
    val baseColorInt = try {
        achievement.backgroundColor.toColorInt()
    } catch (e: Exception) {
        android.graphics.Color.GRAY
    }

    val baseColor = Color(baseColorInt)

    val cardBg = if (isEarned) Color(baseColorInt).copy(alpha = 0.15f) else
        MaterialTheme.colorScheme.surface
    val strokeColor = if (isEarned) baseColor else Color.DarkGray
    val alphaValue = if (isEarned) 1f else 0.5f

    val current = achievement.detail.current
    val target = achievement.detail.target
    val progress = if (target > 0) current.toFloat() / target.toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alphaValue),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, strokeColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                // Icona tonda
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, strokeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(achievement.imageId),
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Testi (Titolo + Data se sbloccato)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(achievement.titleID),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (isEarned && !achievement.date.isNullOrEmpty()) {
                        Text(
                            text = "Sbloccato il ${achievement.date}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = baseColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Text(
                        text = stringResource(achievement.descriptionValue),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra di Progresso Inferiore
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$current / $target ${stringResource(
                        achievement.detail.typeDescription)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = if (isEarned) baseColor else Color.Gray,
                trackColor = MaterialTheme.colorScheme.background
            )
        }
    }
}