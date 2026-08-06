package rpt.tool.hybridwalk.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.*
import androidx.core.graphics.toColorInt
import rpt.tool.hybridwalk.R

@Composable
fun WearSettingsScreen(
    stepGoal: Int,
    selectedColorHex: String,
    onStepGoalChanged: (Int) -> Unit,
    onColorSelected: (String) -> Unit
) {
    val colorOptions = listOf("#81B29A", "#3B82F6", "#F59E0B", "#EF4444", "#8B5CF6", "#EC4899")
    val listState = rememberScalingLazyListState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.title1,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.theme_color),
                    style = MaterialTheme.typography.caption2,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    colorOptions.take(3).forEach { hex ->
                        ColorOption(hex, selectedColorHex, onColorSelected)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    colorOptions.drop(3).forEach { hex ->
                        ColorOption(hex, selectedColorHex, onColorSelected)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.daily_step_goal),
                    style = MaterialTheme.typography.caption2,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { if (stepGoal > 2000) onStepGoalChanged(stepGoal - 500) },
                        modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Text("-", style = MaterialTheme.typography.title2)
                    }

                    Text(
                        text = "$stepGoal",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Button(
                        onClick = { if (stepGoal < 20000) onStepGoalChanged(stepGoal + 500) },
                        modifier = Modifier.size(ButtonDefaults.SmallButtonSize),
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Text("+", style = MaterialTheme.typography.title2)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ColorOption(
    hex: String,
    selectedColorHex: String,
    onColorSelected: (String) -> Unit
) {
    val colorObj = try {
        Color(hex.toColorInt())
    } catch (e: Exception) {
        Color.Gray
    }

    val isSelected = hex.equals(selectedColorHex, ignoreCase = true)

    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .size(32.dp)
            .background(colorObj, CircleShape)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color.White, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable {
                onColorSelected(hex)
            }
    )
}
