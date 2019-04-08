package com.lzp.reflect

import org.junit.Test

import org.junit.Assert.*
import java.lang.reflect.AccessibleObject.setAccessible



/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testReflect(){
        val clazz = Class.forName("com.lzp.reflect.MainActivity")
        val obj = clazz.newInstance()
        // 可以直接对 private 的属性赋值
        val field = clazz.getDeclaredField("tag")
        field.isAccessible = true
        field.set(obj, "Java反射机制")
        System.out.println(field.get(obj))
    }


}
