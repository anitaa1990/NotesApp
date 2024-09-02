package com.an.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.an.notesapp.Constants.ROUTE_DETAIL_ARG_NAME
import com.an.notesapp.Constants.ROUTE_DETAIL_PATH
import com.an.notesapp.Constants.ROUTE_HOME
import com.an.notesapp.ui.screen.NoteDetailScreen
import com.an.notesapp.ui.screen.NotesScreen
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
            NotesScreen(hiltViewModel<NoteViewModel>()) { noteId ->
                navController.navigate(
                    ROUTE_DETAIL_PATH.replace("{$ROUTE_DETAIL_ARG_NAME}", "$noteId")
                )
            }
        }

        composable(
            route = ROUTE_DETAIL_PATH,
            arguments = listOf(
                navArgument(ROUTE_DETAIL_ARG_NAME) { type = NavType.LongType },
            ),
        ) {
            NoteDetailScreen(hiltViewModel<NoteDetailViewModel>())
        }
    }
}