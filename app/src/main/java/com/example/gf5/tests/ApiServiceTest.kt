package com.example.gf5.tests

@RunWith(JUnit4::class)
class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `loginUser returns UserResponse on successful login`() = runBlocking {
        // Prepare mock response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "userId": "123",
                    "userName": "John Doe",
                    "userEmail": "john.doe@example.com",
                    "phoneNumber": "1234567890",
                    "role": "admin"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // Call the API
        val response = apiService.loginUser(LoginRequest("john.doe@example.com", "password123"))

        // Assert
        assertTrue(response.isSuccessful)
        val userResponse = response.body()
        assertNotNull(userResponse)
        assertEquals("123", userResponse?.userId)
        assertEquals("John Doe", userResponse?.userName)
        assertEquals("john.doe@example.com", userResponse?.userEmail)
        assertEquals("1234567890", userResponse?.phoneNumber)
        assertEquals("admin", userResponse?.role)
    }
}
