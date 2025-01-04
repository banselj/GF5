package com.example.gf5.unitTests

@HiltAndroidTest
class DriverStatusViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var rideRepository: RideRepository

    private lateinit var driverStatusViewModel: DriverStatusViewModel

    @Before
    fun init() {
        hiltRule.inject()
        driverStatusViewModel = DriverStatusViewModel(rideRepository)
    }

    @Test
    fun `setStatus to AVAILABLE updates status correctly`() = runTest {
        driverStatusViewModel.setStatus(DriverStatus.AVAILABLE)
        val status = driverStatusViewModel.status.value
        assertEquals(DriverStatus.AVAILABLE, status)
    }

    // Add more tests for other statuses and edge cases
}
