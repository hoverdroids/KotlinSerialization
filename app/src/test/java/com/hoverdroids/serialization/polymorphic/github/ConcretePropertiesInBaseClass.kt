package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#concrete-properties-in-a-base-class
    - A base class in a sealed hierarchy can have properties with backing fields
    - The properties of the superclass are serialized before the properties of the subclass
 */
class ConcretePropertiesInBaseClass {
    @Test
    fun serialize_subclass_serial_name() {
        val json = Json { encodeDefaults = true } // "status" will be skipped otherwise
        val data: Project = OwnedProject("kotlinx.coroutines", "kotlin")
        val serializedData = json.encodeToString(data)
        println(serializedData)//{"type":"owned","status":"open","name":"kotlinx.coroutines","owner":"kotlin"}
    }

    @Serializable
    sealed class Project {
        abstract val name: String
        var status = "open"//<- defaults are not serialized unless specified in Json config
    }

    @Serializable
    @SerialName("owned")
    class OwnedProject(override val name: String, val owner: String) : Project()
}