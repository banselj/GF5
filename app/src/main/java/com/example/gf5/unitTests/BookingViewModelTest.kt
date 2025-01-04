package com.example.gf5.unitTests

@RunWith(MockitoJUnitRunner::class)
class BookingViewModelTest {

    @Mock
    private lateinit var rideRepository: RideRepository

    private lateinit var bookingViewModel: BookingViewModel

    @Before
    fun setUp() {
        bookingViewModel = BookingViewModel(rideRepository)
    }

    @Test
    fun `submitRideRequest success updates bookingStatus to Success`() = runBlockingTest {
        // Given
        val rideDetails = BookingViewModel.RideDetails("ride123", "Requested", "Location A", "Location B")
        whenever(rideRepository.requestRide("Location A", "Location B")).thenReturn(rideDetails)

        // When
        bookingViewModel.submitRideRequest("Location A", "Location B")

        // Then
        assertEquals(BookingViewModel.BookingStatus.Success(rideDetails), bookingViewModel.bookingStatus.value)
    }

    @Test
    fun `submitRideRequest failure updates bookingStatus to Error`() = runBlockingTest {
        // Given
        whenever(rideRepository.requestRide("Location A", "Location B")).thenThrow(RuntimeException("Network Error"))

        // When
        bookingViewModel.submitRideRequest("Location A", "Location B")

        // Then
        assertTrue(bookingViewModel.bookingStatus.value is BookingViewModel.BookingStatus.Error)
        assertEquals("Network Error", (bookingViewModel.bookingStatus.value as BookingViewModel.BookingStatus.Error).message)
    }
}
