package com.chen.home.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chen.home.util.LogUtil;

/**
 * Created by PengChen on 2018/1/17.
 */

public class CanvasView extends View {

    private Paint paint;
    private Paint cPaint;
    private float pathX = 40;
    private float pathY = 500;
    Path path = new Path();
    int dur = 0;
    ValueAnimator animator;
    ValueAnimator animatorB;

    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xffff0000);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);

        cPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cPaint.setColor(0xffff0000);
        cPaint.setStrokeWidth(5f);
        cPaint.setStyle(Paint.Style.FILL);


        animator = new ValueAnimator();
        animator.setIntValues(0, 360);
        animator.setDuration(5000);
        animator.setRepeatCount(-1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dur = (Integer) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawRect(20, 20, 200, 200, paint);
        path.reset();
        path.moveTo(20, 500);
        path.quadTo(pathX, pathY, 600, 500);
        canvas.drawPath(path, paint);

//        p.setStyle(Paint.Style.STROKE);//设置空心
        float finishX;
        if (dur < 180) {
            finishX = dur / 2;
        } else {
            finishX = (360 - dur) / 2;
        }
        LogUtil.i("dur = " + dur + ", finishX = " + finishX);
        float startCentreX =(float) ((100) * Math.cos(dur * Math.PI / 180)) + 250;
        float startCentreY =(float) ((100) * Math.sin(dur * Math.PI / 180)) + 250;

        float finishCentreX =(float) ((100) * Math.cos((dur + finishX) * Math.PI / 180)) + 250;
        float finishCentreY =(float) ((100) * Math.sin((dur + finishX) * Math.PI / 180)) + 250;

        canvas.drawCircle(startCentreX, startCentreY, 5, cPaint);
        canvas.drawCircle(finishCentreX, finishCentreY, 5, cPaint);
        RectF rectf=new RectF(150,150,350,350);
        canvas.drawArc(rectf, dur, finishX, false, paint);
        canvas.drawArc(rectf, 90, 30, false, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.i("touch " + event.getRawX());
        if (event.getAction() != MotionEvent.ACTION_UP) {
            pathX = event.getRawX();
            pathY = event.getRawY();
            invalidate();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            animatorB = new ValueAnimator();
            animatorB.setFloatValues(pathY, 500);
            animatorB.setDuration(100);
            animatorB.setRepeatCount(0);
            animatorB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    pathY = (float)animation.getAnimatedValue();
                    invalidate();
                }
            });
            invalidate();
            animatorB.start();
            animator.start();
        }
        return super.onTouchEvent(event);
    }
}
