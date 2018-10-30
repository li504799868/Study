package recycler.lzp.com.quickreturnlistview.test;

import android.view.View;

/**
 * Created by luomin on 16/8/4.
 */
interface OnItemLongClickListener<T> {
    void onItemLongClick(View view, int position, T data);
}
