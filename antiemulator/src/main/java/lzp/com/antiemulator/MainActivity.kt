package lzp.com.antiemulator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (AntiEmulator.isEmulator(this)) {
            Toast.makeText(this, "是模拟器", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "是手机", Toast.LENGTH_SHORT).show()
        }
    }
}
