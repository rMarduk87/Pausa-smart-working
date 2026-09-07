package rpt.tool.hybridwalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.*
import rpt.tool.hybridwalk.ui.settings.WearSettingsScreen
import rpt.tool.hybridwalk.ui.WearViewModel

class WearMainActivity : ComponentActivity() {

    private val viewModel: WearViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HybridWalkWearApp(viewModel)
        }
    }
}

@Composable
fun HybridWalkWearApp(viewModel: WearViewModel) {
    val steps by viewModel.currentSteps.collectAsState()
    val goal by viewModel.stepGoal.collectAsState()
    val isWfh by viewModel.isWfh.collectAsState()
    val primaryColorHex by viewModel.primaryColorHex.collectAsState()

    val primaryColor = try {
        Color(primaryColorHex.toColorInt())
    } catch (e: Exception) {
        Color(0xFF81B29A)
    }

    val pagerState = rememberPagerState(pageCount = { 2 })

    val pageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageOffset: Float
                get() = pagerState.currentPageOffsetFraction
            override val selectedPage: Int
                get() = pagerState.currentPage
            override val pageCount: Int
                get() = pagerState.pageCount
        }
    }

    MaterialTheme(
        colors = Colors(
            primary = primaryColor,
            onPrimary = Color.Black,
            secondary = primaryColor.copy(alpha = 0.7f),
            onSecondary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            error = Color.Red,
            onError = Color.White
        )
    ) {
        Scaffold(
            timeText = { TimeText() },
            pageIndicator = {
                HorizontalPageIndicator(pageIndicatorState = pageIndicatorState)
            }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> DashboardScreen(steps, goal, isWfh, viewModel)
                    1 -> WearSettingsScreen(
                        stepGoal = goal,
                        selectedColorHex = primaryColorHex,
                        onStepGoalChanged = viewModel::updateStepGoal,
                        onColorSelected = viewModel::updatePrimaryColor
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(
    steps: Int,
    goal: Int,
    isWfh: Boolean,
    viewModel: WearViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            Text(
                text = if (isWfh) stringResource(R.string.wfh_emoji) else stringResource(R.string.gym_emoji),
                style = MaterialTheme.typography.title1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$steps",
                style = MaterialTheme.typography.display1,
                color = MaterialTheme.colors.primary
            )

            Text(
                text = stringResource(R.string.steps_goal_format, goal),
                style = MaterialTheme.typography.caption1,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Chip(
                onClick = { viewModel.toggleMode() },
                label = {
                    Text(
                        text = if (isWfh) stringResource(R.string.smart_working) else stringResource(R.string.rest_day),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = ChipDefaults.primaryChipColors(
                    backgroundColor = if (isWfh) MaterialTheme.colors.primary else Color(0xFFE07A5F)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}