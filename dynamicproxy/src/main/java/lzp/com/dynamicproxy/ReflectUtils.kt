package lzp.com.dynamicproxy

import android.support.annotation.NonNull
import android.util.Log
import java.lang.reflect.Proxy
import kotlin.math.log
import java.lang.reflect.Array.setInt
import java.lang.reflect.Modifier


/**
 * Created by li.zhipeng on 2018/7/13.
 */
object ReflectUtils {

    /**
     *
     * */
    fun getClassAnnotation(obj: Any) {
//        val annotation = clazz.getAnnotation(TestAnnotation::class.java)
        // 创建直接的代理
//        val proxy = Proxy.getInvocationHandler(annotation)
//        val method = annotation.javaClass.getMethod("value")

//        Log.e("lzp", annotation.value.toString())
//        Log.e("lzp", proxy.invoke(annotation, method, null).toString())

        val method = obj.javaClass.getDeclaredMethod("test")
        method.isAccessible = true
        method.invoke(obj)

        val field = obj.javaClass.getDeclaredField("tag")
        field.isAccessible = true
        field.setInt(field, field.modifiers and Modifier.FINAL.inv())//fianl标志位置0

        field.set(obj, "getClassAnnotation")
        Log.e("lzp", MainActivity.tag)

    }

    fun reflect(obj: Any) {
        val field = obj.javaClass.getDeclaredField("VALUE")
        field.isAccessible = true
        field.setInt(field, field.modifiers and Modifier.FINAL.inv())//final标志位置0

        field.set(obj, 10000)
        Log.e("lzp", TestBean.VALUE.toString())

    }
}