package com.example.gf5.integrationTests

import com.example.gf5.repositories.AdminRepository
import com.example.gf5.viewModels.AdminViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AdminViewModelIntegrationTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adminRepository: AdminRepository
    private lateinit var viewModel: AdminViewModel

    @Before
    fun setUp() {
        firestore = FirebaseFirestore.getInstance()
        adminRepository = AdminRepository(firestore)
        viewModel = AdminViewModel(adminRepository)
    }

    @Test
    fun testRefreshDataIntegration() = testDispatcherRule.runBlockingTest {
        // Act
        viewModel.refreshData()

        // Assert
        assertTrue(viewModel.userList.value.isNotEmpty())
    }

    @Test
    fun testLogoutIntegration() = testDispatcherRule.runBlockingTest {
        // Act
        viewModel.logout()

        // Assert
        assertTrue(viewModel.isLoggedOut.value)
    }
}