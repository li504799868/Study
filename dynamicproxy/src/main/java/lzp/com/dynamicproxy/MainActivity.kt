package lzp.com.dynamicproxy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

@TestAnnotation(666)
class MainActivity : AppCompatActivity() {

    companion object {
        const val tag = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        ReflectUtils.getClassAnnotation(this)

        ReflectUtils.reflect(TestBean())
        val subject = RealSubjectInvocationHandler(RealSubject()).bind() as Subject
        subject.say("li", 22)
    }

    private fun test(){
        Log.e("lzp", "test")
    }

    interface Subject {
        fun say(name: String, age: Int)
    }

    class RealSubject : Subject {
        override fun say(name: String, age: Int) {
            Log.e("lzp", "$name:$age")
        }
    }

    /**
     * 动态代码类
     * */
    class RealSubjectInvocationHandler(private val binder: RealSubject) : InvocationHandler {

        fun bind(): Any? {
            return Proxy.newProxyInstance(binder.javaClass.classLoader, binder.javaClass.interfaces,
                    this)
        }

        @Throws(Throwable::class)
        override fun invoke(proxy: Any, method: Method, args: Array<out Any>): Any? {
            method.isAccessible = true
            return method.invoke(binder, *args)
        }

    }
}
