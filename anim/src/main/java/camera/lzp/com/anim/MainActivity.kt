package camera.lzp.com.anim

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layout = findViewById<CheckedAndChangeShapeFrameLayout>(R.id.layout)
        layout.setOnClickListener {
            if (layout.isOpen) {
                layout.status = CheckedAndChangeShapeFrameLayout.Status.CLOSED
            } else {
                layout.status = CheckedAndChangeShapeFrameLayout.Status.OPEN
            }

        }
    }
}
