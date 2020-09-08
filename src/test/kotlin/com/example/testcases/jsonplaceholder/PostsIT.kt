package com.example.testcases.jsonplaceholder

import net.serenitybdd.junit.runners.SerenityRunner
import net.serenitybdd.markers.IsSilent
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.Performable
import net.serenitybdd.screenplay.actors.Cast
import net.serenitybdd.screenplay.rest.abilities.CallAnApi
import net.serenitybdd.screenplay.rest.interactions.Get
import net.serenitybdd.screenplay.rest.questions.ResponseConsequence.*
import org.apache.http.HttpStatus.*
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


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
        restClient.attemptsTo(SilentlyGetSomething())

    }

}

open class SilentlyGetSomething : Performable, IsSilent {
    override fun <T : Actor> performAs(actor: T) {
        actor.attemptsTo(Get.resource("/posts/2"))
        actor.should(seeThatResponse("post has userId") { it
            .statusCode(SC_OK)
            .body("userId", `is`(1))
        })

    }

}
