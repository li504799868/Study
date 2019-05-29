package com.example.johnanna.study;

import android.support.v4.widget.TextViewCompat;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Function;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void rxJavaTest() {
        io.reactivex.Observable.just("1")
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        System.out.println("map to 2");
                        return "2";
                    }
                })
                .flatMap(new Function<String, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(String s) throws Exception {
                        System.out.println("flatMap to 3");
                        return io.reactivex.Observable.just("3");
                    }
                })
                .subscribe();
    }

    @Test
    public void testConcurrentHashMap() {
        final CopyOnWriteArrayList<String> map = new CopyOnWriteArrayList<>();

        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 100000; i++) {
                    System.out.println("put");
                    map.add(String.valueOf(i));
                }
            }
        }.start();

        // 遍历
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (String i : map){
                    System.out.println(i);
                }
            }
        }.start();

    }

    @Test
    public void testInt(){
        int i = 100;
        System.out.println(i);
        // 通过new创建新的对象
        Integer i2 = new Integer(127);
        System.out.println(i2);
        Integer i4 = new Integer(127);
        System.out.println(i2 == i4);

        // 通过valueof
        Integer i3 = Integer.valueOf(127);
        Integer i5 = Integer.valueOf(127);
        System.out.println(i3 == i5);


    }
}