package com.example.koltin

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Created by li.zhipeng on 2018/4/17.
 */
class Example {

    var p: String by Delegate

    var o: String by Delegates.observable("o") { prop: KProperty<*>, old: String, new: String ->
        println("$old to $new")
    }

    object Delegate {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
            return "$thisRef, thank you for delegating '${property.name}' to me!"
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
            println("$value has been assigned to '${property.name}' in $thisRef.")
        }
    }
}