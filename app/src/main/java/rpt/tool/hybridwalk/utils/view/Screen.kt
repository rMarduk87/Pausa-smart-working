package rpt.tool.hybridwalk.utils.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import rpt.tool.hybridwalk.R

sealed class Screen(val route: String, @androidx.annotation.StringRes val titleRes: Int, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", R.string.oggi, Icons.Default.Home)
    object Stats : Screen("stats", R.string.andamento, Icons.Default.Info)
    object Achievement : Screen("achievement", R.string.obiettivi, Icons.Default.Star)
    object Settings : Screen("settings", R.string.impostazioni, Icons.Default.Settings)
}

@Composable
fun HybridScaffold(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = colorResource(R.color.surface_dark),
                contentColor = Color.White
            ) {
                val items = listOf(Screen.Dashboard, Screen.Stats, Screen.Achievement, Screen.Settings)
                items.forEach { screen ->
                    val title = androidx.compose.ui.res.stringResource(screen.titleRes)
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = title) },
                        label = { Text(title) },
                        selected = currentScreen == screen,
                        onClick = { onTabSelected(screen) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}