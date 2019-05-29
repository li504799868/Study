package com.lzp.producerandconsumer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Shop.addProducer(Producer())
        Shop.addProducer(Producer())
        Shop.addProducer(Producer())
        Shop.addProducer(Producer())
        Shop.addProducer(Producer())
        Shop.addProducer(Producer())

        Shop.addConsumer(Consumer())
        Shop.addConsumer(Consumer())
    }
}
