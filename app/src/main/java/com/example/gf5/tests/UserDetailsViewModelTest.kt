package com.example.gf5.tests

// Example Unit Test for UserDetailsViewModel
@RunWith(MockitoJUnitRunner::class)
class UserDetailsViewModelTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: UserDetailsViewModel

    @Before
    fun setup() {
        viewModel = UserDetailsViewModel(userRepository)
    }

    @Test
    fun `fetchUserDetails returns user`() = runBlocking {
        val userId = "user123"
        val expectedUser = User(id = userId, name = "John Doe", email = "john.doe@example.com", role = "admin")

        Mockito.`when`(userRepository.getUserById(userId)).thenReturn(expectedUser)

        viewModel.fetchUserDetails(userId)

        assertEquals(expectedUser, viewModel.userDetails.value)
    }

    @Test(expected = Exception::class)
    fun `fetchUserDetails throws exception when user not found`() = runBlocking {
        val userId = "nonexistent"

        Mockito.`when`(userRepository.getUserById(userId)).thenThrow(Exception("User not found."))

        viewModel.fetchUserDetails(userId)
    }
}
