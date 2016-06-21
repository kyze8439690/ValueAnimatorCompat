package me.yugy.github.valueanimatorcompat;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * A 'fake' ValueAnimator implementation which uses a Runnable.
 */
class ValueAnimatorCompatImplEclairMr1 extends ValueAnimatorCompat.Impl {

    private static final int HANDLER_DELAY = 10;
    private static final int DEFAULT_DURATION = 200;

    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private long mStartTime;
    private boolean mIsRunning;

    private final int[] mIntValues = new int[2];
    private final float[] mFloatValues = new float[2];

    private int mDuration = DEFAULT_DURATION;
    private Interpolator mInterpolator;
    private AnimatorListenerProxy mListener;
    private AnimatorUpdateListenerProxy mUpdateListener;

    private float mAnimatedFraction;


    // The number of times the animation will repeat. The default is 0, which means the animation
    // will play only once
    private int mRepeatCount = 0;

    private int mCurrentIteration = 0;

    /**
     * The type of repetition that will occur when repeatMode is nonzero. RESTART means the
     * animation will start from the beginning on every new cycle. REVERSE means the animation
     * will reverse directions on each iteration.
     */
    private int mRepeatMode = ValueAnimatorCompat.RESTART;

    @Override
    void setRepeatMode(int value) {
        mRepeatMode = value;
    }

    @Override
    int getRepeatMode() {
        return mRepeatMode;
    }

    @Override
    void setRepeatCount(int value) {
        mRepeatCount = value;
    }

    @Override
    int getRepeatCount() {
        return mRepeatCount;
    }

    @Override
    public void start() {
        if (mIsRunning) {
            // If we're already running, ignore
            return;
        }

        if (mInterpolator == null) {
            mInterpolator = new AccelerateDecelerateInterpolator();
        }

        mStartTime = SystemClock.uptimeMillis();
        mIsRunning = true;

        if (mListener != null) {
            mListener.onAnimationStart();
        }

        sHandler.postDelayed(mRunnable, HANDLER_DELAY);
    }

    @Override
    public boolean isRunning() {
        return mIsRunning;
    }

    @Override
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    @Override
    public void setListener(AnimatorListenerProxy listener) {
        mListener = listener;
    }

    @Override
    public void setUpdateListener(AnimatorUpdateListenerProxy updateListener) {
        mUpdateListener = updateListener;
    }

    @Override
    public void setIntValues(int from, int to) {
        mIntValues[0] = from;
        mIntValues[1] = to;
    }

    @Override
    public int getAnimatedIntValue() {
        return lerp(mIntValues[0], mIntValues[1], getAnimatedFraction());
    }

    @Override
    public void setFloatValues(float from, float to) {
        mFloatValues[0] = from;
        mFloatValues[1] = to;
    }

    @Override
    public float getAnimatedFloatValue() {
        return lerp(mFloatValues[0], mFloatValues[1], getAnimatedFraction());
    }

    @Override
    public void setDuration(int duration) {
        mDuration = duration;
    }

    @Override
    public void cancel() {
        mIsRunning = false;
        sHandler.removeCallbacks(mRunnable);

        if (mListener != null) {
            mListener.onAnimationCancel();
        }
    }

    @Override
    public float getAnimatedFraction() {
        return mAnimatedFraction;
    }

    @Override
    public void end() {
        if (mIsRunning) {
            mIsRunning = false;
            sHandler.removeCallbacks(mRunnable);

            // Set our animated fraction to 1
            mAnimatedFraction = 1f;

            if (mUpdateListener != null) {
                mUpdateListener.onAnimationUpdate();
            }

            if (mListener != null) {
                mListener.onAnimationEnd();
            }
        }
    }

    private void update() {
        if (mIsRunning) {
            // Update the animated fraction
            final long elapsed = SystemClock.uptimeMillis() - mStartTime;
            final float linearFraction = elapsed / (float) mDuration;
            mAnimatedFraction = mInterpolator != null
                    ? mInterpolator.getInterpolation(linearFraction)
                    : linearFraction;

            // If we're running, dispatch tp the listener
            if (mUpdateListener != null) {
                mUpdateListener.onAnimationUpdate();
            }

            // Check to see if we've passed the animation duration
            if (SystemClock.uptimeMillis() >= (mStartTime + mDuration)) {
                if ((mRepeatCount == ValueAnimatorCompat.INFINITE && mRepeatMode == ValueAnimatorCompat.RESTART)
                        || mRepeatCount > 0 && mRepeatMode == ValueAnimatorCompat.RESTART && mCurrentIteration <= mRepeatCount) {
                    mStartTime = SystemClock.uptimeMillis();
                    if (mListener != null) {
                        mListener.onAnimationRepeat();
                    }
                    mCurrentIteration++;
                } else {
                    mIsRunning = false;

                    if (mListener != null) {
                        mListener.onAnimationEnd();
                    }
                }
            }
        }

        if (mIsRunning) {
            // If we're still running, post another delayed runnable
            sHandler.postDelayed(mRunnable, HANDLER_DELAY);
        }
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            update();
        }
    };

    /**
     * Linear interpolation between {@code startValue} and {@code endValue} by {@code fraction}.
     */
    static float lerp(float startValue, float endValue, float fraction) {
        return startValue + (fraction * (endValue - startValue));
    }


    static int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (endValue - startValue));
    }
}

