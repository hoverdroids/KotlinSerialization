package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#default-polymorphic-type-handler-for-deserialization
    - What happens when we deserialize a subclass that was not registered?
      Exception in thread "main" kotlinx.serialization.json.internal.JsonDecodingException: Polymorphic serializer was not found for class discriminator 'unknown'
 */
class DefaultPolymorphicTypeHandlerForDeserialization {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun serialize() {
        // In the below example we don't use the type, but always return the Plugin-generated serializer of the BasicProject class
        val module = SerializersModule {
            polymorphic(Project::class) {
                subclass(OwnedProject::class)
                defaultDeserializer { BasicProject.serializer() }
            }
        }

        //Using this module we can now deserialize both instances of the registered OwnedProject and any unregistered one
        val format = Json { serializersModule = module }

        println(format.decodeFromString<List<Project>>("""
        [
            {"type":"unknown","name":"example"},
            {"type":"OwnedProject","name":"kotlinx.serialization","owner":"kotlin"} 
        ]
        """))
    }

    @Serializable
    abstract class Project {
        abstract val name: String
    }

    @Serializable
    data class BasicProject(override val name: String, val type: String): Project()

    @Serializable
    @SerialName("OwnedProject")
    data class OwnedProject(override val name: String, val owner: String) : Project()
}