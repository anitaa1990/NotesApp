package com.an.notesapp.ui.screen

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.navigation.FloatingWindow
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import kotlinx.coroutines.flow.filterNot


// Ref: https://stackoverflow.com/questions/71417326/jetpack-compose-topappbar-with-dynamic-actions
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(navController: NavController) {
    val currentContentBackStackEntry by produceState(
        initialValue = null as NavBackStackEntry?,
        producer = {
            navController.currentBackStackEntryFlow
                .filterNot { it.destination is FloatingWindow }
                .collect{ value = it }
        }
    )
    TopAppBar(
        navigationIcon = {
            val backPressDispatcher = LocalOnBackPressedDispatcherOwner.current
            IconButton(
                onClick = { backPressDispatcher?.onBackPressedDispatcher?.onBackPressed() },
                content = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "arrowBack"
                    )
                }
            )
        },
        title = {
            AppBarTitle(currentContentBackStackEntry)
        },
        actions = {
            AppBarAction(currentContentBackStackEntry)
        }
    )
}