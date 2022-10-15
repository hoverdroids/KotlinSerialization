package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

//https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#custom-subclass-serial-name
class CustomSubclassSerialName {
    @Test
    fun serialize_subclass_serial_name() {
        val data: Project = OwnedProject("kotlinx.coroutines", "kotlin")
        //        ^compiled vs  ^run ... polymorphic classes must be serialized with the compile time type specified
        val serializedData = Json.encodeToString(data)
        println(serializedData)//{"type":"owned","name":"kotlinx.coroutines","owner":"kotlin"}
    }

    @Serializable
    sealed class Project {//<-- notice this is sealed
        abstract val name: String
    }

    @Serializable
    @SerialName("owned")//<-- notice serial name, which replaces "OwnedProject" as type when serialized
    class OwnedProject(override val name: String, val owner: String) : Project()
}