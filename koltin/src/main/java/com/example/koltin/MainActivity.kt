package com.example.koltin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arrayListOf("1").test()
        shl2(2)

        val items = listOf(1, 2, 3, 4, 5)

        // Lambdas are code blocks enclosed in curly braces.
        items.fold(0, {
            // When a lambda has parameters, they go first, followed by '->'
            acc: Int, i: Int ->
            print("acc = $acc, i = $i, ")
            val result = acc + i
            println("result = $result")
            // The last expression in a lambda is considered the return value:
            result
        })

        // Parameter types in a lambda are optional if they can be inferred:
        val joinedToString = items.fold("Elements:", { acc, i -> acc + " " + i })

        // Function references can also be used for higher-order function calls:
        val product = items.fold(1, Int::times)
    }

    private fun ArrayList<String>.test() {
        Log.e("lzp", "${this[0]} + test")
    }

    private infix fun shl2(x: Int) {
        Log.e("lzp", "$x")
    }

    private fun <T, R> Collection<T>.fold(initial: R, combine: (acc: R, nextElement: T) -> R): R {
        var accumulator: R = initial
        for (element: T in this) {
            accumulator = combine(accumulator, element)
        }
        return accumulator
    }
}
