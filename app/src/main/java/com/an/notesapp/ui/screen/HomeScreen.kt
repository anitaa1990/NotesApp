package com.an.notesapp.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.an.notesapp.Constants
import com.an.notesapp.navigation.Navigation
import com.an.notesapp.ui.theme.NotesAppTheme

@Composable
fun HomeScreen(
    navController: NavHostController = rememberNavController()
) {
    NotesAppTheme {
        Scaffold(
            topBar = {
                MainTopAppBar(navController = navController)
            },
            floatingActionButton = {
                FloatingActionButton(
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = {
                        navController.navigate(Constants.ROUTE_DETAIL_PATH)
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
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

