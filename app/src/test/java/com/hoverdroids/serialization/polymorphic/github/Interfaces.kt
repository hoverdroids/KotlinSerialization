package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#serializing-interfaces
    - we cannot mark an interface itself as @Serializable

    - Interfaces are used in the Kotlin language to enable polymorphism,so all interfaces
      are considered to be implicitly serializable with the PolymorphicSerializer strategy

    - Just need to mark their implementing classes as @Serializable and register them

    - When using an interface as a property in another serializable class, serialization with type works at runtime
      as long as we've registered the actual subtype of the interface that is being serialized in the SerializersModule
      of our format, we get it working at runtime
 */
class Interfaces {
    @Test
    fun serialize_interface() {
        val module = SerializersModule {
            polymorphic(Project::class) {
                subclass(OwnedProject::class)
            }
        }

        val format = Json { serializersModule = module }//even though project is interface, still need a custom formatter

        val data: Project = OwnedProject("kotlinx.coroutines", "kotlin")//and still need to specify the compile time type
        val serializedData = format.encodeToString(data)
        println(serializedData)//{"type":"owned","name":"kotlinx.coroutines","owner":"kotlin"}

        val datasData = Data(OwnedProject("kotlinx.coroutines", "kotlin"))
        //            ^no compile time type
        val serializedDatasData = format.encodeToString(data)
        println(serializedDatasData)//{"project":{"type":"owned","name":"kotlinx.coroutines","owner":"kotlin"}}
    }

    interface Project {
        val name: String
    }

    @Serializable
    @SerialName("owned")
    class OwnedProject(override val name: String, val owner: String) : Project

    @Serializable
    class Data(val project: Project) // Project is an interface, and used as a field in another serializable class
}