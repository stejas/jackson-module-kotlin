package com.fasterxml.jackson.module.kotlin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Ignore
import org.junit.Test


class IteratorTests {
    class TinyPerson(val name: String, val age: Int)
    class KotlinPersonIterator(private val personList: List<TinyPerson>) : Iterator<TinyPerson> by personList.iterator() {}

    val mapper: ObjectMapper = jacksonObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, false)

    Test fun testKotlinIterator() {
        val expected = """[{"name":"Fred","age":10},{"name":"Max","age":11}]"""
        val people = KotlinPersonIterator(listOf(TinyPerson("Fred", 10), TinyPerson("Max", 11)))
        val kotlinJson = mapper.writerFor<ObjectWriter>(object : TypeReference<Iterator<TinyPerson>>() {}).writeValueAsString(people)
        assertThat(kotlinJson, equalTo(expected))
    }

    Ignore("Failing, but need change in Jackson to allow this to work.")
    Test fun testKotlinIteratorFails() {
        val expected = """[{"name":"Fred","age":10},{"name":"Max","age":11}]"""
        val people = KotlinPersonIterator(listOf(TinyPerson("Fred", 10), TinyPerson("Max", 11)))
        val kotlinJson = mapper.writeValueAsString(people)
        assertThat(kotlinJson, equalTo(expected))
    }

    class Company(val name: String, [JsonSerialize(`as` = javaClass<java.util.Iterator<TinyPerson>>())] val people: KotlinPersonIterator)

    Test fun testKotlinIteratorAsField() {
        val expected = """{"name":"KidVille","people":[{"name":"Fred","age":10},{"name":"Max","age":11}]}"""
        val people = KotlinPersonIterator(listOf(TinyPerson("Fred", 10), TinyPerson("Max", 11)))
        val company = Company("KidVille", people)
        val kotlinJson = mapper.writeValueAsString(company)
        assertThat(kotlinJson, equalTo(expected))
    }

}