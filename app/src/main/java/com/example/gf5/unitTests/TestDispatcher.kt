package com.example.gf5.unitTests

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher

/**
 * Utility object for testing coroutines.
 */
object TestDispatcher {
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
}