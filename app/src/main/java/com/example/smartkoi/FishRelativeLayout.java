package com.example.smartkoi;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

public class FishRelativeLayout extends RelativeLayout {
    private Path mPath;
    private Paint mPaint;
    private ImageView iv_fish;
    private FishDrawable fishDrawable;
    private float touchX;
    private float touchY;
    private float ripple = 0f;

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    private float alpha;



    public float getRipple() {
        return ripple;
    }

    public void setRipple(float ripple) {
        this.ripple = ripple;
        alpha = 100*(1-ripple);
        invalidate();
    }

    public FishRelativeLayout(Context context) {
        this(context,null);
    }

    public FishRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FishRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //VIewGroup默认不执行onDraw放阿飞
        setWillNotDraw(false);
        //画水波纹
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(8);
        iv_fish = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        iv_fish.setLayoutParams(layoutParams);
        fishDrawable = new FishDrawable();
        iv_fish.setImageDrawable(fishDrawable);
        addView(iv_fish);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAlpha((int) alpha);
        canvas.drawCircle(touchX,touchY,ripple * 50,mPaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchX = event.getX();
        touchY = event.getY();
        ObjectAnimator.ofFloat(this,"ripple",0f,1f).setDuration(500).start();
        
        makeTrail();
        
        return super.onTouchEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void makeTrail() {
        //鱼的重心相对坐标
        PointF fishRelativeMiddle = fishDrawable.getMiddlePoint();

        //鱼的绝对坐标 --- 起始点
        PointF fishMiddle = new PointF(fishRelativeMiddle.x + iv_fish.getX(), fishRelativeMiddle.y + iv_fish.getY());

        // 鱼头坐标 ---- 控制点一
        PointF fishHead = new PointF(fishDrawable.getHeadPoint().x + iv_fish.getX(),
                fishDrawable.getHeadPoint().y+iv_fish.getY());

        //点击坐标 ----结束点
        PointF touch = new PointF(touchX,touchY);

        /**
         * 先用cos公式向量+三角函数算出AOB的度数
         * 控制点2在AOB的角平分线上（人为规定的）
         */


        Path path = new Path();
        //ObjectAnimator.ofFloat(iv_fish,"x","y",path);
    }

    /**
     *
     * @param A
     * @param O
     * @param B
     * @return
     */
    public float includeAngle(PointF A, PointF O, PointF B){
        //0A*0B向量积
        float AOB = (A.x-O.x)*(B.x-O.x) + (A.y-O.y)*(B.y-O.y);
        //OA*OB绝对值
        float OALength = (float) Math.sqrt((A.x-O.x)*(A.x-O.x) + (A.y-O.y)*(A.y-O.y));
        float OBLength = (float) Math.sqrt((B.x-O.x)*(B.x-O.x) + (B.y-O.y)*(B.y-O.y));
        float cosAOB = OALength*OBLength / AOB;
        float angleAOB = (float) Math.toDegrees(Math.acos(cosAOB));
        return 0;
    }
}
