package com.gestaoicj.app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import com.gestaoicj.app.ui.theme.Inter
import com.gestaoicj.app.ui.theme.extraColors

class BottomItem(val screen: Screen, val label: String, val icon: ImageVector)

val bottomItems = listOf(
    BottomItem(Screen.Estoque, "Estoque", Icons.Filled.Inventory2),
    BottomItem(Screen.Reposicao, "Reposição", Icons.Filled.Autorenew),
    BottomItem(Screen.Historico, "Histórico", Icons.Filled.History),
    BottomItem(Screen.Perfil, "Perfil", Icons.Filled.Person),
)

@Composable
fun IcjBottomBar(currentRoute: String?, onSelect: (Screen) -> Unit) {
    val cores = MaterialTheme.colorScheme

    Column {
        HorizontalDivider(color = cores.outline)
        NavigationBar(containerColor = cores.surfaceContainer) {
            bottomItems.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.screen.route,
                    onClick = { onSelect(item.screen) },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, fontFamily = Inter, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = cores.onPrimary,
                        selectedTextColor = MaterialTheme.extraColors.accent,
                        indicatorColor = cores.primary,
                        unselectedIconColor = cores.onSurfaceVariant,
                        unselectedTextColor = cores.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}