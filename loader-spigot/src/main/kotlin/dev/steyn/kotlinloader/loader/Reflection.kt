package dev.steyn.kotlinloader.loader

import sun.misc.Unsafe
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KProperty


fun Field.makeMutable() : Boolean {
    try {
        val modifiersField = javaClass.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(this, (modifiers and Modifier.FINAL))
    } catch(e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}

