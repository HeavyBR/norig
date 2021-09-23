package com.example.norig

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpStatus
import kotlin.random.Random

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.datasource.url=jdbc:h2:mem:testdb"
    ]
)
class NorigApplicationTests(@Autowired val client: TestRestTemplate) {

    @Test
    fun `test hello endpoint`() {
        val entity = client.getForEntity<String>("/hello")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isEqualTo("hello")
    }

    @Test
    fun `should create message with success`() {
        val id = "${Random.nextInt()}".uuid()
        val message = Message(id, "some message")
        client.postForObject<Message>("/messages", message)

        val entity = client.getForEntity<String>("/messages/$id")
        val msg = client.getForObject<Message>("/messages/$id")!!
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(msg.id).isEqualTo(message.id)
        assertThat(msg.text).isEqualTo(message.text)
    }

    @Test
    fun `should return not found on invalid ID`() {
        val id = "${Random.nextInt()}".uuid()
        val entity = client.getForEntity<String>("/messages/$id")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }
}
