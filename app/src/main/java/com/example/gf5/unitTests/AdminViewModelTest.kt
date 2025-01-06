package com.example.gf5.unitTests

import com.example.gf5.viewModels.AdminViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AdminViewModelTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: AdminViewModel

    @Test
    fun testRefreshData(): Unit = testDispatcherRule.runBlockingTest {
        // Arrange
        viewModel = AdminViewModel(/* dependencies */)

        // Act
        viewModel.refreshData()

        // Assert
        assertTrue(viewModel.userList.value.isNotEmpty())
    }

    @Test
    fun testLogout(): Unit = testDispatcherRule.runBlockingTest {
        // Arrange
        viewModel = AdminViewModel(/* dependencies */)

        // Act
        viewModel.logout()

        // Assert
        assertTrue(viewModel.isLoggedOut.value)
    }
}