/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import me.zhanghai.android.douya.R;

public abstract class AnimatedSortedListAdapter<T, VH extends RecyclerView.ViewHolder>
        extends SortedListAdapter<T, VH> {

    private static final int ANIMATION_STAGGER_MILLIS = 20;

    private boolean mShouldStartAnimation;
    private int mAnimationStartOffset;

    private Handler mStopAnimationHandler = new Handler(Looper.getMainLooper());
    private final Runnable mStopAnimationRunnable = this::stopAnimation;

    private RecyclerView mRecyclerView;
    private final RecyclerView.OnScrollListener mClearAnimationListener =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    clearAnimation();
                }
            };

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
        mRecyclerView.addOnScrollListener(mClearAnimationListener);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        mRecyclerView.removeOnScrollListener(mClearAnimationListener);
        mRecyclerView = null;
    }

    @Override
    public void refresh() {
        resetAnimation();
        super.refresh();
    }

    @Override
    public void clear() {
        resetAnimation();
        super.clear();
    }

    protected void bindViewHolderAnimation(VH holder) {
        holder.itemView.clearAnimation();
        if (mShouldStartAnimation) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                    R.anim.list_item);
            animation.setStartOffset(mAnimationStartOffset);
            mAnimationStartOffset += ANIMATION_STAGGER_MILLIS;
            holder.itemView.startAnimation(animation);
            postStopAnimation();
        }
    }

    private void stopAnimation() {
        mStopAnimationHandler.removeCallbacks(mStopAnimationRunnable);
        mShouldStartAnimation = false;
        mAnimationStartOffset = 0;
    }

    private void postStopAnimation() {
        mStopAnimationHandler.removeCallbacks(mStopAnimationRunnable);
        mStopAnimationHandler.post(mStopAnimationRunnable);
    }

    private void clearAnimation() {
        stopAnimation();
        for (int i = 0, count = mRecyclerView.getChildCount(); i < count; ++i) {
            View child = mRecyclerView.getChildAt(i);
            child.clearAnimation();
        }
    }

    private void resetAnimation() {
        clearAnimation();
        mShouldStartAnimation = true;
    }
}
