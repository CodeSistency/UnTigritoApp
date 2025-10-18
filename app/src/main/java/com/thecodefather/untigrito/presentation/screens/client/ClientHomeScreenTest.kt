package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.ServicePosting
import com.thecodefather.untigrito.presentation.viewmodel.ClientHomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for ClientHomeScreen
 * Focuses on: balance display, service loading, state transitions
 */
@RunWith(AndroidJUnit4::class)
class ClientHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: ClientHomeViewModel
    private val mockRepository = mockk()

    @Before
    fun setup() {
        viewModel = ClientHomeViewModel(mockRepository)
    }

    @Test
    fun testHomeScreenDisplaysUserBalance() {
        // Given
        val testUser = ClientUser(
            id = "test-user",
            name = "Test User",
            balance = 250.50
        )
        coEvery { mockRepository.getUserById("current_user_id") } returns flowOf(testUser)

        // When
        composeTestRule.setContent {
            ClientHomeScreen(
                navController = mockk(),
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Mi Balance").assertExists()
        composeTestRule.onNodeWithText("250.50").assertExists()
    }

    @Test
    fun testCategoriesDisplayed() {
        // Given
        val testUser = ClientUser(id = "test", balance = 100.0)
        coEvery { mockRepository.getUserById("current_user_id") } returns flowOf(testUser)

        // When
        composeTestRule.setContent {
            ClientHomeScreen(
                navController = mockk(),
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Plomería").assertExists()
        composeTestRule.onNodeWithText("Electricidad").assertExists()
        composeTestRule.onNodeWithText("Albañilería").assertExists()
    }

    @Test
    fun testServicesListDisplayed() {
        // Given
        val testServices = listOf(
            ServicePosting(
                id = "1",
                clientId = "client1",
                title = "Reparación de tubería",
                description = "Se necesita reparar tubería rota",
                category = ServicePosting.CATEGORY_PLOMERIA,
                budget = 500.0,
                status = ServicePosting.STATUS_OPEN
            ),
            ServicePosting(
                id = "2",
                clientId = "client2",
                title = "Instalación eléctrica",
                description = "Instalar puntos de energía",
                category = ServicePosting.CATEGORY_ELECTRICIDAD,
                budget = 800.0,
                status = ServicePosting.STATUS_OPEN
            )
        )
        
        coEvery { mockRepository.getServicePostingsByStatus(ServicePosting.STATUS_OPEN) } returns flowOf(testServices)

        // When
        composeTestRule.setContent {
            ClientHomeScreen(
                navController = mockk(),
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Reparación de tubería").assertExists()
        composeTestRule.onNodeWithText("Instalación eléctrica").assertExists()
    }

    @Test
    fun testEmptyServicesState() {
        // Given
        coEvery { mockRepository.getServicePostingsByStatus(ServicePosting.STATUS_OPEN) } returns flowOf(emptyList())
        coEvery { mockRepository.getUserById("current_user_id") } returns flowOf(ClientUser(id = "test"))

        // When
        composeTestRule.setContent {
            ClientHomeScreen(
                navController = mockk(),
                viewModel = viewModel
            )
        }

        // Then
        composeTestRule.onNodeWithText("Servicios Disponibles").assertExists()
    }

    @Test
    fun testNavigationFABButton() {
        // Given
        coEvery { mockRepository.getUserById("current_user_id") } returns flowOf(ClientUser(id = "test"))

        // When
        composeTestRule.setContent {
            ClientHomeScreen(
                navController = mockk(),
                viewModel = viewModel
            )
        }

        // Then
        // FAB button should exist (Icon with contentDescription "Create Service")
        composeTestRule.onNodeWithText("Create Service").assertExists()
    }
}
