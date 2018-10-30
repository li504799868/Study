package com.lzp.rxjavastudy

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.functions.Function
import org.junit.Test

import org.junit.Assert.*

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
    fun testSingle() {
        Single.just("1")
                .map {
                    "2"
                }
                .map {
                    "3"
                }
                .subscribe({
                    System.out.println("success")
                }, {
                    System.out.println("failed")
                })
    }

    @Test
    fun testObservable() {
        Observable.just("1")
                .map {
                    "2"
                }
                .switchMap { _ ->
                    Observable.just("3")
                }
                .subscribe({
                    System.out.println("success")
                }, {
                    System.out.println("failed")
                })
    }
}
