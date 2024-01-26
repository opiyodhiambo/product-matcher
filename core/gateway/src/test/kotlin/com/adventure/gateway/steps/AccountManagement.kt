package com.adventure.gateway.steps

import com.adventure.apis.accounts.Requests.*
import com.adventure.gateway.controller.Account
import com.adventure.gateway.utils.Mappings.ACCOUNT_CREATION_MAPPING
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.When
import io.cucumber.java.en.Then
import io.cucumber.java.en.And
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import java.time.LocalDate

@WebFluxTest(controllers = [Account::class])
class AccountManagement {
    @Autowired
    private lateinit var webTestClient: WebTestClient
    @MockBean
    private lateinit var commandGateway: ReactorCommandGateway
    private var createAccountRequest: CreateAccountRequest? = null
    private var responseMessage: String = ""

    @When("a user sends a requests to create an account with the following details:")
    fun aUserSendsARequestsToCreateAnAccountWithTheFollowingDetails(details: DataTable) {
        val rows = details.asMaps(String::class.java, String::class.java)
        for (column: Map<String, String> in rows) {
            createAccountRequest = CreateAccountRequest(
                firstName = column["first_name"]!!,
                lastName = column["last_name"]!!,
                dob = LocalDate.parse(column["date_of_birth"]!!),
                email = column["email_address"]!!,
                gender = column["gender"]!!,
                country = column["country"]!!,
                role = column["role"]!!,
            )
        }
    }

    @Then("the request should be successful")
    fun theRequestShouldBeSuccessful() {
        webTestClient.post()
            .uri(ACCOUNT_CREATION_MAPPING)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(createAccountRequest!!))
            .exchange()
            .expectBody(String::class.java)
            .consumeWith { response ->
                responseMessage = response.responseBody!!
            }
    }
    @And("the response body should contain the message {string}")
    fun theResponseBodyShouldContainTheMessage(expectedMessage: String) {
        assertEquals(responseMessage, expectedMessage)
    }
}