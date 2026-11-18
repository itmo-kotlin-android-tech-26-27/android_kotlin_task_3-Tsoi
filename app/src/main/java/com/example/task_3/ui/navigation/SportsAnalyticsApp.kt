package com.example.task_3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.task_3.ui.group.GroupScreen
import com.example.task_3.ui.group.GroupViewModel
import com.example.task_3.ui.section.SectionScreen
import com.example.task_3.ui.section.SectionViewModel
import com.example.task_3.ui.sections.SectionsScreen
import com.example.task_3.ui.sections.SectionsViewModel

@Composable
fun SportsAnalyticsApp(navController: NavHostController = rememberNavController()) {
    val onHome: () -> Unit = { navController.popBackStack(Routes.SECTIONS, inclusive = false) }
    NavHost(navController, startDestination = Routes.SECTIONS) {
        composable(Routes.SECTIONS) {
            val viewModel: SectionsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            SectionsScreen(
                state,
                onQueryChange = viewModel::updateQuery,
                onSectionClick = { id ->
                    viewModel.selectSection(id)
                    navController.navigate(Routes.section(id)) { launchSingleTop = true }
                },
                onRetry = viewModel::reload,
            )
        }
        composable(
            route = Routes.SECTION,
            arguments = listOf(navArgument(Routes.SECTION_ID) { type = NavType.StringType }),
        ) {
            val viewModel: SectionViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            SectionScreen(
                state,
                onGroupClick = { groupId ->
                    val section = state.section
                    if (section != null) {
                        viewModel.selectGroup(groupId)
                        navController.navigate(Routes.group(section.id, groupId)) { launchSingleTop = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onHome = onHome,
                onRetry = viewModel::reload,
            )
        }
        composable(
            route = Routes.GROUP,
            arguments = listOf(
                navArgument(Routes.SECTION_ID) { type = NavType.StringType },
                navArgument(Routes.GROUP_ID) { type = NavType.StringType },
            ),
        ) {
            val viewModel: GroupViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            GroupScreen(
                state,
                onBack = { navController.popBackStack() },
                onHome = onHome,
                onRetry = viewModel::reload,
            )
        }
    }
}
