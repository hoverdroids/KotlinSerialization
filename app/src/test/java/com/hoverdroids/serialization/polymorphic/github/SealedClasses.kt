package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#sealed-classes
    Setting the compile time type to the polymorphic base type is required to include the type in the serialized output
*/
class SerializingPolymorphicSealedClasses {
    @Test
    fun serialize_sealed_class() {
        val dataWithCompileTimeType: Project = OwnedProject("kotlinx.coroutines", "kotlin")
        val serializedDataWithType = Json.encodeToString(dataWithCompileTimeType)
        println(serializedDataWithType)//{"type":"example.examplePoly04.OwnedProject","name":"kotlinx.coroutines","owner":"kotlin"}

        val dataWithoutCompileTimeType = OwnedProject("kotlinx.coroutines", "kotlin")
        val serializedDataWithoutType = Json.encodeToString(dataWithoutCompileTimeType)
        println(serializedDataWithoutType)//{"name":"kotlinx.coroutines","owner":"kotlin"}
    }
}

@Serializable
sealed class Project {
    abstract val name: String
}

@Serializable
class OwnedProject(override val name: String, val owner: String) : Project()