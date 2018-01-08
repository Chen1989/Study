package com.chen.home.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.View;
import android.widget.TextView;

import com.chen.home.R;

/**
 * Created by admin on 2017/11/27.
 */

public class CommonTitleView extends RelativeLayout implements View.OnClickListener {
    private TextView tvLeft;

    private TextView tvRight;

    private TextView tvContent;

    private Context mContext;

    private OnTitleViewClickListener listener;

    private AttributeSet mAttrs;

    private LayoutInflater mInflater;

    private View mView;

    public static final int TITLE_STYLE_COMMON=0;

    public static final int TITLE_STYPE_REBACK=1;

    public static final int TITLE_STYPE_NOTITLE=2;

    public static final int TITLE_STYPE_COMMON_TEXT=3;

    private int mTheme;

    private Drawable mLeftDrawable;

    private Drawable mRightDrawable;

    private  CharSequence mLeftText;

    private  CharSequence mRightText;

    private CharSequence middleText;

    private  int leftTextColor;
    private  int rightTextColor;
    private  int middleTextColor;
    private  int leftTextSize;
    private  int rightTextSize;
    private  int middleTextSize;

    private int onSetStatusBar=-1;

    private LinearLayout flCommonLayout;

    private RelativeLayout rlCommonLayout;

    public CommonTitleView(Context context) {
        super(context);
        this.mContext=context;
    }

    public CommonTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        onCreate(context,attrs);
    }

    public CommonTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        onCreate(context,attrs);
    }

    public OnTitleViewClickListener getOnTitleViewClickListener() {
        return listener;
    }

    public void setOnTitleViewClickListener(OnTitleViewClickListener listener) {
        this.listener = listener;
    }


    public void setStatusBarColor(int color){
        this.onSetStatusBar=color;
    }

    /**
     *  执行View的创建
     */
    private  void onCreate(Context context , AttributeSet attrs){

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VS_CommonTitleView);
        mLeftDrawable=a.getDrawable(R.styleable.VS_CommonTitleView_leftDrawable);
        mRightDrawable=a.getDrawable(R.styleable.VS_CommonTitleView_rightDrawable);
        mLeftText=a.getText(R.styleable.VS_CommonTitleView_leftText);
        mRightText=a.getText(R.styleable.VS_CommonTitleView_rightText);
        middleText=a.getText(R.styleable.VS_CommonTitleView_middleText);
        leftTextColor=a.getColor(R.styleable.VS_CommonTitleView_leftTextColor, mContext.getResources().getColor(R.color.vs_global_text_blue));
        rightTextColor=a.getColor(R.styleable.VS_CommonTitleView_rightTextColor,mContext.getResources().getColor(R.color.vs_global_text_blue));
        middleTextColor=a.getColor(R.styleable.VS_CommonTitleView_rightTextColor, mContext.getResources().getColor(R.color.black));
        leftTextSize=a.getInt(R.styleable.VS_CommonTitleView_leftTextSize, 16);
        rightTextSize=a.getInt(R.styleable.VS_CommonTitleView_rightTextSize, 16);
        middleTextSize=a.getInt(R.styleable.VS_CommonTitleView_middleTextSize, 18);
        a.recycle();


        mInflater= LayoutInflater.from(mContext);
        mView=mInflater.inflate(R.layout.common_title_view, this);
        tvLeft= (TextView) mView.findViewById(R.id.tv_common_title_left);
        tvRight= (TextView) mView.findViewById(R.id.tv_common_title_right);
        tvContent= (TextView) mView.findViewById(R.id.tv_common_title_content);
        flCommonLayout=(LinearLayout)mView.findViewById(R.id.ll_common_titleview);
        rlCommonLayout=(RelativeLayout)mView.findViewById(R.id.rl_common_titleview);
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        flCommonLayout.setBackgroundColor(Color.WHITE);

        if(mLeftDrawable != null){
            setLeftDrawable(mLeftDrawable);
        }else {
            if(mLeftText==null) {
                setLeftDrawable(R.drawable.btn_page_back);
                mTheme=TITLE_STYPE_REBACK;
            }
        }

        if(mRightDrawable != null){
            setRightDrawable(mRightDrawable);
        }else{
            if(mRightText==null) {
                tvRight.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
            }
        }

        if(mLeftText !=null){
            setLeftText(mLeftText.toString());
        }

        if(mRightText != null){
            setRightText(mRightText.toString());
        }


        if(middleText != null){
            setContentText(middleText.toString());
        }

        tvLeft.setTextColor(leftTextColor);
        tvRight.setTextColor(rightTextColor);
        tvContent.setTextColor(middleTextColor);

        tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP,leftTextSize);
        tvRight.setTextSize(TypedValue.COMPLEX_UNIT_SP,rightTextSize);
        tvContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,middleTextSize);

        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(listener != null) {
            if (v == tvLeft) {
                listener.onLeftClick();
            } else if (v == tvRight) {
                listener.onRightClick();
            }
        }else{
            if(v==tvLeft){
                if(mContext instanceof Activity && mLeftText == null && (mTheme==TITLE_STYPE_COMMON_TEXT|| mTheme==TITLE_STYPE_REBACK) ){
                    Activity activity = (Activity) mContext;
                    activity.finish();
                }
            }
        }
    }

    public void setLeftText(String text) {
        tvLeft.setText(text);
    }


    public void setRightText(String text) {
        tvRight.setText(text);
    }


    public void setContentText(String text) {
        tvContent.setText(text);
    }

    public void setContentText(int  resId) {
        tvContent.setText(getResources().getText(resId));
    }

    /**
     * 是否显示右边按钮
     * @param isVisible
     */
    public void setRightVisible(boolean isVisible){
        if(isVisible==true){
            tvRight.setVisibility(View.VISIBLE);
        }else{
            tvRight.setVisibility(View.GONE);
        }
    }


    /**
     * 是否显示左边按钮
     * @param isVisible
     */
    public void setLeftVisible(boolean isVisible){
        if(isVisible==true){
            tvLeft.setVisibility(View.VISIBLE);
        }else{
            tvLeft.setVisibility(View.GONE);
        }
    }

    /**
     * 设置左边的字体的颜色
     * @param resId
     */
    public void setLeftColor(int resId){
        tvLeft.setTextColor(resId);
    }
    /**
     * 设置右边的字体的颜色
     * @param resId
     */
    public void setRightColor(int resId){

        tvRight.setTextColor(resId);

    }
    /**
     * 设置中间的字体的颜色
     * @param resId
     */
    public void setTitleColor(int resId){
        tvContent.setTextColor(resId);
    }

    public void setLeftDrawable(int resId) {
        Drawable drawable=getResources().getDrawable(resId);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        tvLeft.setCompoundDrawables(drawable, null, null, null);
    }

    public void setLeftDrawable(Drawable drawable) {
        if(drawable!=null){
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            tvLeft.setCompoundDrawables(drawable, null, null, null);
        }else{
            tvLeft.setCompoundDrawables(null, null, null, null);
        }
    }

    /**
     * 设置背景 如设置里面的头部bar,
     * @param drawable
     */
    public void setTitleViewBackground(Drawable drawable){

        if(drawable==null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                flCommonLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                flCommonLayout.setBackground(drawable);
            }
        }
    }





    public void setRightDrawable(int resId) {
        Drawable drawable=getResources().getDrawable(resId);
        drawable.setBounds(0, 0,drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
        tvRight.setCompoundDrawables(null, null, drawable, null);//画在右边
    }

    public void setRightDrawable(Drawable drawable) {
        if(drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
            tvRight.setCompoundDrawables(null, null, drawable, null);//画在右边
        }else{
            tvRight.setCompoundDrawables(null, null, null, null);//画在右边
        }
    }


    public void setView(View view) {

    }


    public void setLeftText(int resId) {
        tvLeft.setText(getResources().getText(resId));
    }


    public void setRightText(int resId) {
        tvRight.setText(getResources().getText(resId));
    }


    public interface  OnTitleViewClickListener{
        /**
         * 执行左边的事件
         */
        void onLeftClick();
        /**
         * 执行右边的事件
         */
        void onRightClick();
//        /**
//         * 执行右边的事件
//         */
//        void onContentClick();

    }

    public void setStyle(int style) {

        switch (style) {
            case TITLE_STYLE_COMMON:
                tvRight.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.VISIBLE);
                setLeftDrawable(R.drawable.btn_main_head_glasses_selector);
                setRightDrawable(R.drawable.btn_main_head_search_selector);
                mTheme = TITLE_STYLE_COMMON;
                break;
            case TITLE_STYPE_REBACK:
                tvContent.setVisibility(View.VISIBLE);
                setLeftDrawable(R.drawable.btn_page_back);
                tvRight.setVisibility(View.GONE);
                mTheme = TITLE_STYPE_REBACK;
                break;
            case TITLE_STYPE_NOTITLE:
                tvContent.setVisibility(View.GONE);
                tvRight.setVisibility(View.VISIBLE);
                setLeftDrawable(R.drawable.btn_main_head_glasses_selector);
                setRightDrawable(R.drawable.btn_main_head_search_selector);
                mTheme = TITLE_STYPE_NOTITLE;
                break;
            case TITLE_STYPE_COMMON_TEXT:
                tvRight.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.VISIBLE);
                setLeftDrawable(R.drawable.btn_page_back);
                setRightDrawable(null);
                mTheme = TITLE_STYPE_COMMON_TEXT;
                break;
            default:
                tvContent.setVisibility(View.VISIBLE);
                setLeftDrawable(R.drawable.btn_page_back);
                tvRight.setVisibility(View.GONE);
                break;

        }
    }
    public void startSock(){

        if(mContext instanceof  Activity) {
            Activity activity= (Activity) mContext;

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                setDockerView(activity);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }else if (Build.VERSION.SDK_INT >=21 && Build.VERSION.SDK_INT<23) {
                setDockerView(activity);
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }else if(Build.VERSION.SDK_INT>=23){
                setDockerView(activity);
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }



    void setDockerView(Activity activity){
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,getStatusBarHeight(activity));
        View statusBarView = new View(activity);
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        // 移除半透明矩形,以免叠加
        if (flCommonLayout.getChildCount() >1) {
            flCommonLayout.removeViewAt(0);
        }
        flCommonLayout.addView(statusBarView, 0);

        View statusBarViewTransparent = new View(activity);
        statusBarViewTransparent.setLayoutParams(params);

        if(onSetStatusBar != -1){
            int color=onSetStatusBar;
            statusBarViewTransparent.setBackgroundColor(color);
        }else {
            statusBarViewTransparent.setBackgroundDrawable(getResources().getDrawable(R.drawable.vs_bg_common_sock));
        }
        if (rlCommonLayout.getChildCount() >1) {
            rlCommonLayout.removeViewAt(1);
        }
        rlCommonLayout.addView(statusBarViewTransparent,1);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
}
