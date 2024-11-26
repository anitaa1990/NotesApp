package com.an.notesapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.navigation.compose.rememberNavController
import com.an.notesapp.view.ui.component.AppBarType
import com.an.notesapp.view.ui.component.MainTopAppBar
import com.an.notesapp.view.ui.theme.NotesAppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.an.notesapp.R

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

                }
            }
        }
    }
}
