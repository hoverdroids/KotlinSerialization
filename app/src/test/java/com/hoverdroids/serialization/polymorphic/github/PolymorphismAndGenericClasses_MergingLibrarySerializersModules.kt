package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#polymorphism-and-generic-classes
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#merging-library-serializers-modules
    - Generic subtypes for a serializable class require a special handling
    - If you're writing a library or shared module with an abstract class and some implementations of it, you can expose your own
      serializers module for your clients to use so that a client can combine your module with their modules
 */
class PolymorphismAndGenericClasses_MergingLibrarySerializersModules {
    @Test
    fun serialize() {
        val responseModule = SerializersModule {
            polymorphic(Response::class) {
                subclass(OkResponse.serializer(PolymorphicSerializer(Any::class)))
            }
        }

        val projectModule = SerializersModule {
            fun PolymorphicModuleBuilder<Project>.registerProjectSubclasses() {
                subclass(OwnedProject::class)
            }
            polymorphic(Any::class) { registerProjectSubclasses() }
            polymorphic(Project::class) { registerProjectSubclasses() }
        }

        val format = Json { serializersModule = (projectModule + responseModule) }

        // both Response and Project are abstract and their concrete subtypes are being serialized
        val data: Response<Project> =  OkResponse(OwnedProject("kotlinx.serialization", "kotlin"))
        val serializedData = format.encodeToString(data)
        println(serializedData)//{"type":"OkResponse","data":{"type":"OwnedProject","name":"kotlinx.serialization","owner":"kotlin"}}
        val deserializedData = format.decodeFromString<Response<Project>>(serializedData)
        println(deserializedData)
    }

    @Serializable
    abstract class Response<out T>

    @Serializable
    @SerialName("OkResponse")
    data class OkResponse<out T>(val data: T) : Response<T>()

    @Serializable
    abstract class Project {
        abstract val name: String
    }

    @Serializable
    @SerialName("OwnedProject")
    data class OwnedProject(override val name: String, val owner: String) : Project()

}