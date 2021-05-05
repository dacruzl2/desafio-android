package com.picpay.desafio.android.rest_picpay

import com.google.common.truth.Truth.assertThat
import com.picpay.desafio.android.rest_picpay.repository.UserRepositoryImpl
import com.picpay.desafio.android.rest_picpay.util.UserRepositoryRule
import com.picpay.desafio.android.rest_picpay.util.loadResource
import com.picpay.desafio.android.domain.errors.RemoteServiceError
import com.picpay.desafio.android.domain.models.User
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

fun unwrapCaughtError(result: Result<*>) =
    result.exceptionOrNull()
        ?.let { it }
        ?: throw IllegalArgumentException("Not an error")

fun assertErrorTransformed(expected: Throwable, whenRunning: () -> Any) {
    val result = runCatching { whenRunning.invoke() }
    val unwrapped = unwrapCaughtError(result)
    assertThat(unwrapped).isEqualTo(expected)
}

internal class UserRepositoryTests {

    @get:Rule
    val rule = UserRepositoryRule()

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun `before each test`() {
        repository = UserRepositoryImpl(rule.api)
    }

    @Test
    fun `should handle no results properly`() {

        rule.defineScenario(
            status = 200,
            response = loadResource("200_no_results.json")
        )

        val noUsers = emptyList<User>()
        assertThat(simpleSearch()).isEqualTo(noUsers)
    }

    @Test
    fun `should handle client issue`() {

        rule.defineScenario(
            status = 404,
            response = loadResource("404_wrong_path.json")
        )

        assertThat(RemoteServiceError.ClientOrigin)

        assertErrorTransformed(
            whenRunning = this::simpleSearch,
            expected = RemoteServiceError.ClientOrigin
        )
    }

    @Test
    fun `should handle remote system issue`() {

        rule.defineScenario(
            status = 500
        )

        assertErrorTransformed(
            whenRunning = this::simpleSearch,
            expected = RemoteServiceError.RemoteSystem
        )
    }

    @Test
    fun `should handle broken contract`() {

        rule.defineScenario(
            status = 200,
            response = loadResource("200_list_users_broken_contract.json")
        )

        assertErrorTransformed(
            whenRunning = this::simpleSearch,
            expected = RemoteServiceError.UnexpectedResponse
        )
    }

    @Test
    fun `should fetch users`() {

        rule.defineScenario(
            status = 200,
            response = loadResource("200_list_users.json")
        )

        val expected = listOf(
            User(
                id = 1001,
                username = "@leonardo",
                name = "Leonardo Cruz",
                img = "https://randomuser.me/api/portraits/men/9.jpg"
            ),
            User(
                id = 1002,
                username = "@simone",
                name = "Simone Cruz",
                img = "https://randomuser.me/api/portraits/women/37.jpg"
            )
        )

        val remoteUsers = runBlocking {
            repository.getUsers()
        }

        assertThat(remoteUsers).isEqualTo(expected)
    }

    private fun simpleSearch() = runBlocking {
        repository.getUsers()
    }
}