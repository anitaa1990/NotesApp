package com.an.notesapp.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.an.notesapp.Constants
import com.an.notesapp.navigation.Navigation
import com.an.notesapp.ui.component.MainTopAppBar
import com.an.notesapp.ui.component.ProvideSnackbarController
import com.an.notesapp.ui.theme.NotesAppTheme

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController()
) {
    NotesAppTheme {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val isRootScreen by remember(currentBackStackEntry) {
            derivedStateOf { navController.previousBackStackEntry == null }
        }

        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        ProvideSnackbarController(
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    MainTopAppBar(
                        navController = navController,
                        showBackButton = !isRootScreen
                    )
                },
                floatingActionButton = {
                    if (isRootScreen) {
                        FloatingActionButton(
                            shape = CircleShape,
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary,
                            onClick = { navController.navigate(Constants.ROUTE_DETAIL_PATH) }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                }
            ) { innerPadding ->
                Navigation(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController
                )
            }
        }
    }
}

