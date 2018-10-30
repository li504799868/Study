package recycler.lzp.com.quickreturnlistview.test;

import android.view.View;

/**
 * Created by lizhipeng 2017.06.28
 * <p>
 * recycleradapter 的item点击回调监听
 */
public interface OnItemClickListener<T> {
    void onItemClick(View view, T t, int position);
}