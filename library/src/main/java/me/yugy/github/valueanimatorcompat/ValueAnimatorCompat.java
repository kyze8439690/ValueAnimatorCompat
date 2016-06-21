package me.yugy.github.valueanimatorcompat;

import android.os.Build;
import android.view.animation.Interpolator;

/**
 * This class offers a very small subset of {@code ValueAnimator}'s API, but works pre-v11 too.
 * <p>
 * You shouldn't not instantiate this directly. Instead use {@code ValueAnimatorCompat.createAnimator()}.
 */
public class ValueAnimatorCompat {

    /**
     * When the animation reaches the end and <code>repeatCount</code> is INFINITE
     * or a positive value, the animation restarts from the beginning.
     */
    public static final int RESTART = 1;

    /**
     * This value used used with the setRepeatCount(int) property to repeat
     * the animation indefinitely.
     */
    public static final int INFINITE = -1;

    public static ValueAnimatorCompat createAnimator() {
        return new ValueAnimatorCompat(Build.VERSION.SDK_INT >= 12
                ? new ValueAnimatorCompatImplEclairMr1()
                : new ValueAnimatorCompatImplEclairMr1());
    }

    interface AnimatorUpdateListener {
        /**
         * <p>Notifies the occurrence of another frame of the animation.</p>
         *
         * @param animator The animation which was repeated.
         */
        void onAnimationUpdate(ValueAnimatorCompat animator);
    }

    /**
     * An animation listener receives notifications from an animation.
     * Notifications indicate animation related events, such as the end or the
     * repetition of the animation.
     */
    interface AnimatorListener {
        /**
         * <p>Notifies the start of the animation.</p>
         *
         * @param animator The started animation.
         */
        void onAnimationStart(ValueAnimatorCompat animator);
        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         *
         * @param animator The animation which reached its end.
         */
        void onAnimationEnd(ValueAnimatorCompat animator);
        /**
         * <p>Notifies the cancellation of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         *
         * @param animator The animation which was canceled.
         */
        void onAnimationCancel(ValueAnimatorCompat animator);

        /**
         * <p>Notifies the repetition of the animation.</p>
         *
         * @param animator The animation which was repeated.
         */
        void onAnimationRepeat(ValueAnimatorCompat animator);
    }

    static class AnimatorListenerAdapter implements AnimatorListener {
        @Override
        public void onAnimationStart(ValueAnimatorCompat animator) {
        }

        @Override
        public void onAnimationEnd(ValueAnimatorCompat animator) {
        }

        @Override
        public void onAnimationCancel(ValueAnimatorCompat animator) {
        }

        @Override
        public void onAnimationRepeat(ValueAnimatorCompat animator) {

        }
    }

    static abstract class Impl {
        interface AnimatorUpdateListenerProxy {
            void onAnimationUpdate();
        }

        interface AnimatorListenerProxy {
            void onAnimationStart();
            void onAnimationEnd();
            void onAnimationCancel();
            void onAnimationRepeat();
        }

        abstract void setRepeatMode(int value);
        abstract int getRepeatMode();
        abstract void setRepeatCount(int value);
        abstract int getRepeatCount();
        abstract void start();
        abstract boolean isRunning();
        abstract void setInterpolator(Interpolator interpolator);
        abstract void setListener(AnimatorListenerProxy listener);
        abstract void setUpdateListener(AnimatorUpdateListenerProxy updateListener);
        abstract void setIntValues(int from, int to);
        abstract int getAnimatedIntValue();
        abstract void setFloatValues(float from, float to);
        abstract float getAnimatedFloatValue();
        abstract void setDuration(int duration);
        abstract void cancel();
        abstract float getAnimatedFraction();
        abstract void end();
    }

    private final Impl mImpl;

    ValueAnimatorCompat(Impl impl) {
        mImpl = impl;
    }

    public void start() {
        mImpl.start();
    }

    public boolean isRunning() {
        return mImpl.isRunning();
    }

    public void setInterpolator(Interpolator interpolator) {
        mImpl.setInterpolator(interpolator);
    }

    public void setUpdateListener(final AnimatorUpdateListener updateListener) {
        if (updateListener != null) {
            mImpl.setUpdateListener(new Impl.AnimatorUpdateListenerProxy() {
                @Override
                public void onAnimationUpdate() {
                    updateListener.onAnimationUpdate(ValueAnimatorCompat.this);
                }
            });
        } else {
            mImpl.setUpdateListener(null);
        }
    }

    public void setListener(final AnimatorListener listener) {
        if (listener != null) {
            mImpl.setListener(new Impl.AnimatorListenerProxy() {
                @Override
                public void onAnimationStart() {
                    listener.onAnimationStart(ValueAnimatorCompat.this);
                }

                @Override
                public void onAnimationEnd() {
                    listener.onAnimationEnd(ValueAnimatorCompat.this);
                }

                @Override
                public void onAnimationCancel() {
                    listener.onAnimationCancel(ValueAnimatorCompat.this);
                }

                @Override
                public void onAnimationRepeat() {
                    listener.onAnimationRepeat(ValueAnimatorCompat.this);
                }
            });
        } else {
            mImpl.setListener(null);
        }
    }

    public void setIntValues(int from, int to) {
        mImpl.setIntValues(from, to);
    }

    public int getAnimatedIntValue() {
        return mImpl.getAnimatedIntValue();
    }

    public void setFloatValues(float from, float to) {
        mImpl.setFloatValues(from, to);
    }

    public float getAnimatedFloatValue() {
        return mImpl.getAnimatedFloatValue();
    }

    public void setDuration(int duration) {
        mImpl.setDuration(duration);
    }

    public void cancel() {
        mImpl.cancel();
    }

    public float getAnimatedFraction() {
        return mImpl.getAnimatedFraction();
    }

    public void end() {
        mImpl.end();
    }

    public void setRepeatMode(int value) {
        mImpl.setRepeatMode(value);
    }

    public int getRepeatMode() {
        return mImpl.getRepeatMode();
    }

    public void setRepeatCount(int value) {
        mImpl.setRepeatCount(value);
    }
    public int getRepeatCount() {
        return mImpl.getRepeatCount();
    }
}
