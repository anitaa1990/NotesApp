package com.an.notesapp.view.ui.component

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    appBarType: AppBarType,
    showBackButton: Boolean,
    @StringRes titleId: Int,
    scrollBehavior: TopAppBarScrollBehavior,
    actions: @Composable RowScope.() -> Unit = {}
) {
    when (appBarType) {
        AppBarType.LARGE -> LargeTopAppBar(
            title = title(titleId),
            colors = topAppBarColors(),
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon(showBackButton),
            actions = actions
        )
        AppBarType.SMALL -> TopAppBar(
            title = title(titleId),
            colors = topAppBarColors(),
            scrollBehavior = scrollBehavior,
            navigationIcon = navigationIcon(showBackButton),
            actions = actions
        )
    }
}

@Composable
private fun title(@StringRes titleId: Int) = @Composable {
    Text(
        text = stringResource(id = titleId),
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.fillMaxWidth()
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

enum class AppBarType {
    LARGE, SMALL
}