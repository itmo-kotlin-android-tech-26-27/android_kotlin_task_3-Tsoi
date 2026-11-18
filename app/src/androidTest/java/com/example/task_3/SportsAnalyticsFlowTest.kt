package com.example.task_3

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SportsAnalyticsFlowTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigationBackRestoresSectionAndStartScreens() {
        waitForTag(BOXING_SECTION)

        composeRule.onNodeWithTag(BOXING_SECTION).performClick()
        waitForTag(JUNIORS_GROUP)

        composeRule.onNodeWithTag(JUNIORS_GROUP).performClick()
        waitForTag(ATTENDANCE_VALUE)
        composeRule.onNodeWithTag(ATTENDANCE_VALUE).assertTextEquals("63%")
        composeRule.onNodeWithText("Юниоры").assertIsDisplayed()

        Espresso.pressBack()
        waitForTag(SECTION_SCREEN)
        composeRule.onNodeWithTag(JUNIORS_GROUP).assertIsDisplayed()
        composeRule.onNodeWithTag(GROUP_SCREEN).assertDoesNotExist()

        Espresso.pressBack()
        waitForTag(SECTIONS_SCREEN)
        composeRule.onNodeWithTag(BOXING_SECTION).assertIsDisplayed()
        composeRule.onNodeWithTag(SECTION_SCREEN).assertDoesNotExist()
    }

    @Test
    fun returningHomeStartsFromSections() {
        openJuniors()
        composeRule.onNodeWithTag(HOME_BUTTON).performClick()
        waitForTag(BOXING_SECTION)
        composeRule.onNodeWithTag(SECTIONS_SCREEN).assertIsDisplayed()

        composeRule.onNodeWithTag(BOXING_SECTION).performClick()
        waitForTag(JUNIORS_GROUP)
        Espresso.pressBack()

        waitForTag(SECTIONS_SCREEN)
        composeRule.onNodeWithTag(BOXING_SECTION).assertIsDisplayed()
        composeRule.onNodeWithTag(GROUP_SCREEN).assertDoesNotExist()
    }

    @Test
    fun searchSurvivesActivityRecreationAndCanBeCleared() {
        waitForTag(BOXING_SECTION)
        composeRule.onNodeWithTag(SEARCH_FIELD).performTextInput("Бокс")
        Espresso.closeSoftKeyboard()
        composeRule.onNodeWithTag(BOXING_SECTION).assertIsDisplayed()
        composeRule.onNodeWithTag(SWIMMING_SECTION).assertDoesNotExist()

        composeRule.activityRule.scenario.recreate()

        waitForTag(BOXING_SECTION)
        composeRule.onNodeWithTag(SEARCH_FIELD).assertTextContains("Бокс")
        composeRule.onNodeWithTag(SWIMMING_SECTION).assertDoesNotExist()

        composeRule.onNodeWithTag(SEARCH_FIELD).performTextClearance()
        composeRule.onNodeWithTag(SEARCH_FIELD).performTextInput("Несуществующая секция")
        Espresso.closeSoftKeyboard()
        waitForText("Ничего не найдено")
        composeRule.onNodeWithTag(BOXING_SECTION).assertDoesNotExist()
        composeRule.onNodeWithText("Очистить поиск").performClick()
        Espresso.closeSoftKeyboard()

        waitForTag(BOXING_SECTION)
        waitForTag(SWIMMING_SECTION)
    }

    @Test
    fun groupDetailsSurviveActivityRecreation() {
        openJuniors()

        composeRule.activityRule.scenario.recreate()

        waitForTag(ATTENDANCE_VALUE)
        composeRule.onNodeWithTag(GROUP_SCREEN).assertIsDisplayed()
        composeRule.onNodeWithText("Юниоры").assertIsDisplayed()
        composeRule.onNodeWithTag(ATTENDANCE_VALUE).assertTextEquals("63%")
        Espresso.pressBack()
        waitForTag(JUNIORS_GROUP)
    }

    @Test
    fun groupWithoutTrainingsShowsEmptyAttendance() {
        waitForTag(SWIMMING_SECTION)
        composeRule.onNodeWithTag(SWIMMING_SECTION).performClick()
        waitForTag(NEW_GROUP)
        composeRule.onNodeWithTag(NEW_GROUP).performClick()

        waitForTag(ATTENDANCE_VALUE)
        composeRule.onNodeWithTag(ATTENDANCE_VALUE).assertTextEquals("—")
        composeRule.onNodeWithText("За этот период ещё не было тренировок.")
            .assertIsDisplayed()
    }

    private fun openJuniors() {
        waitForTag(BOXING_SECTION)
        composeRule.onNodeWithTag(BOXING_SECTION).performClick()
        waitForTag(JUNIORS_GROUP)
        composeRule.onNodeWithTag(JUNIORS_GROUP).performClick()
        waitForTag(ATTENDANCE_VALUE)
    }

    private fun waitForTag(tag: String) {
        val listTag = when (tag) {
            BOXING_SECTION, SWIMMING_SECTION -> SECTIONS_LIST
            JUNIORS_GROUP, NEW_GROUP -> GROUPS_LIST
            else -> null
        }
        if (listTag != null) {
            composeRule.waitUntil(timeoutMillis = WAIT_TIMEOUT_MILLIS) {
                composeRule.onAllNodesWithTag(listTag).fetchSemanticsNodes().isNotEmpty()
            }
            composeRule.onNodeWithTag(listTag).performScrollToNode(hasTestTag(tag))
        }
        composeRule.waitUntil(timeoutMillis = WAIT_TIMEOUT_MILLIS) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag(tag).assertIsDisplayed()
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = WAIT_TIMEOUT_MILLIS) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    private companion object {
        const val WAIT_TIMEOUT_MILLIS = 10_000L
        const val SECTIONS_SCREEN = "screen_sections"
        const val SECTION_SCREEN = "screen_section"
        const val GROUP_SCREEN = "screen_group"
        const val BOXING_SECTION = "section_boxing-center"
        const val SWIMMING_SECTION = "section_swimming-south"
        const val JUNIORS_GROUP = "group_juniors"
        const val NEW_GROUP = "group_new-group"
        const val ATTENDANCE_VALUE = "attendance_value"
        const val SEARCH_FIELD = "sections_search"
        const val HOME_BUTTON = "home_button"
        const val SECTIONS_LIST = "sections_list"
        const val GROUPS_LIST = "groups_list"
    }
}
