package com.hoverdroids.serialization.polymorphic.christests

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import org.junit.Test
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*

class SomeTest {

    val itemsGroupModelSerializersModule = SerializersModule {
        polymorphic(ItemsGroupModel::class) {
            subclass(PatternItemsGroupModel::class)
        }
    }

    //TODO - CHRIS - tableItemsGroupModelSerializersModule

    val format = Json { serializersModule = itemsGroupModelSerializersModule }

    @Test
    fun serialize() {
        /*val itemsGroupModel = ItemsGroupModel()
        val serializedItemsGroupModel = format.encodeToString(itemsGroupModel)
        println(serializedItemsGroupModel)*/

        val patternItemsGroupModel = PatternItemsGroupModel()
        val serializedPatternItemsGroupModel = format.encodeToString(patternItemsGroupModel)
        println(serializedPatternItemsGroupModel)

        val patternItemsGroupModelWithSuperCompileTimeType : ItemsGroupModel = PatternItemsGroupModel()
        val serializedPatternItemsGroupModelWithSuperCompileTimeType = format.encodeToString(patternItemsGroupModelWithSuperCompileTimeType)
        println(serializedPatternItemsGroupModelWithSuperCompileTimeType)

        val patternItemsGroupModelWithCompileTimeType : ItemsGroupModel = PatternItemsGroupModel()
        val serializedPatternItemsGroupModelWithCompileTimeType = format.encodeToString(patternItemsGroupModelWithCompileTimeType)
        println(serializedPatternItemsGroupModelWithCompileTimeType)
    }

    interface IItemsGroupModel {
        var groupName : String
        //var groupIcon : Icon?                             <- Can't use; serializer has not been found for the class
        var items : MutableList<ItemsGroupModel>?
    }

    @Polymorphic
    @Serializable
    abstract class ItemsGroupModel : IItemsGroupModel {

        override var groupName : String = "Some Group Name"
        override var items : MutableList<ItemsGroupModel>? = null

        constructor()

        constructor(
            groupName : String = "",
            items : MutableList<ItemsGroupModel>? = null
        ) : this() {
            this.groupName = groupName
            this.items = items
        }
    }

    interface IPatternModel {
        var patternName : String
        var pattern : String
    }

    interface IPatternItemsGroupModel: IItemsGroupModel, IPatternModel

    @Serializable
    @SerialName("PatternItemsGroupModel")
    class PatternItemsGroupModel : ItemsGroupModel, IPatternItemsGroupModel {

        override var patternName: String = "Some Pattern Name"
        override var pattern: String = "*...*/*987u"

        constructor() : super()

        constructor(
            groupName : String = "",
            items : MutableList<ItemsGroupModel>? = null,
            patternName: String = "",
            pattern: String = ""
        ) : this() {
            this.groupName = groupName
            this.items = items
            this.patternName = patternName
            this.pattern = pattern
        }
    }

}