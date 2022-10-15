package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#objects
    - An object serializes as an empty class, also using its fully-qualified class name as type by default
    - Even if object has properties, they are not serialized
 */
class Objects {
    @Test
    fun serialize_subclass_serial_name() {
        val list = listOf(EmptyResponse, TextResponse("OK"))
        val serializedData = Json.encodeToString(list)
        println(serializedData)//[{"type":"example.examplePoly08.EmptyResponse"},{"type":"example.examplePoly08.TextResponse","text":"OK"}]
    }

    @Serializable
    sealed class Response

    @Serializable
    object EmptyResponse : Response()//<-- Notice the subclass is an object

    @Serializable
    class TextResponse(val text: String) : Response()
}