package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#explicitly-marking-polymorphic-class-properties
    - The property of an interface type is implicitly considered polymorphic

    - Kotlin Serialization does not compile a serializable class with a property of a non-serializable class type
 */
class ExplicityMarkingPolymorphicProperties {
    @Test
    fun serialize() {
        val module = SerializersModule {
            polymorphic(Any::class) { //3. Must indicate the strategy for serializing any
                subclass(OwnedProject::class) //4. The type to serialize as, if the backing property is OwnedProject
            }                                 //5. Otherwise, does it not serialize?
        }

        val format = Json { serializersModule = module }

        val data = Data(OwnedProject("kotlinx.coroutines", "kotlin"))
        //        ^compile time type not specified because it's same as run time
        val serializedData = format.encodeToString(data)//6. This will serialize properly
        println(serializedData)//{"project":{"type":"owned","name":"kotlinx.coroutines","owner":"kotlin"}}

        val data2 = Data("string")
        val serializedData2 = format.encodeToString(data2)//7. Throws error when trying to serialize: Class 'String' is not registered for polymorphic serialization in the scope of 'Any'.
        println(serializedData2)
    }

    interface Project {
        val name: String
    }

    @Serializable
    @SerialName("owned")
    class OwnedProject(override val name: String, val owner: String) : Project

    @Serializable
    class Data(
        @Polymorphic    // 1. the code does not compile without it
        val project: Any// 2. because Any is a non-serializable class type
    )
}