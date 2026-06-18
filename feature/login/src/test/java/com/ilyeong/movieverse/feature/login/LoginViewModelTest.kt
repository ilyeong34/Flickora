package com.ilyeong.movieverse.feature.login

import com.ilyeong.movieverse.core.data.oauth.repository.OAuthRepository
import com.ilyeong.movieverse.core.model.RequestToken
import com.ilyeong.movieverse.feature.login.model.LoginEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun automaticallyLogin_whenAuthenticated_navigatesToMain() = runTest {
        val repository = FakeOAuthRepository(isAuthenticatedResult = true)
        val viewModel = LoginViewModel(repository)
        val event = async { viewModel.events.first() }

        viewModel.automaticallyLogin()
        advanceUntilIdle()

        assertEquals(LoginEvent.NavigateToMain, event.await())
        assertFalse(viewModel.shouldShowLoginUi)
    }

    @Test
    fun automaticallyLogin_whenNotAuthenticated_showsLoginUi() = runTest {
        val repository = FakeOAuthRepository(isAuthenticatedResult = false)
        val viewModel = LoginViewModel(repository)

        viewModel.automaticallyLogin()
        advanceUntilIdle()

        assertTrue(viewModel.shouldShowLoginUi)
    }

    @Test
    fun continueAsGuest_navigatesToMain_andStopsLoading() = runTest {
        val repository = FakeOAuthRepository()
        val viewModel = LoginViewModel(repository)
        val event = async { viewModel.events.first() }

        viewModel.continueAsGuest()
        advanceUntilIdle()

        assertEquals(LoginEvent.NavigateToMain, event.await())
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(1, repository.continueAsGuestCalls)
    }

    private class FakeOAuthRepository(
        private val isAuthenticatedResult: Boolean = false
    ) : OAuthRepository {
        var continueAsGuestCalls = 0

        override fun isAuthenticated(): Flow<Boolean> = flowOf(isAuthenticatedResult)

        override fun createRequestToken(): Flow<RequestToken> = flow<RequestToken> {
            error("createRequestToken is not used in this test")
        }

        override fun createSessionId(requestToken: String): Flow<Unit> = flow {
            emit(Unit)
        }

        override fun logout(): Flow<Unit> = flow {
            emit(Unit)
        }

        override fun continueAsGuest(): Flow<Unit> = flow {
            continueAsGuestCalls++
            emit(Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    class MainDispatcherRule(
        private val dispatcher: TestDispatcher = StandardTestDispatcher()
    ) : TestWatcher() {

        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}
