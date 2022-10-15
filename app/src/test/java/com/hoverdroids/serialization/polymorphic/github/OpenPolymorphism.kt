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
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#open-polymorphism
    - Serialization can work with arbitrary open classes or abstract classes. However, since this kind of polymorphism
    is open, there is a possibility that subclasses are defined anywhere in the source code, even in other modules, the
    list of subclasses that are serialized cannot be determined at compile-time and must be explicitly registered at runtime

    - Let us start with the code from the Designing serializable hierarchy section. To make it work with serialization without
    making it sealed, we have to define a SerializersModule using the SerializersModule {} builder function
 */
class OpenPolymorphism {
    @Test
    fun serialize_subclass_serial_name() {
        val module = SerializersModule {        //Module to specify serialization
            polymorphic(Project::class) {//of this polymorphic class
                subclass(OwnedProject::class)                           //which is subclassed by this class
            }
        }
        val format = Json { serializersModule = module }                //Then use a customized JSON formatter

        val data: Project = OwnedProject("kotlinx.coroutines", "kotlin")    //Still need to use compile time type
        val serializedData = format.encodeToString(data)
        //                   ^use the custom formatter, not the default
        println(serializedData)//{"type":"owned","name":"kotlinx.coroutines","owner":"kotlin"}
    }

    @Serializable
    abstract class Project {//<- abstract, not sealed
        abstract val name: String
    }

    @Serializable
    @SerialName("owned")
    class OwnedProject(
        override val name: String,
        val owner: String
    ) : Project()
}