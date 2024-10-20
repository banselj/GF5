package com.example.gf5.tests

@RunWith(MockitoJUnitRunner::class)
class RegistrationViewModelTest {

    @Mock
    private lateinit var auth: FirebaseAuth

    @Mock
    private lateinit var db: FirebaseFirestore

    @Mock
    private lateinit var documentReference: DocumentReference

    @Mock
    private lateinit var task: Task<Void>

    private lateinit var registrationViewModel: RegistrationViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(auth.createUserWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(Tasks.forResult(MockAuthResult()))
        Mockito.`when`(db.collection("users")).thenReturn(mockCollectionReference)
        Mockito.`when`(mockCollectionReference.document(anyString())).thenReturn(documentReference)
        Mockito.`when`(documentReference.set(anyMap())).thenReturn(Tasks.forResult(null))
        registrationViewModel = RegistrationViewModel(auth, db)
    }

    @Test
    fun `registerUser success updates registrationState to Success`() = runBlocking {
        registrationViewModel.registerUser("test@example.com", "Password1!")
        assert(registrationViewModel.registrationState.value is RegistrationViewModel.RegistrationState.Success)
    }

    @Test
    fun `registerUser failure updates registrationState to Error`() = runBlocking {
        Mockito.`when`(auth.createUserWithEmailAndPassword(anyString(), anyString()))
            .thenReturn(Tasks.forException(Exception("Registration Error")))
        registrationViewModel.registerUser("test@example.com", "Password1!")
        assert(registrationViewModel.registrationState.value is RegistrationViewModel.RegistrationState.Error)
    }

    // Mock classes and helper methods
    private val mockCollectionReference: CollectionReference = mock()
    private class MockAuthResult : AuthResult {
        override fun getAdditionalUserInfo(): AdditionalUserInfo? = null
        override fun getUser(): FirebaseUser? = mock()
    }
}
