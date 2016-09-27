# A FlipBoard Demo

---

>**一个翻转卡片**

![GIF](https://raw.githubusercontent.com/WaylanPunch/FlipBoard/master/demo_03.gif)

## 自定义翻转动画FlipAnimator

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


## 如何使用

		fb_sun.setOnFlipListener(new FlipBoard.OnFlipListener() {
            @Override
            public void onFlipStart(FlipBoard view) {
                boolean isFlipped = view.isFlipped();
                Log.d(TAG, "onFlipStart debug, isFlipped = " + isFlipped);
            }

            @Override
            public void onFlipEnd(FlipBoard view) {
                boolean isFlipped = view.isFlipped();
                Log.d(TAG, "onFlipEnd debug, isFlipped = " + isFlipped);
                if (isFlipped) {
                    Toast.makeText(MainActivity.this, "Back Side", Toast.LENGTH_SHORT).show();
                    rootView.setBackgroundResource(R.color.colorPrimary);
                } else {
                    Toast.makeText(MainActivity.this, "Front Side", Toast.LENGTH_SHORT).show();
                    rootView.setBackgroundResource(R.color.colorWhite);
                }
            }
        });

>通过**isFlipped()**方法获取当前视图是哪个卡片，如果为true则表示已翻转到反面卡片，false则为正面卡片。

## 我说完了