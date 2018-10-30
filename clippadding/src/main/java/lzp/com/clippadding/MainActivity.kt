package lzp.com.clippadding

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.w3c.dom.Text
import java.util.concurrent.ConcurrentHashMap

class MainActivity : AppCompatActivity() {

    private val data = arrayOf(
            "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111",
            "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111",
            "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111", "1111"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val list = findViewById<ListView>(R.id.list)
//        list.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayOf(
//                "1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111",
//                "1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111",
//                "1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111","1111"
//        ))

        val recycleView = findViewById<RecyclerView>(R.id.recyclerView)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return MyViewHolder(layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false))
            }

            override fun getItemCount(): Int = data.size

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val textView = holder.itemView.findViewById<TextView>(android.R.id.text1)
                textView.text = data[position]
            }

            inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            }

        }
    }
}
