package com.example.smartkoi;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FishDrawable extends Drawable {
    private Paint mPaint;
    private Path mPath;
    private float currentValue = 0f;
    private int OTHER_ALPHA = 1000;

    public float getFrequance() {
        return frequance;
    }

    public void setFrequance(float frequance) {
        this.frequance = frequance;
    }

    //小鱼点击尾巴加速摆动
    private float frequance = 1f;
    public float getHEAD_RADIUS() {
        return HEAD_RADIUS;
    }

    private float HEAD_RADIUS = 50;
    //
    private float fishMainAngle = 90;

    public PointF getMiddlePoint() {
        return middlePoint;
    }

    //鱼的中心
    private PointF middlePoint;
    //鱼身长度
    private float BODY_LENGTH = HEAD_RADIUS * 3.2f;
    //寻找鱼鳍的初始点
    private float FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS;
    //鱼鳍的长度
    private float FINS_LENGTH = 1.3f * HEAD_RADIUS;
    //大圆半径
    private float BIG_CIRCLE_RADIUS = 0.7f*HEAD_RADIUS;
    //中圆半径
    private float MIDDLE_CIRCLE_RADIUS = 0.6f*BIG_CIRCLE_RADIUS;
    //小圆半径
    private float SMALL_CIRCLE_RADIUS = 0.4f*MIDDLE_CIRCLE_RADIUS;
    //寻找尾部中圆圆心线长
    private float FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS * 1.6f;
    //寻找小圆
    private float FIND_SMALL_CIRCLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 3.1f;
    //寻找三角形
    private float FIND_TRIANGLE = MIDDLE_CIRCLE_RADIUS * 2.7f;
    //眼镜半径
    private float EYE_CIRCLE = HEAD_RADIUS * 0.1f;
    //鱼头坐标
    PointF headPoint;

    public PointF getHeadPoint() {
        return headPoint;
    }

    FishDrawable(){
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPath = new Path();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setARGB(OTHER_ALPHA,244,92,71);

        middlePoint = new PointF(4.19f * HEAD_RADIUS,4.19f * HEAD_RADIUS);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f,360f);
        //动画周期
        valueAnimator.setDuration(2000);
        //重复模式
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        //重复次数
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) animation.getAnimatedValue();
                invalidateSelf();
            }
        });
        valueAnimator.start();
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        //鱼的初始角度
        float fishAngle = (float) (fishMainAngle + Math.sin(Math.toRadians(currentValue * frequance))*5);
        //鱼头的圆心坐标
        headPoint = calculatePoint(middlePoint, BODY_LENGTH ,fishAngle);

        //画鱼头
        canvas.drawCircle(headPoint.x,headPoint.y,HEAD_RADIUS,mPaint);
        //画右鱼鳍
        PointF rightFinsPoint = calculatePoint(headPoint,FIND_FINS_LENGTH,fishAngle-130);
        makeFins(canvas,rightFinsPoint,fishAngle,true);
        //画左鱼鳍
        PointF leftFinsPoint = calculatePoint(headPoint,FIND_FINS_LENGTH,fishAngle+130);
        makeFins(canvas,leftFinsPoint,fishAngle,false);
        //画大节支
        PointF bodyBottomCenterPoint = calculatePoint(headPoint,BODY_LENGTH,fishAngle-180);
        PointF smallCenterPoint = makeSegment(canvas,bodyBottomCenterPoint,BIG_CIRCLE_RADIUS,MIDDLE_CIRCLE_RADIUS,FIND_MIDDLE_CIRCLE_LENGTH,
                fishAngle,true);
        //画小节支
        makeSegment(canvas,smallCenterPoint,MIDDLE_CIRCLE_RADIUS,SMALL_CIRCLE_RADIUS,FIND_SMALL_CIRCLE_LENGTH,
                fishAngle,false);

        float findEdgeLength = (float) (Math.abs(Math.sin(Math.toRadians(currentValue * frequance  )) * BIG_CIRCLE_RADIUS));
        //画尾巴
        makeTriangle(canvas,smallCenterPoint,FIND_TRIANGLE,findEdgeLength,fishAngle);
        makeTriangle(canvas,smallCenterPoint,FIND_TRIANGLE-10,findEdgeLength-10,fishAngle);
        //画身体
        makeBody(canvas,headPoint,fishAngle);
        //画眼睛
        makeEyes(canvas,headPoint,fishAngle);

    }

    private void makeEyes(Canvas canvas, PointF headPoint, float fishAngle) {
        PointF leftEye = calculatePoint(headPoint,HEAD_RADIUS,fishAngle + 45);
        PointF rightEye = calculatePoint(headPoint,HEAD_RADIUS,fishAngle - 45);
        canvas.drawCircle(leftEye.x,leftEye.y,EYE_CIRCLE,mPaint);
        canvas.drawCircle(rightEye.x,rightEye.y,EYE_CIRCLE,mPaint);
    }

    /**
     *
     * @param canvas
     * @param headPoint
     * @param fishAngle
     */
    private void makeBody(Canvas canvas, PointF headPoint, float fishAngle) {
        PointF topLeft = calculatePoint(headPoint,HEAD_RADIUS,fishAngle+90);
        PointF topRight = calculatePoint(headPoint,HEAD_RADIUS,fishAngle-90);
        PointF bottomCenterPoint = calculatePoint(headPoint,BODY_LENGTH,fishAngle-180);
        PointF bottomLeft = calculatePoint(bottomCenterPoint,BIG_CIRCLE_RADIUS,fishAngle+90);
        PointF bottomRight = calculatePoint(bottomCenterPoint,BIG_CIRCLE_RADIUS,fishAngle-90);
        PointF controlLeft = calculatePoint(headPoint,BODY_LENGTH*0.56f,fishAngle+140);
        PointF controlRight = calculatePoint(headPoint,BODY_LENGTH*0.56f,fishAngle-140);
        mPath.reset();
        mPath.moveTo(topLeft.x,topLeft.y);
        mPath.quadTo(controlLeft.x,controlLeft.y,bottomLeft.x,bottomLeft.y);
        mPath.lineTo(bottomRight.x,bottomRight.y);
        mPath.quadTo(controlRight.x,controlRight.y,topRight.x,topRight.y);
        canvas.drawPath(mPath,mPaint);
    }

    /**
     *
     * @param canvas
     * @param startPoint
     * @param fishAngle
     */

    private void makeTriangle(Canvas canvas, PointF startPoint,float findTriangle, float bigCircleLength, float fishAngle) {
        float tr = 0;
        tr = (float) (fishAngle + Math.sin(Math.toRadians(currentValue*2*frequance))*30);
        //三角形底边中心坐标
        PointF centerPoint = calculatePoint(startPoint,findTriangle,tr - 180);
        //得到三角形的两个点
        PointF bottomLeft = calculatePoint(centerPoint,bigCircleLength,tr+90);
        PointF bottomRight = calculatePoint(centerPoint,bigCircleLength,tr-90);
        mPath.reset();
        mPath.moveTo(startPoint.x,startPoint.y);
        mPath.lineTo(bottomLeft.x,bottomLeft.y);
        mPath.lineTo(bottomRight.x,bottomRight.y);
        canvas.drawPath(mPath,mPaint);
    }


    /**
     *
     * @param canvas
     * @param startPoint
     * @param fishAngle
     * @return
     */
    private PointF makeSegment(Canvas canvas, PointF startPoint,float bigRadius, float smallRadius,
                             float findLength, float fishAngle, boolean hasBigCircle) {
        float segmentAngle = 0f;
        if (hasBigCircle){
            segmentAngle = (float) (fishAngle + Math.cos(Math.toRadians(currentValue*2* frequance))*15);
        }else {
            segmentAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue*2* frequance))*30);
        }
        //梯形上底圆心
        PointF upperPoint = calculatePoint(startPoint,findLength,segmentAngle - 180);
        //梯形4个点
        PointF bottomLeftPoint = calculatePoint(startPoint,bigRadius,segmentAngle-90);
        PointF bottomRightPoint = calculatePoint(startPoint,bigRadius,segmentAngle+90);
        PointF upperLeftPoint = calculatePoint(upperPoint,smallRadius,segmentAngle-90);
        PointF upperRightPoint = calculatePoint(upperPoint,smallRadius,segmentAngle+90);
        if(hasBigCircle) {
            //画大圆
            canvas.drawCircle(startPoint.x, startPoint.y, bigRadius, mPaint);
        }
        //画小圆
        canvas.drawCircle(upperPoint.x,upperPoint.y,smallRadius,mPaint);
        //画梯形
        mPath.reset();
        mPath.moveTo(bottomLeftPoint.x,bottomLeftPoint.y);
        mPath.lineTo(bottomRightPoint.x,bottomRightPoint.y);
        mPath.lineTo(upperRightPoint.x,upperRightPoint.y);
        mPath.lineTo(upperLeftPoint.x,upperLeftPoint.y);
        canvas.drawPath(mPath,mPaint);
        return upperPoint;
    }

    /**
     * 画鱼鳍
     * @param canvas
     * @param startPoint
     * @param fishAngle
     * @param isRight
     */
    private void makeFins(Canvas canvas, PointF startPoint, float fishAngle, boolean isRight) {
        float controlAngle = 115;
        PointF endPoint = calculatePoint(startPoint,FINS_LENGTH,fishAngle - 180);
        mPath.reset();
        mPath.moveTo(startPoint.x,startPoint.y);
        //控制点
        PointF controlPoint = calculatePoint(startPoint,FINS_LENGTH * 1.8f,
                isRight? fishAngle - controlAngle : fishAngle + controlAngle);
        mPath.quadTo(controlPoint.x,controlPoint.y,endPoint.x,endPoint.y);
        canvas.drawPath(mPath,mPaint);
    }

    /**
     * 求关键坐标点的核心算法
     * @param startPoint 相对点的坐标
     * @param length 相对点到要求的点之间的直线距离
     * @param angle 和fishAngle之间的角度
     * @return
     */
    public PointF calculatePoint(PointF startPoint, float length, float angle){
        //x
        float deltaX = (float) (Math.cos(Math.toRadians(angle)) * length);
        //Y
        float deltaY = (float) (Math.sin(Math.toRadians(angle + 180)) * length);
        return new PointF(startPoint.x+deltaX,startPoint.y+deltaY);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) (8.38f * HEAD_RADIUS);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) (8.38f * HEAD_RADIUS);
    }
}
