package com.lzp.activity;

import android.os.Debug;

import org.junit.Test;

import java.io.Console;

/**
 * Created by li.zhipeng on 2019-04-24.
 */
public class StringTest {

    @Test
    public void stringTest(){
        String a = "me";
        String b = a;
        System.out.println(a);//me
        System.out.println(b);//me
//        System.out.println("hashcode_a={0}，hashcode_b={1}", a.GetHashCode(), b.GetHashCode());
        a = "meeeee";//重新a赋值
        System.out.println(a);//meeeee
        System.out.println(b);//me
//        Debug.Assert(a.GetHashCode() != b.GetHashCode());
//        System.out.println("hashcode_a={0}，hashcode_b={1}", a.GetHashCode(), b.GetHashCode());
        System.out.println("\r\n\r\n.................按任意键继续");
    }
}
