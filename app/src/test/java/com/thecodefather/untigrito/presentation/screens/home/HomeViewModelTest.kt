package com.thecodefather.untigrito.presentation.screens.home

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for HomeViewModel
 *
 * Tests the business logic and state management of the HomeViewModel.
 */
class HomeViewModelTest {
    
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel()
    }

    @Test
    fun testInitialState() = runTest {
        // Given: A freshly created ViewModel
        // When: We check the initial UI state
        val initialState = viewModel.uiState.value
        
        // Then: The state should have default values
        assertEquals(false, initialState.isLoading)
        assertEquals(null, initialState.errorMessage)
        assertEquals("Welcome to UnTigritoApp", initialState.data)
    }

    @Test
    fun testRetryFunction() = runTest {
        // Given: A ViewModel with data loaded
        val viewModelUnderTest = HomeViewModel()
        
        // When: We call retry
        viewModelUnderTest.retry()
        
        // Then: The state should still be valid
        val state = viewModelUnderTest.uiState.value
        assertEquals("Welcome to UnTigritoApp", state.data)
    }
}
