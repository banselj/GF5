package com.example.gf5.tests

@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var authResult: AuthResult

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        Mockito.`when`(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(Tasks.forResult(authResult))
        Mockito.`when`(authResult.user).thenReturn(firebaseUser)
        authViewModel = AuthViewModel(firebaseAuth)
    }

    @Test
    fun `loginUser successful login updates authState to Success`() = runBlocking {
        authViewModel.loginUser("test@example.com", "password123")
        val state = authViewModel.authState.first()
        assert(state is AuthViewModel.AuthState.Success)
        assert((state as AuthViewModel.AuthState.Success).user == firebaseUser)
    }

    @Test
    fun `loginUser failed login updates authState to Error`() = runBlocking {
        val exception = FirebaseAuthInvalidCredentialsException("invalid", "Invalid credentials")
        Mockito.`when`(firebaseAuth.signInWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(Tasks.forException(exception))

        authViewModel.loginUser("test@example.com", "wrongpassword")
        val state = authViewModel.authState.first()
        assert(state is AuthViewModel.AuthState.Error)
        assert((state as AuthViewModel.AuthState.Error).message == "Login failed. Please check your credentials and try again.")
    }
}
