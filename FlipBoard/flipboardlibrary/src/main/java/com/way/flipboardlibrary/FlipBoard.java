package com.way.flipboardlibrary;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

/**
 * Created by pc on 2016/9/19.
 */

public class FlipBoard extends FrameLayout implements Animation.AnimationListener, View.OnClickListener, OnSwipeListener {

    public static final int ANIM_DURATION_MILLIS = 500;
    private static final Interpolator fDefaultInterpolator = new DecelerateInterpolator();
    private OnFlipListener listener;
    private FlipAnimator animator;
    private boolean isFlipped;
    private Direction direction;
    private OnSwipeTouchListener touchListener;
    private View frontView, backView;

    public FlipBoard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public FlipBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlipBoard(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        animator = new FlipAnimator();
        animator.setAnimationListener(this);
        animator.setInterpolator(fDefaultInterpolator);
        animator.setDuration(ANIM_DURATION_MILLIS);
        direction = Direction.DOWN;
        setSoundEffectsEnabled(true);
        touchListener = new OnSwipeTouchListener(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() > 2) {
            throw new IllegalStateException("FlipLayout can host only two direct children");
        }

        frontView = getChildAt(0);
        frontView.setOnTouchListener(touchListener);
        frontView.setOnClickListener(this);

        backView = getChildAt(1);
        backView.setOnTouchListener(touchListener);
        backView.setOnClickListener(this);

        touchListener.addSwipeListener(this);
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    private void toggleView() {
        if (frontView == null || backView == null) {
            return;
        }

        if (isFlipped) {
            frontView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.GONE);
        } else {
            frontView.setVisibility(View.GONE);
            backView.setVisibility(View.VISIBLE);
        }

        isFlipped = !isFlipped;
    }

    public void setOnFlipListener(OnFlipListener listener) {
        this.listener = listener;
    }

    public void reset() {
        isFlipped = false;
        direction = Direction.DOWN;
        frontView.setVisibility(View.VISIBLE);
        backView.setVisibility(View.GONE);
    }

    public void toggleUp() {
        direction = Direction.UP;
        startAnimation();
    }

    public void toggleDown() {
        direction = Direction.DOWN;
        startAnimation();
    }

    public void startAnimation() {
        animator.setVisibilitySwapped();
        startAnimation(animator);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (listener != null) {
            listener.onFlipStart(this);
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (listener != null) {
            listener.onFlipEnd(this);
        }
        direction = direction == Direction.UP ? Direction.DOWN : Direction.UP;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        animator.setAnimationListener(listener);
    }

    @Override
    public void onClick(View view) {
        toggleDown();
    }

    @Override
    public void onSwipeLeft() {

    }

    @Override
    public void onSwipeRight() {

    }

    @Override
    public void onSwipeUp() {
        toggleUp();
    }

    @Override
    public void onSwipeDown() {
        toggleDown();
    }

    private enum Direction {
        UP, DOWN
    }

    public interface OnFlipListener {

        void onFlipStart(FlipBoard view);

        void onFlipEnd(FlipBoard view);
    }

    public class FlipAnimator extends Animation {

        private static final float EXPERIMENTAL_VALUE = 50.f;
        private Camera camera;
        private float centerX;
        private float centerY;
        private boolean visibilitySwapped;

        public FlipAnimator() {
            setFillAfter(true);
        }

        public void setVisibilitySwapped() {
            visibilitySwapped = false;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            camera = new Camera();
            this.centerX = width / 2;
            this.centerY = height / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            //计算给定时间内Y轴坐标上的偏转角，根据弧度和角度公式获得
            final double radians = Math.PI * interpolatedTime;

            float degrees = (float) (180.0 * radians / Math.PI);

            if (direction == Direction.UP) {
                degrees = -degrees;
            }


            //动画播放到中点时，需要隐藏初始视图，展示最终视图，增加或减少180°的差值，避免最终视图翻转过去
            if (interpolatedTime >= 0.5f) {
                if (direction == Direction.UP) {
                    degrees += 180.f;
                }

                if (direction == Direction.DOWN) {
                    degrees -= 180.f;
                }

                if (!visibilitySwapped) {
                    toggleView();
                    visibilitySwapped = true;
                }
            }

            final Matrix matrix = t.getMatrix();

            camera.save();
            //可删除此行代码，镜头在各轴上的位移效果
            camera.translate(1.5f, 1.5f, (float) (EXPERIMENTAL_VALUE * Math.sin(radians)));
            camera.rotateX(degrees);
            camera.rotateY(0);
            camera.rotateZ(0);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }
}
