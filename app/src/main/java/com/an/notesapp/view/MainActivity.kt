package com.an.notesapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.an.notesapp.Constants.ROUTE_DETAIL_ARG_NAME
import com.an.notesapp.Constants.ROUTE_DETAIL_PATH
import com.an.notesapp.Constants.ROUTE_HOME
import com.an.notesapp.R
import com.an.notesapp.view.ui.component.AppBarType
import com.an.notesapp.view.ui.component.MainTopAppBar
import com.an.notesapp.view.ui.screen.NotesScreen
import com.an.notesapp.view.ui.theme.NotesAppTheme
import com.an.notesapp.view.ui.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTheme {
                val appBarType = rememberSaveable { mutableStateOf(AppBarType.LARGE) }
                val titleId = rememberSaveable { mutableIntStateOf( R.string.app_name ) }
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                    rememberTopAppBarState()
                )

                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val showBackButton by remember(currentBackStackEntry) {
                    derivedStateOf { navController.previousBackStackEntry != null }
                }

                val viewModel = hiltViewModel<NoteViewModel>()

                Scaffold(
                    topBar = {
                        MainTopAppBar(
                            appBarType = appBarType.value,
                            showBackButton = showBackButton,
                            titleId = titleId.intValue,
                            scrollBehavior = scrollBehavior
                        )
                    },
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_HOME,
                        modifier = Modifier.padding(innerPadding).fillMaxSize()
                    ) {
                        composable(route = ROUTE_HOME) {
                            appBarType.value = AppBarType.LARGE
                            titleId.value = R.string.app_name

                            NotesScreen(viewModel = viewModel) { noteId ->
                                // TODO
                            }
                        }
                        composable(route = ROUTE_DETAIL_PATH,
                            arguments = listOf(
                                navArgument(ROUTE_DETAIL_ARG_NAME) { nullable = true },
                            )) {
                            appBarType.value = AppBarType.SMALL
                            titleId.value = R.string.note_detail_title
                        }
                    }
                }
            }
        }
    }
}
