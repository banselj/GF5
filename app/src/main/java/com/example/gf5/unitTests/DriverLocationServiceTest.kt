package com.example.gf5.unitTests

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DriverLocationServiceTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var firestore: FirebaseFirestore

    private lateinit var service: DriverLocationService

    @Before
    fun setup() {
        hiltRule.inject()
        service = DriverLocationService()
    }

    @Test
    fun testDriverIdRetrieval() {
        // Mock authenticated user
        val mockUser = mock(FirebaseUser::class.java).apply {
            whenever(uid).thenReturn("driver123")
        }
        whenever(auth.currentUser).thenReturn(mockUser)

        assertEquals("driver123", service.getCurrentDriverId())
    }

    @Test
    fun testUpdateLocationInFirestore() = runBlocking {
        // Setup
        val location = Location("provider").apply {
            latitude = 37.4219983
            longitude = -122.084
        }

        // Mock Firestore updates
        whenever(firestore.collection("drivers")).thenReturn(mockCollection)
        whenever(firestore.collection("vehicles")).thenReturn(mockCollection)
        whenever(mockCollection.document("driver123")).thenReturn(mockDocument)
        whenever(mockDocument.update(anyMap())).thenReturn(mockTask)

        // Execute
        service.updateLocationInFirestore(location)

        // Verify
        verify(mockDocument).update(mapOf(
            "latitude" to 37.4219983,
            "longitude" to -122.084,
            "timestamp" to any<Long>()
        ))
    }

    // Mock classes and helper methods
    private val mockCollection: CollectionReference = mock()
    private val mockDocument: DocumentReference = mock()
    private val mockTask: Task<Void> = Tasks.forResult(null)
}
