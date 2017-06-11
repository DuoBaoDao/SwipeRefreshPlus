package com.gu.swiperefreshplush.extention;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.apkfuns.logutils.LogUtils;
import com.gu.swiperefresh.IRefreshViewController;
import com.gu.swiperefresh.SwipeRefreshPlus;
import com.gu.swiperefresh.Utils.Size;
import com.gu.swiperefresh.ZIndex;
import com.gu.swiperefreshplush.R;

/**
 * Created by GUHY on 2017/4/18.
 */

public class MRefreshViewController implements IRefreshViewController {
    private static final int DEFAULT_POSITION = 100;
    private static final int DEFAULT_PULL_UP_DURATION = 300;
    private static final int DEFAULT_PULL_DOWWN_DURATION = 500;
    private int mOriginOffset;
    private int mTargetPosition;
    private int mCurrentOffsetTop;
    private Context mContext;
    private View mParent;
    private View mTarget;
    private boolean refreshing;
    private boolean mNotify;
    private float mTotalDiatance;

    private RefreshViewLayout mRefreshView;
    private SwipeRefreshPlus.OnRefreshListener mOnRefreshListener;

    private ValueAnimator mPullDownAnimation ;
    private ValueAnimator mPullUpAnimation;


    private Animator.AnimatorListener mPullUpListener = new  Animator.AnimatorListener(){

        @Override
        public void onAnimationStart(Animator animation) {
            mRefreshView.reset();
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
    private Animator.AnimatorListener mPullDownListener=new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public MRefreshViewController(Context context, View parent, View target) {
        mContext = context;
        mParent = parent;
        mTarget = target;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mTargetPosition = (int) (DEFAULT_POSITION * metrics.density);
        mCurrentOffsetTop = -mTargetPosition;
        mOriginOffset = mCurrentOffsetTop;

    }

    @Override
    public void reset() {
        mCurrentOffsetTop = -mTargetPosition;
    }

    @Override
    public View create() {
        mRefreshView = new RefreshViewLayout(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRefreshView.setLayoutParams(params);
        mRefreshView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        mRefreshView.setDefaultThreshold(mTargetPosition);
        return mRefreshView;
    }

    @Override
    public int getZIndex() {
        return ZIndex.TOP;
    }

    @Override
    public int getCurrentTargetOffsetTop() {
        return mCurrentOffsetTop;
    }


    @Override
    public boolean isRefresh() {
        return refreshing;
    }

    @Override
    public int showPullRefresh(float overscrollTop) {
        if (overscrollTop <= mTargetPosition) {
            mParent.scrollTo(0, (int) -overscrollTop);
            mCurrentOffsetTop = (int) (mOriginOffset+overscrollTop);
        } else {
            mRefreshView.pullDown((int) overscrollTop-mTargetPosition);
        }
        return 0;
    }

    @Override
    public float finishPullRefresh(float overscrollTop) {

        mTotalDiatance=overscrollTop;
        if (overscrollTop > mTargetPosition) {
            pullUpAnimation();
        } else {
            pullUpAnimation();
        }
        return 0;
    }

    @Override
    public void startProgress() {
        //setRefreshing(true,true);
    }

    @Override
    public void setTargetOffsetTopAndBottom(int i, boolean b) {
        LogUtils.d(i);
        ViewCompat.offsetTopAndBottom(mParent, i);
        mCurrentOffsetTop = mRefreshView.getTop();
    }

    @Override
    public void setRefreshListener(SwipeRefreshPlus.OnRefreshListener mListener) {
        mOnRefreshListener = mListener;
    }

    @Override
    public void setRefreshing(boolean refresh) {
        if (refresh && refreshing != refresh) {
            // scale and show
            refreshing = refresh;
            int endTarget = 0;

            setTargetOffsetTopAndBottom(endTarget - mCurrentOffsetTop,
                    true /* requires update */);
            mNotify = false;
            pullDownAnimation();
        } else {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void pullDownAnimation() {
        if(mPullDownAnimation!=null){
            mPullDownAnimation.cancel();
        }
        mPullDownAnimation=ValueAnimator.ofInt(mParent.getTop(),mTargetPosition);
        mPullDownAnimation
                .setDuration(DEFAULT_PULL_DOWWN_DURATION)
                .addListener(mPullDownListener);
        mPullDownAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mParent.scrollTo(0, (int) animation.getAnimatedValue());
                mCurrentOffsetTop=mRefreshView.getTop();
            }
        });
        mPullDownAnimation.start();
    }

    private void pullUpAnimation() {
        if(mPullUpAnimation!=null){
            mPullUpAnimation.cancel();
        }
        mPullUpAnimation = ValueAnimator.ofInt(-mParent.getTop(), 0);
        mPullUpAnimation.setDuration(DEFAULT_PULL_UP_DURATION);
        mPullUpAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LogUtils.d(animation.getAnimatedValue());
                mParent.scrollTo(0, (int) animation.getAnimatedValue());
                mCurrentOffsetTop=mRefreshView.getTop();
            }
        });
        mPullUpAnimation.addListener(mPullUpListener);
        mPullUpAnimation.start();

    }

    private void setRefreshing(boolean refresh, final boolean notify) {
        if (refreshing != refresh) {
            mNotify = notify;
            // ensureTarget();
            refreshing = refresh;
            if (refreshing) {
                // animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener);
            } else {
                mRefreshView.reset();
                pullUpAnimation();
            }
        }
    }

}
