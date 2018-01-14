package com.chen.home;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chen.home.download.DownLoaderManger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private RecyclerView recyclerView;
    private List<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_test);
        Log.d("ChenSdk", "length = ");
        initData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, LinearLayoutManager.VERTICAL));
//        testDownload();
    }

    protected void initData()
    {
        mDatas = new ArrayList<String>();
        for (int i = 0; i < 99999; i++)
        {
            mDatas.add("chen_" + i);
        }
    }

    private void testDownload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = "IFirstPlug";
                String url = "http://192.168.1.157:7899/test/IFirstPlug";
                DownLoaderManger.getInstance(getApplicationContext()).start(name, url, 1);
            }
        }).start();

    }

    private void test() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlDownload = "";
                urlDownload = "http://192.168.1.105/webPage.js";

                try {
                    URL url = new URL(urlDownload);
                    // 打开连接
                    URLConnection con = url.openConnection();
                    // 输入流
                    int contentLength  = con.getContentLength();
                    InputStream inStream = con.getInputStream();
                    Log.d("ChenSdk", "length = " + contentLength);
                    Log.d("ChenSdk", "content = " + inStream.toString());

                    byte[] buffer = new byte[1024 * 4];
                    int len = 0;
                    StringBuffer sb = new StringBuffer();
                    String dirName  = "D:\\workInstall\\text.js";
                    File f = new File(dirName);
                    OutputStream os = new FileOutputStream(dirName);
                    while( (len = inStream.read(buffer)) !=-1 ){
                        os.write(buffer,0, len);
                    }
                    inStream.close();
                    os.close();
                    Log.d("ChenSdk", "content = " + sb.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewHolder holder = new RecyclerViewHolder(LayoutInflater.from(
                    MainActivity.this).inflate(R.layout.recycler_view_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
            holder.tv_num.setText(mDatas.get(position));
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("holder", "AAAAAAAAAAAAA");
//                    Toast.makeText(MainActivity.this, "click item " + position, Toast.LENGTH_SHORT).show();
//                }
//            });
            holder.itemView.setOnClickListener(l -> {
                Log.i("holder", "AAAAAAAAAAAAA");
            });
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_num;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tv_num = (TextView)itemView.findViewById(R.id.tv_num);
        }
    }

    public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };
        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
        /**
         * 用于绘制间隔样式
         */
        private Drawable mDivider;
        /**
         * 列表的方向，水平/竖直
         */
        private int mOrientation;

        public RecyclerViewItemDecoration(Context context, int orientation) {
            // 获取默认主题的属性
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            // 绘制间隔
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }

        private void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        /**
         * 绘制间隔
         */
        private void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left + 30, top, right, bottom);
                mDivider.draw(c);
            }
        }

        /**
         * 绘制间隔
         */
        private void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin +
                        Math.round(ViewCompat.getTranslationX(child));
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
