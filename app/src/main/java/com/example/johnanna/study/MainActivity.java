package com.example.johnanna.study;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOverlay;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = findViewById(R.id.text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ViewOverlay overlay = text.getOverlay();
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
            drawable.setBounds(0 ,0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
            overlay.add(drawable);
        }

        final TextView text1 = findViewById(R.id.text1);
        text1.post(new Runnable() {
            @Override
            public void run() {
                View v = findViewById(R.id.good);
                v.scrollTo(0, 3000);
                Toast.makeText(MainActivity.this, text1.getWidth() + "", Toast.LENGTH_LONG).show();
            }
        });

        getApplication();
        getApplicationContext();
    }
}
