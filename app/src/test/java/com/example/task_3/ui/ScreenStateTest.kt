package com.example.task_3.ui

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class ScreenStateTest {
    @Test
    fun cancellationIsPropagatedInsteadOfBecomingAnErrorState() = runTest {
        val cancellation = CancellationException("Screen was closed")
        var displayedError: Exception? = null

        try {
            loadScreen(onError = { displayedError = it }) { throw cancellation }
            fail("Cancellation must propagate to the coroutine")
        } catch (error: CancellationException) {
            assertSame(cancellation, error)
        }

        assertNull(displayedError)
    }
}
