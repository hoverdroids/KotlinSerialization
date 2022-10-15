package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#registering-multiple-superclasses
    - When the same class gets serialized as a value of properties with different compile-time type from the list of its superclasses,
     we must register it in the SerializersModule for each of its superclasses separately
 */
class RegisteringMultipleSuperclasses {
    @Test
    fun serialize() {
        val module = SerializersModule {
            fun PolymorphicModuleBuilder<Project>.registerProjectSubclasses() {//Registering OwnedProject for both Any and Project
                subclass(OwnedProject::class)
            }
            polymorphic(Any::class) { registerProjectSubclasses() }
            polymorphic(Project::class) { registerProjectSubclasses() }
        }

        val format = Json { serializersModule = module }

        val data = Data(OwnedProject("kotlinx.coroutines", "kotlin"))
        //        ^compile time type not specified because it's same as run time
        val serializedData = format.encodeToString(data)
        println(serializedData)//{"project":{"type":"owned","name":"kotlinx.coroutines","owner":"kotlin"}}
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