package com.hoverdroids.serialization.polymorphic.christests

import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.junit.Test

class PolymorphismTests {

    /*val itemsGroupModelSerializersModule = SerializersModule {
        polymorphic(ItemsGroupModel::class) {
            subclass(PatternItemsGroupModel::class)
        }
    }*/

    //TODO - CHRIS - tableItemsGroupModelSerializersModule

//    val format = Json { serializersModule = itemsGroupModelSerializersModule }

    @Test
    fun test1() {
        val item = ItemsGroupModel()
        val serializedItem = Json.encodeToString(item)//No exception, but doesn't serialize properly either
        println(serializedItem)// {}
    }

    @Test
    fun test2() {
        val item: IItemsGroupModel = ItemsGroupModel()
        val serializedItem = Json.encodeToString(item)//Exception: Class 'ItemsGroupModel' is not registered for polymorphic serialization in the scope of 'IItemsGroupModel'
        println(serializedItem)
    }

    @Test
    fun test3() {
        val json = Json { serializersModule =
            SerializersModule {
                polymorphic(IItemsGroupModel::class) {
                    subclass(ItemsGroupModel::class)
                }
            }
        }

        val item: IItemsGroupModel = ItemsGroupModel()
        val serializedItem = json.encodeToString(item)//Serializes, but without fields
        println(serializedItem)//{"type":"com.hoverdroids.serialization.polymorphic.christests.Test1.ItemsGroupModel"}
    }

    @Test
    fun test4() {
        val json = Json { serializersModule =
            SerializersModule {
                polymorphic(IItemsGroupModel::class) {
                    subclass(ItemsGroupModel::class, ItemsGroupModel.serializer())//<-testing the difference when the serializer is specified vs not
                }
            }
        }

        val item: IItemsGroupModel = ItemsGroupModel()
        val serializedItem = json.encodeToString(item)//Serializes, but without fields
        println(serializedItem)//{"type":"com.hoverdroids.serialization.polymorphic.christests.Test1.ItemsGroupModel"}
    }

    @Test
    fun test5() {
        val json = Json { serializersModule =
            SerializersModule {
                polymorphic(IItemsGroupModel::class) {
                    subclass(ItemsGroupModel::class)
                }
            }
        }

        val item: IItemsGroupModel = ItemsGroupModel("bro")
        val serializedItem = json.encodeToString(item)//Serializes, only with groupName because it's not the default param
        println(serializedItem)//{"type":"com.hoverdroids.serialization.polymorphic.christests.Test1.ItemsGroupModel","groupName":"bro"}
    }

    @Test
    fun test6() {
        val json = Json { serializersModule =
            SerializersModule {
                polymorphic(IItemsGroupModel::class) {
                    subclass(ItemsGroupModel::class)
                }
            }
            encodeDefaults = true//default values will not be serialized without this
        }

        val item: IItemsGroupModel = ItemsGroupModel()
        val serializedItem = json.encodeToString(item)//Serializes, only with groupName because it's not the default param
        println(serializedItem)//{"type":"com.hoverdroids.serialization.polymorphic.christests.Test1.ItemsGroupModel","groupName":"Some Group Name","items":null}
    }

    @Test
    fun test7() {
        val json = Json { serializersModule =
            SerializersModule {
                polymorphic(IItemsGroupModel::class) {
                    subclass(ItemsGroupModel::class)
                    subclass(ItemsGroupModel2::class)
                    subclass(ItemsGroupModel3::class)
                }
            }
            encodeDefaults = true//default values will not be serialized without this
        }

        // 1. The following shows how to serialize a class that implements IItemsGroupModel, and then a subclass
        val item: IItemsGroupModel = ItemsGroupModel("Bruh",
            mutableListOf(
                ItemsGroupModel("Bruh2"),
                ItemsGroupModel2("BroBro",
                    mutableListOf(
                        ItemsGroupModel3("Sista")
                    )
                ),
            )
        )

        val serializedItem = json.encodeToString(item)//Serializes, only with groupName because it's not the default param
        println(serializedItem)//{"type":"com.hoverdroids.serialization.polymorphic.christests.Test1.ItemsGroupModel","groupName":"Bruh","items":[{"type":"com.hoverdroids.serialization.polymorphic.christests.Test1.ItemsGroupModel","groupName":"Bruh2","items":null}]}

        // 2. Both are properly deserialized back to the specific class types and not the generic type
        val deserializedItemUsingInterface = json.decodeFromString<IItemsGroupModel>(serializedItem)
        println(deserializedItemUsingInterface)//Works as expected

        // The following will throw an exception because decoding from ItemsGroupModel is wrong; it must be IItemsGroupModel because
        // that is what was registered in SerializersModule
        try {
            val deserializedItemUsingClass = json.decodeFromString<ItemsGroupModel>(serializedItem)
            println(deserializedItemUsingClass)//Exception thrown
        } catch (e: Exception) {

        }

        // Now with lists ...
        val item2: IItemsGroupModel = ItemsGroupModel("Bruh2",
            mutableListOf(
                ItemsGroupModel("Bruh22"),
                ItemsGroupModel2("BroBro2",
                    mutableListOf(
                        ItemsGroupModel3("Sista2")
                    )
                ),
            )
        )

        val items = listOf(item, item2)
        val serializedList = json.encodeToString(items)
        println(serializedList)
        val deserializedList = json.decodeFromString<List<IItemsGroupModel>>(serializedList)
        println(deserializedList)
    }

    interface IItemsGroupModel {
        var groupName : String
        var items : MutableList<IItemsGroupModel>?
    }

    @Serializable
    open class ItemsGroupModel(
        override var groupName : String = "Some Group Name",
        override var items : MutableList<IItemsGroupModel>? = null
    ) : IItemsGroupModel

    @Serializable
    open class ItemsGroupModel2 : ItemsGroupModel {

        constructor() : super()

        constructor(groupName : String = "Some Group Name",
                    items : MutableList<IItemsGroupModel>? = null) : super(groupName, items)
    }

    @Serializable
    @SerialName("ItemsGroupModel3")//<-not essential to be serialized; it just strips the package from the class for smaller jsons
    class ItemsGroupModel3 : ItemsGroupModel2 {

        constructor() : super()

        constructor(groupName : String = "Some Group Name",
                    items : MutableList<IItemsGroupModel>? = null) : super(groupName, items)
    }
}