package com.an.notesapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.an.notesapp.Constants.ROUTE_DETAIL_ARG_NAME
import com.an.notesapp.Constants.ROUTE_DETAIL_PATH
import com.an.notesapp.Constants.ROUTE_HOME
import com.an.notesapp.intent.NoteIntent
import com.an.notesapp.util.stringResource
import com.an.notesapp.view.ui.component.MainTopAppBar
import com.an.notesapp.view.ui.screen.NoteDetailScreen
import com.an.notesapp.view.ui.screen.NotesScreen
import com.an.notesapp.view.ui.theme.NotesAppTheme
import com.an.notesapp.view.ui.viewmodel.BaseViewModel
import com.an.notesapp.view.ui.viewmodel.NoteDetailViewModel
import com.an.notesapp.view.ui.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTheme {
                val context = LocalContext.current
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState()
                )

                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val showBackButton by remember(currentBackStackEntry) {
                    derivedStateOf { navController.previousBackStackEntry != null }
                }

                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                    val viewModel = hiltViewModel<NoteViewModel>()

                    // Collect effects such as showing snackbar messages
                    LaunchedEffect(viewModel.eventsFlow) {
                        viewModel.eventsFlow.collect { event ->
                            when (event) {
                                is BaseViewModel.AppEvent.ShowSnackbar -> {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            context.stringResource(event.message),
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                                is BaseViewModel.AppEvent.ExitScreen -> navController.navigateUp()
                                is BaseViewModel.AppEvent.NavigateToDetail -> navController.navigate(ROUTE_DETAIL_PATH)
                                else -> {  }
                            }
                        }
                    }


                    Scaffold(
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        topBar = {
                            MainTopAppBar(
                                navController = navController,
                                showBackButton = showBackButton,
                                scrollBehavior = scrollBehavior
                            )
                        },
                        floatingActionButton = {
                            if (!showBackButton) {
                                FloatingActionButton(
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    onClick = { viewModel.handleIntent(NoteIntent.AddNoteClicked) }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add")
                                }
                            }
                        },
                        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = ROUTE_HOME,
                            modifier = Modifier.padding(innerPadding).fillMaxSize()
                        ) {
                            composable(route = ROUTE_HOME) {
                                NotesScreen(viewModel = viewModel) { noteId ->
                                    navController.navigate(
                                        ROUTE_DETAIL_PATH.replace(
                                            "{$ROUTE_DETAIL_ARG_NAME}",
                                            "$noteId"
                                        )
                                    )
                                }
                            }
                            composable(
                                route = ROUTE_DETAIL_PATH,
                                arguments = listOf(
                                    navArgument(ROUTE_DETAIL_ARG_NAME) { nullable = true },
                                )
                            ) {
                                val noteDetailViewModel = hiltViewModel<NoteDetailViewModel>()
                                NoteDetailScreen(noteDetailViewModel)
                            }
                        }
                    }
                }
        }
    }
}
