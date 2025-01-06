package com.example.gf5.unitTests

import com.example.gf5.repositories.UserRepository
import com.example.gf5.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var repository: UserRepository

    @Test
    fun testGetUsers() = testDispatcherRule.runBlockingTest {
        // Arrange
        repository = UserRepository(/* dependencies */)

        // Act
        val users = repository.getUsers()

        // Assert
        assertTrue(users.isNotEmpty())
    }
}