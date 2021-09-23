package com.example.norig

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.query
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@SpringBootApplication
class NorigApplication

fun main(args: Array<String>) {
    runApplication<NorigApplication>(*args)
}

@RestController()
@RequestMapping("/hello")
class HelloResource() {

    @GetMapping
    fun index(): ResponseEntity<String> = ResponseEntity.ok().body("hello")
}


@RestController
@RequestMapping("/messages")
class MessageResource(val service: MessageService) {

    @GetMapping
    fun index(): List<Message> = service.findMessages()

    @GetMapping("/{id}")
    fun index(@PathVariable id: String): Message = service.findMessagesById(id) ?: throw ResponseStatusException(
        HttpStatus.NOT_FOUND, "Message not found")

    @PostMapping
    fun post(@RequestBody message: Message) {
        service.post(message)
    }
}

@Service
class MessageService(val db: JdbcTemplate) {

    fun findMessages(): List<Message> = db.query("select * from messages") { rs, _ ->
        Message(rs.getString("id"), rs.getString("text"))
    }

    fun findMessagesById(id: String): Message? = db.query("select * from messages where id = ?", id) { rs, _ ->
        Message(rs.getString("id"), rs.getString("text"))
    }.firstOrNull()

    fun post(message: Message) {
        db.update("insert into messages values ( ?, ? )", message.id ?: message.text.uuid(), message.text)
    }
}

data class Message(val id: String?, val text: String)


// Extension method on string to generate UUID's
fun String.uuid(): String {
    return UUID.nameUUIDFromBytes(this.encodeToByteArray()).toString()
}
