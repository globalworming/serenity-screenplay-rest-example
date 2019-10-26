package com.example.testcases.jsonplaceholder

import com.example.screenplay.CompromisedTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import net.serenitybdd.core.Serenity
import net.serenitybdd.core.exceptions.TestCompromisedException
import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.rest.SerenityRest
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.actors.Cast
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Get
import net.serenitybdd.screenplay.rest.interactions.Patch
import net.serenitybdd.screenplay.rest.interactions.Post
import net.serenitybdd.screenplay.rest.interactions.Put
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse
import org.apache.http.HttpStatus
import org.apache.http.HttpStatus.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.greaterThan
import kotlin.AssertionError


@RunWith(SerenityRunner::class)
class PostsIT {

    lateinit var restClient : Actor

    @Before
    fun setUp() {
        val cast = Cast.whereEveryoneCan(CallAnApi.at("https://jsonplaceholder.typicode.com"))
        restClient = cast.actorNamed("rest client")
    }

    @Test
    fun `when getting a post, then then the author id is attached`() {
        restClient.attemptsTo(Get.resource("/posts/1"))
        restClient.should(seeThatResponse("post has userId") { it
                .statusCode(SC_OK)
                .body("userId", `is`(1))
        })
    }

    @Test
    fun `when getting all posts, there should be more than 99 ones`() {
        restClient.attemptsTo(Get.resource("/posts"))
        restClient.should(seeThatResponse("a lot of posts are returned") { it
                .statusCode(SC_OK)
                .body("size()", greaterThan(99))
        })
        Serenity.recordReportData().withTitle("number of posts")
                .andContents(SerenityRest.lastResponse().jsonPath().getList<Any>("").size.toString())
    }

    @Test
    fun `when getting all posts from a single user`() {
        restClient.attemptsTo(Get.resource("/posts").with {
            it.queryParam("userId", 1) })
        restClient.should(seeThatResponse("some posts are returned") { it
                .statusCode(SC_OK)
                .body("size()", greaterThan(9))
        })
    }

    @Test
    fun `when adding a post`() {
        restClient.attemptsTo(Post.to("/posts").with {
            it.body(this.javaClass.getResourceAsStream("post.json")) })
        restClient.should(seeThatResponse("a lot of posts are returned") { it
                .statusCode(SC_CREATED)
                .body("id", `is`(101))
        })
    }

    @Test
    fun `when updating a post`() {
        restClient.attemptsTo(Patch.to("/posts/1").with {
            it.contentType(ContentType.JSON).body("{ \"title\" : \"newTitle\"}") })
        restClient.should(seeThatResponse("title was updated") { it
                .statusCode(SC_OK)
                .body("title", `is`("newTitle"))
        })
    }


}
