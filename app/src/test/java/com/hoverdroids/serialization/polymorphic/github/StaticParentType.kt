package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.PolymorphicSerializer
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
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#static-parent-type-lookup-for-polymorphism
    - Any is a class and it is not serializable

    - must to explicitly pass an instance of PolymorphicSerializer for the base class
      Any as the first parameter to the encodeToString function
 */
class StaticParentType {
    @Test
    fun serialize_static_type_with_error() {
        val module = SerializersModule {
            polymorphic(Project::class) {
                subclass(OwnedProject::class)
            }
        }

        val format = Json { serializersModule = module }//even though project is interface, still need a custom formatter

        val data: Any = OwnedProject("kotlinx.coroutines", "kotlin")
        //        ^notice compile time type is Any
        val serializedData = format.encodeToString(data)
        println(serializedData)
        // Exception in thread "main" kotlinx.serialization.SerializationException: Serializer for class 'Any' is not found.
        // Mark the class as @Serializable or provide the serializer explicitly.
    }

    @Test
    fun serialize_static_type_without_error() {
        val module = SerializersModule {
            polymorphic(Any::class) {//<-- must use Any, not the interface
                subclass(OwnedProject::class)
            }
        }

        val format = Json { serializersModule = module }//even though project is interface, still need a custom formatter

        val data: Any = OwnedProject("kotlinx.coroutines", "kotlin")
        //        ^notice compile time type is Any
        val serializedData = format.encodeToString(PolymorphicSerializer(Any::class), data)
        //                                          ^ must explicitly pass
        println(serializedData)
    }

    interface Project {
        val name: String
    }

    @Serializable
    @SerialName("owned")
    class OwnedProject(override val name: String, val owner: String) : Project
}