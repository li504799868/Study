package com.lzp.parcelable

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE
import android.view.View
import android.widget.TextView
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val path = "${Environment.getExternalStorageDirectory()}/parcel.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 写入测试数据
                writeTestData()
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
        } else {
            // 写入测试数据
            writeTestData()
        }

        findViewById<View>(R.id.read).setOnClickListener {
            readTestData()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("student", Student("zhangsan", 18))
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 写入测试数据
        writeTestData()
    }

    private fun writeTestData() {
        Thread(Runnable {
            val student = Student("zhangsan", 18)
            val parcel = Parcel.obtain()
            // 因为Parcel内部有缓存复用
            // 设置数据的位置指针为头部
            parcel.setDataPosition(0)
            student.writeToParcel(parcel, 0)
            val fos = FileOutputStream(path)
            fos.write(parcel.marshall())
            fos.flush()

            fos.close()
            parcel.recycle()

        }).start()
    }

    private fun readTestData() {
        Thread(Runnable {
            val fis = FileInputStream(path)
            val data = fis.readBytes()
            val parcel = Parcel.obtain()
            // 因为Parcel内部有缓存复用
            // 设置数据的位置指针为头部
            parcel.unmarshall(data, 0, data.size)
            parcel.setDataPosition(0)
            val student = Student(parcel)
            parcel.recycle()
            runOnUiThread {
                findViewById<TextView>(R.id.text).text = student.toString()
            }


        }).start()
    }
}
