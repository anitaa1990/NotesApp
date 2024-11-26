package com.an.notesapp.view.ui.component

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.FloatingWindow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.filterNot
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    showBackButton: Boolean,
    navController: NavHostController,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val currentContentBackStackEntry by produceState(
        initialValue = null as NavBackStackEntry?,
        producer = {
            navController.currentBackStackEntryFlow
                .filterNot { it.destination is FloatingWindow }
                .collect{ value = it }
        }
    )

    LargeTopAppBar(
        title = { AppBarTitle(currentContentBackStackEntry) },
        colors = topAppBarColors(),
        scrollBehavior = scrollBehavior,
        navigationIcon = navigationIcon(showBackButton),
        actions = actions
    )
}

@Composable
private fun navigationIcon(showBackButton: Boolean) = @Composable {
    val backPressDispatcher = LocalOnBackPressedDispatcherOwner.current
    if (showBackButton) {
        IconButton(
            onClick = { backPressDispatcher?.onBackPressedDispatcher?.onBackPressed() },
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun topAppBarColors() = TopAppBarColors(
    containerColor = MaterialTheme.colorScheme.primaryContainer,
    scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
    titleContentColor = MaterialTheme.colorScheme.primaryContainer,
    actionIconContentColor = MaterialTheme.colorScheme.primaryContainer,
    navigationIconContentColor = MaterialTheme.colorScheme.primaryContainer
)
