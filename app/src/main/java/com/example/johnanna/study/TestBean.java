package com.example.johnanna.study;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by li.zhipeng on 2019-05-13.
 */
public class TestBean implements Parcelable {

    protected TestBean(Parcel in) {
    }

    public static final Creator<TestBean> CREATOR = new Creator<TestBean>() {
        @Override
        public TestBean createFromParcel(Parcel in) {
            return new TestBean(in);
        }

        @Override
        public TestBean[] newArray(int size) {
            return new TestBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    class InnerClass {
        /**
         * 内部类可以有静态变量
         * */
        public static final String NAME = "123";

        /**
         * 内部类不可以有静态方法
         * */
//        public static void sayName(){
//
//        }
    }

    static class StaticInnerClass {
        /**
         * 静态内部类可以有静态变量
         * */
        public static final String NAME = "123";

        /**
         * 静态内部类可以有静态方法
         * */
        public static void sayName(){

        }
    }
}
