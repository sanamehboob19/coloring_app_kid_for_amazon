package com.nz.coloringgamesforkidsdoodle.drawingkids.painting.color_by_num_module.helper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Detects user idle state after [idleTimeoutMs] of inactivity.
 * Call [onUserInteracted] whenever a touch/input event occurs.
 * Call [start] to begin monitoring, [stop] to cancel.
 */
class IdleDetectorHelper(
    private val idleTimeoutMs: Long = 10_000L,
    private val onIdle: () -> Unit
)
{
    private var idleJob: Job? = null
    private var scope: CoroutineScope? = null

    fun start(coroutineScope: CoroutineScope) {
        scope = coroutineScope
        scheduleIdleCheck()
    }

    /** Call on every user touch/interaction to reset the 10s countdown. */
    fun onUserInteracted() {
        scheduleIdleCheck()
    }

    fun stop() {
        idleJob?.cancel()
        idleJob = null
    }

    private fun scheduleIdleCheck() {
        idleJob?.cancel()
        idleJob = scope?.launch {
            delay(idleTimeoutMs)
            onIdle()
            // No auto re-arm here — caller decides when to restart via onUserInteracted()
        }
    }
}