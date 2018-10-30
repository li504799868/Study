package recycler.lzp.com.quickreturnlistview.test;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import recycler.lzp.com.quickreturnlistview.R;

/**
 * Created by li.zhipeng on 2018/10/26.
 */
public class TestActivity extends Activity implements FloatHeaderAndFooterRecyclerView.OnFloatViewStateChangedListener {

    private View floatFooter;

    private ObjectAnimator showFloatHeaderAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 999; i++) {
            list.add("1111");
        }

        floatFooter = findViewById(R.id.float_footer);

        FloatHeaderAndFooterRecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setFloatHeader(findViewById(R.id.float_header));
        recyclerView.setListener(this);
        recyclerView.setAdapter(new BaseRecycleViewAdapter<String>(this, list) {
            @Override
            protected int getLayoutId(int viewType) {
                return R.layout.list_item;
            }

            @Override
            protected void convert(ViewHolder holder, String item, int position) {
                holder.setText(R.id.text1, item);
            }

        });

    }

    @Override
    public void onFloatViewShow() {
        if (showFloatHeaderAnimator != null && showFloatHeaderAnimator.isRunning()){
            return;
        }
        if (showFloatHeaderAnimator == null){
            showFloatHeaderAnimator = ObjectAnimator.ofFloat(floatFooter, "translationY", floatFooter.getTranslationY(), 0);
            showFloatHeaderAnimator.setDuration(300);
        }
        showFloatHeaderAnimator.start();
    }

    @Override
    public void onFloatViewHide() {
        ObjectAnimator showFloatHeaderAnimator = ObjectAnimator.ofFloat(floatFooter, "translationY",
                floatFooter.getTranslationY(), floatFooter.getHeight());
        showFloatHeaderAnimator.setDuration(300);
        showFloatHeaderAnimator.start();
    }

    @Override
    public void onFloatViewScroll() {
        if (floatFooter.getTranslationY() != 0) {
            onFloatViewShow();
        }
    }
}
