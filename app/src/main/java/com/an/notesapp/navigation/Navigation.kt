package com.an.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.an.notesapp.Constants.ROUTE_DETAIL_ARG_NAME
import com.an.notesapp.Constants.ROUTE_DETAIL_PATH
import com.an.notesapp.Constants.ROUTE_HOME
import com.an.notesapp.ui.component.SnackbarMessage
import com.an.notesapp.ui.component.SnackbarMessageHandler
import com.an.notesapp.ui.screen.NoteDetailScreen
import com.an.notesapp.ui.screen.NotesScreen
import com.an.notesapp.ui.viewmodel.BaseViewModel
import com.an.notesapp.ui.viewmodel.NoteDetailViewModel
import com.an.notesapp.ui.viewmodel.NoteViewModel

@Composable
fun Navigation(
    modifier: Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = ROUTE_HOME,
    ) {
        composable(route = ROUTE_HOME) {
            val viewModel = hiltViewModel<NoteViewModel>()
            EventHandler(viewModel, navController)
            NotesScreen(viewModel) { noteId ->
                navController.navigate(
                    ROUTE_DETAIL_PATH.replace("{$ROUTE_DETAIL_ARG_NAME}", "$noteId")
                )
            }
        }

        composable(
            route = ROUTE_DETAIL_PATH,
            arguments = listOf(
                navArgument(ROUTE_DETAIL_ARG_NAME) { nullable = true },
            ),
        ) {
            val viewModel = hiltViewModel<NoteDetailViewModel>()
            EventHandler(viewModel, navController)
            NoteDetailScreen(viewModel)
        }
    }
}

@Composable
fun EventHandler(
    viewModel: BaseViewModel,
    navController: NavHostController
) {
    viewModel.eventsFlow.collectAsStateWithLifecycle(
        initialValue = null,
        lifecycle = LocalLifecycleOwner.current.lifecycle
    ).value?.let { event ->
        when (event) {
            is BaseViewModel.Event.ShowSnackbar -> {
                SnackbarMessageHandler(snackbarMessage = SnackbarMessage(event.message))
            }
            is BaseViewModel.Event.ExitScreen -> navController.navigateUp()
        }
    }
}