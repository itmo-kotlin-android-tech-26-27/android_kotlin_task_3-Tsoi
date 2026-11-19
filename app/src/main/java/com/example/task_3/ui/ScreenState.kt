package com.example.task_3.ui

import java.io.IOException
import org.json.JSONException

enum class ScreenStatus { LOADING, READY, ERROR, NOT_FOUND }

internal suspend fun loadScreen(
    onError: (Exception) -> Unit,
    block: suspend () -> Unit,
) {
    try {
        block()
    } catch (error: IOException) {
        onError(error)
    } catch (error: JSONException) {
        onError(error)
    } catch (error: IllegalArgumentException) {
        onError(error)
    }
}
