package com.hoverdroids.serialization.polymorphic.github

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*
import org.junit.Test

/*
!!! NOTICE !!!
    https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#default-polymorphic-type-handler-for-deserialization
    - What happens when we deserialize a subclass that was not registered?
      Exception in thread "main" kotlinx.serialization.json.internal.JsonDecodingException: Polymorphic serializer was not found for class discriminator 'unknown'
 */
class DefaultPolymorphicTypeHandlerForSerialization {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun serialize() {
        val module = SerializersModule {
            polymorphicDefaultSerializer(Animal::class) { instance ->
                @Suppress("UNCHECKED_CAST")
                when (instance) {
                    is Cat -> CatSerializer as SerializationStrategy<Animal>
                    is Dog -> DogSerializer as SerializationStrategy<Animal>
                    else -> null
                }
            }
        }

        val format = Json { serializersModule = module }

        println(format.encodeToString<Animal>(AnimalProvider.createCat()))//{"type":"Cat","catType":"Tabby"}
    }

    interface Animal {
    }

    interface Cat : Animal {
        val catType: String
    }

    interface Dog : Animal {
        val dogType: String
    }

    private class CatImpl : Cat {
        override val catType: String = "Tabby"
    }

    private class DogImpl : Dog {
        override val dogType: String = "Husky"
    }

    object AnimalProvider {
        fun createCat(): Cat = CatImpl()
        fun createDog(): Dog = DogImpl()
    }

    object CatSerializer : SerializationStrategy<Cat> {
        override val descriptor = buildClassSerialDescriptor("Cat") {
            element<String>("catType")
        }

        override fun serialize(encoder: Encoder, value: Cat) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.catType)
            }
        }
    }

    object DogSerializer : SerializationStrategy<Dog> {
        override val descriptor = buildClassSerialDescriptor("Dog") {
            element<String>("dogType")
        }

        override fun serialize(encoder: Encoder, value: Dog) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.dogType)
            }
        }
    }
}