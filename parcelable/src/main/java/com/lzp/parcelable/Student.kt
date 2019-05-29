package com.lzp.parcelable

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by li.zhipeng on 2019-05-27.
 *
 *      测试学生类
 */
class Student(private val name: String, private val age: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Student> {
        override fun createFromParcel(parcel: Parcel): Student {
            return Student(parcel)
        }

        override fun newArray(size: Int): Array<Student?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "Student: name = $name, age = $age"
    }
}