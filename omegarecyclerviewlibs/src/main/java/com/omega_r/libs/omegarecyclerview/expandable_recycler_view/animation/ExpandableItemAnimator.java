package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpandableItemAnimator extends SimpleItemAnimator {

    private static final String TAG = ExpandableItemAnimator.class.getName();

    private final Runnable mPendingCleaner = new Runnable() {
        @Override
        public void run() {
            mPendingChanges.clear();
        }
    };

    private AnimationHelper.PendingChanges mPendingChanges = new AnimationHelper.PendingChanges();

    private ArrayList<ArrayList<ViewHolder>> mAdditionsList = new ArrayList<>();
    private ArrayList<ArrayList<AnimationHelper.MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<ArrayList<AnimationHelper.ChangeInfo>> mChangesList = new ArrayList<>();

    private ArrayList<ViewHolder> mAddAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mMoveAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mRemoveAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mChangeAnimations = new ArrayList<>();
    private List<OnAnimationEndListener> mOnRemoveAnimationEndListeners = new ArrayList<>();
    private List<OnAnimationEndListener> mOnAddAnimationEndListeners = new ArrayList<>();

    protected abstract void onRemoveStart(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void setupRemoveAnimation(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onRemoveCancel(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onRemoveEnd(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onAddStart(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void setupAddAnimation(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onAddCancel(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onAddEnd(ViewPropertyAnimator animation, final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract boolean shouldReverseAddOrder();

    protected abstract boolean shouldReverseRemoveOrder();

    protected abstract boolean isNeedAddingDelay();

    @Override
    public void runPendingAnimations() {
        boolean removalsPending = !mPendingChanges.removals.isEmpty();
        boolean movesPending = !mPendingChanges.moves.isEmpty();
        boolean changesPending = !mPendingChanges.changes.isEmpty();
        boolean additionsPending = !mPendingChanges.additions.isEmpty();

        if (removalsPending) runRemoveActions();
        if (movesPending) runMoveActions(removalsPending);
        if (changesPending) runChangesActions(removalsPending);
        if (additionsPending) runAddActions(removalsPending, movesPending, changesPending);

        View someView = null;
        if (removalsPending) someView = mPendingChanges.removals.get(0).itemView;
        if (someView == null && movesPending) someView = mPendingChanges.moves.get(0).holder.itemView;
        if (someView == null && changesPending) someView = mPendingChanges.changes.get(0).oldHolder.itemView;
        if (someView == null && additionsPending) someView = mPendingChanges.additions.get(0).itemView;
        if (someView != null) {
            ViewCompat.postOnAnimationDelayed(someView, mPendingCleaner, getTotalDelay(removalsPending, movesPending, changesPending));
        }
    }

    private void runRemoveActions() {
        proceedWithAnimationHelper(mPendingChanges.removals, new UnVoidFunction<ViewHolder>() {
            @Override
            public void apply(ViewHolder param) {
                runRemoveAnimation(param);
            }
        }, false);
    }

    private void runMoveActions(boolean hasRemovals) {
        final ArrayList<AnimationHelper.MoveInfo> moves = new ArrayList<>(mPendingChanges.moves);
        mMovesList.add(moves);
        Runnable mover = new Runnable() {
            @Override
            public void run() {
                for (AnimationHelper.MoveInfo moveInfo : moves) {
                    runMoveAnimation(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
                }
                moves.clear();
                mMovesList.remove(moves);
            }
        };
        if (hasRemovals) {
            View view = moves.get(0).holder.itemView;
            ViewCompat.postOnAnimationDelayed(view, mover, getRemoveDuration());
        } else {
            mover.run();
        }
    }

    private void runChangesActions(boolean hasRemovals) {
        final ArrayList<AnimationHelper.ChangeInfo> changes = new ArrayList<>(mPendingChanges.changes);
        mChangesList.add(changes);
        Runnable changer = new Runnable() {
            @Override
            public void run() {
                for (AnimationHelper.ChangeInfo change : changes) {
                    runChangeAnimation(change);
                }
                changes.clear();
                mChangesList.remove(changes);
            }
        };
        if (hasRemovals) {
            ViewHolder holder = changes.get(0).oldHolder;
            ViewCompat.postOnAnimationDelayed(holder.itemView, changer, getRemoveDuration());
        } else {
            changer.run();
        }
    }

    private void runAddActions(boolean hasRemovals, boolean hasMoves, boolean hasChanges) {
        final ArrayList<ViewHolder> groupAdditions = new ArrayList<>();
        final ArrayList<ViewHolder> childAdditions = new ArrayList<>();
        for (ViewHolder addition : mPendingChanges.additions) {
            if (addition instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                childAdditions.add(addition);
            } else {
                groupAdditions.add(addition);
            }
        }

        scheduleAddAnimation(groupAdditions, true, hasRemovals, hasMoves, hasChanges);
        scheduleAddAnimation(childAdditions, false, hasRemovals, hasMoves, hasChanges);
    }

    private void scheduleAddAnimation(final ArrayList<ViewHolder> additions, boolean forceDelay, boolean hasRemovals, boolean hasMoves, boolean hasChanges) {
        if (!additions.isEmpty()) {
            mAdditionsList.add(additions);
            Runnable adder = new Runnable() {
                public void run() {
                    proceedWithAnimationHelper(additions, new UnVoidFunction<ViewHolder>() {
                        @Override
                        public void apply(ViewHolder param) {
                            runAddAnimation(param);
                        }
                    }, true);
                    mAdditionsList.remove(additions);
                }
            };
            if (hasRemovals || hasMoves || hasChanges) {
                View view = additions.get(0).itemView;
                long totalDelay = getTotalDelay(hasRemovals, hasMoves, hasChanges);
                ViewCompat.postOnAnimationDelayed(view, adder, forceDelay || isNeedAddingDelay() ? totalDelay : 0L);
            } else {
                adder.run();
            }
        }
    }

    private long getTotalDelay(boolean hasRemovals, boolean hasMoves, boolean hasChanges) {
        long removeDuration = hasRemovals ? getRemoveDuration() : 0;
        long moveDuration = hasMoves ? getMoveDuration() : 0;
        long changeDuration = hasChanges ? getChangeDuration() : 0;
        return removeDuration + Math.max(moveDuration, changeDuration);
    }

    private void proceedWithAnimationHelper(List<ViewHolder> holders, UnVoidFunction<ViewHolder> func, boolean isAdding) {
        int childChangesCount = 0;
        for (int i = 0; i < holders.size(); i++) {
            ViewHolder holder = holders.get(i);
            if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder childViewHolder =
                        (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder;
                int rememberedAdapterPosition = childViewHolder.animationHelper.visibleAdapterPosition;
                childViewHolder.animationHelper.clear();
                childViewHolder.animationHelper.visibleAdapterPosition = rememberedAdapterPosition;
                childViewHolder.animationHelper.height = childViewHolder.itemView.getHeight();
                childViewHolder.animationHelper.width = childViewHolder.itemView.getWidth();
                childViewHolder.animationHelper.setPendingChanges(mPendingChanges);

                childViewHolder.animationHelper.upperViewHolder = tryGetOffsettedViewHolder(holders, childViewHolder, -1);
                childViewHolder.animationHelper.lowerViewHolder = tryGetOffsettedViewHolder(holders, childViewHolder, 1);

                childChangesCount++;
            }
        }

        if (isAdding && shouldReverseAddOrder() || !isAdding && shouldReverseRemoveOrder()) {
            for (int i = holders.size() - 1; i >= 0; i--) {
                applyViewHolder(holders.get(i), func, childChangesCount, i);
            }
        } else {
            for (int i = 0; i < holders.size(); i++) {
                applyViewHolder(holders.get(i), func, childChangesCount, i);
            }
        }
    }

    @Nullable
    private OmegaExpandableRecyclerView.Adapter.ChildViewHolder tryGetOffsettedViewHolder(
            List<ViewHolder> holders,
            OmegaExpandableRecyclerView.Adapter.ChildViewHolder forHolder,
            int offset) {
        int adapterPosition = forHolder.getAdapterPosition();
        int targetAdapterPosition =
                (adapterPosition == RecyclerView.NO_POSITION ? forHolder.animationHelper.visibleAdapterPosition : adapterPosition)
                        + offset;
        for (ViewHolder holder : holders) {
            if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder;
                int holderAdapterPosition = cvh.getAdapterPosition() == RecyclerView.NO_POSITION ?
                        cvh.animationHelper.visibleAdapterPosition :
                        cvh.getAdapterPosition();
                if (holderAdapterPosition == targetAdapterPosition) {
                    return cvh;
                }
            }
        }
        return null;
    }

    private void applyViewHolder(ViewHolder holder, UnVoidFunction<ViewHolder> func, int childChangesCount, int positionInChanges) {
        if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
            OmegaExpandableRecyclerView.Adapter.ChildViewHolder childHolder =
                    (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder;
            childHolder.animationHelper.totalChanges = childChangesCount;
            childHolder.animationHelper.positionInChanges = positionInChanges;
        }

        func.apply(holder);
    }

    @Override
    public boolean animateRemove(final ViewHolder holder) {
        endAnimation(holder);
        mPendingChanges.removals.add(holder);
        return true;
    }

    private void runRemoveAnimation(ViewHolder holder) {
        if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
            animateRemoveChild((OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder);
        } else {
            animateRemoveGroup(holder);
        }
        mRemoveAnimations.add(holder);
    }

    private void animateRemoveGroup(final ViewHolder holder) {
        final ViewPropertyAnimator animation = holder.itemView.animate();
        animation
                .setDuration(getRemoveDuration())
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        dispatchRemoveStarting(holder);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        holder.itemView.setAlpha(1.0f);
                        dispatchRemoveFinished(holder);
                        mRemoveAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
    }

    private void animateRemoveChild(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        onRemoveStart(holder);
        final ViewPropertyAnimator animation = holder.contentView.animate();
        setupRemoveAnimation(animation, holder);
        animation
                .setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        dispatchRemoveStarting(holder);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        onRemoveCancel(animation, holder);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        onRemoveEnd(animation, holder);
                        dispatchRemoveFinished(holder);
                        mRemoveAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                        notifyRemoveAnimationEnds();
                    }
                })
                .start();
    }

    private void notifyRemoveAnimationEnds() {
        for (OnAnimationEndListener listener : mOnRemoveAnimationEndListeners) {
            listener.onAnimationEnd();
        }
        mOnRemoveAnimationEndListeners.clear();
    }

    @Override
    public boolean animateAdd(final ViewHolder holder) {
        makeHolderInvisible(holder);
        endAnimation(holder);
        mPendingChanges.additions.add(holder);
        return true;
    }

    private void makeHolderInvisible(ViewHolder holder) {
        if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
            OmegaExpandableRecyclerView.Adapter.ChildViewHolder cvh = (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder;
            cvh.contentView.setAlpha(0f);
            cvh.itemView.setAlpha(1f);
        } else {
            holder.itemView.setAlpha(0f);
        }
    }

    private void runAddAnimation(final ViewHolder holder) {
        if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
            animateAddChild((OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder);
        } else {
            animateAddGroup(holder);
        }
        mAddAnimations.add(holder);
    }

    private void animateAddGroup(final ViewHolder holder) {
        final ViewPropertyAnimator animation = holder.itemView.animate();
        animation
                .alpha(1f)
                .setDuration(getAddDuration())
                .setListener(new AnimatorListenerAdapter() {

                    public void onAnimationStart(Animator animator) {
                        dispatchAddStarting(holder);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        holder.itemView.setAlpha(1f);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        holder.itemView.setAlpha(1f);
                        dispatchAddFinished(holder);
                        mAddAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }

                })
                .start();
    }

    private void animateAddChild(final OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder) {
        onAddStart(holder);
        final ViewPropertyAnimator animation = holder.contentView.animate();
        setupAddAnimation(animation, holder);
        animation
                .setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        dispatchAddStarting(holder);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        onAddCancel(animation, holder);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        onAddEnd(animation, holder);
                        dispatchAddFinished(holder);
                        mAddAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                        notifyAddAnimationEnds();
                    }
                })
                .start();
    }

    private void notifyAddAnimationEnds() {
        for (OnAnimationEndListener listener : mOnAddAnimationEndListeners) {
            listener.onAnimationEnd();
        }
        mOnAddAnimationEndListeners.clear();
    }

    @Override
    public boolean animateMove(final ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        View view = holder.itemView;
        fromX += (int) holder.itemView.getTranslationX();
        fromY += (int) holder.itemView.getTranslationY();
        endAnimation(holder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if (deltaX == 0 && deltaY == 0) {
            this.dispatchMoveFinished(holder);
            return false;
        } else {
            if (deltaX != 0) {
                view.setTranslationX((float) (-deltaX));
            }

            if (deltaY != 0) {
                view.setTranslationY((float) (-deltaY));
            }

            mPendingChanges.moves.add(new AnimationHelper.MoveInfo(holder, fromX, fromY, toX, toY));
            return true;
        }
    }

    private void runMoveAnimation(final ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;
        if (deltaX != 0) {
            view.animate().translationX(0.0F);
        }

        if (deltaY != 0) {
            view.animate().translationY(0.0F);
        }

        final ViewPropertyAnimator animation = view.animate();
        this.mMoveAnimations.add(holder);
        animation.setDuration(this.getMoveDuration()).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                dispatchMoveStarting(holder);
            }

            public void onAnimationCancel(Animator animator) {
                if (deltaX != 0) {
                    view.setTranslationX(0.0F);
                }

                if (deltaY != 0) {
                    view.setTranslationY(0.0F);
                }

            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                dispatchMoveFinished(holder);
                mMoveAnimations.remove(holder);
                dispatchFinishedWhenDone();
            }
        }).start();
    }

    @Override
    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder,
                                 int fromX, int fromY, int toX, int toY) {
        if (oldHolder == newHolder) {
            return this.animateMove(oldHolder, fromX, fromY, toX, toY);
        } else {
            float prevTranslationX = oldHolder.itemView.getTranslationX();
            float prevTranslationY = oldHolder.itemView.getTranslationY();
            float prevAlpha = oldHolder.itemView.getAlpha();
            endAnimation(oldHolder);
            int deltaX = (int) ((float) (toX - fromX) - prevTranslationX);
            int deltaY = (int) ((float) (toY - fromY) - prevTranslationY);
            oldHolder.itemView.setTranslationX(prevTranslationX);
            oldHolder.itemView.setTranslationY(prevTranslationY);
            oldHolder.itemView.setAlpha(prevAlpha);
            if (newHolder != null) {
                endAnimation(newHolder);
                newHolder.itemView.setTranslationX((float) (-deltaX));
                newHolder.itemView.setTranslationY((float) (-deltaY));
                makeHolderInvisible(newHolder);
            }

            mPendingChanges.changes.add(new AnimationHelper.ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
            return true;
        }
    }

    private void runChangeAnimation(final AnimationHelper.ChangeInfo changeInfo) {
        ViewHolder holder = changeInfo.oldHolder;
        final View view = holder == null ? null : holder.itemView;
        ViewHolder newHolder = changeInfo.newHolder;
        final View newView = newHolder != null ? newHolder.itemView : null;

        if (view != null) {
            mChangeAnimations.add(changeInfo.oldHolder);
            final ViewPropertyAnimator animation = view.animate();
            animation
                    .setDuration(this.getChangeDuration())
                    .translationX((float) (changeInfo.toX - changeInfo.fromX))
                    .translationY((float) (changeInfo.toY - changeInfo.fromY))
                    .alpha(0.0F)
                    .setListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animator) {
                            dispatchChangeStarting(changeInfo.oldHolder, true);
                        }

                        public void onAnimationEnd(Animator animator) {
                            animation.setListener(null);
                            view.setAlpha(1.0F);
                            view.setTranslationX(0.0F);
                            view.setTranslationY(0.0F);
                            dispatchChangeFinished(changeInfo.oldHolder, true);
                            mChangeAnimations.remove(changeInfo.oldHolder);
                            dispatchFinishedWhenDone();
                        }
                    }).start();
        }

        if (newView != null) {
            final ViewPropertyAnimator animation = newView.animate();
            mChangeAnimations.add(changeInfo.newHolder);
            animation
                    .translationX(0.0F)
                    .translationY(0.0F)
                    .setDuration(this.getChangeDuration())
                    .alpha(1.0F)
                    .setListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animator) {
                            dispatchChangeStarting(changeInfo.newHolder, false);
                        }

                        public void onAnimationEnd(Animator animator) {
                            animation.setListener(null);
                            newView.setAlpha(1.0F);
                            newView.setTranslationX(0.0F);
                            newView.setTranslationY(0.0F);
                            dispatchChangeFinished(changeInfo.newHolder, false);
                            mChangeAnimations.remove(changeInfo.newHolder);
                            dispatchFinishedWhenDone();
                        }
                    })
                    .start();
        }
    }

    private void endChangeAnimation(List<AnimationHelper.ChangeInfo> infoList, ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            AnimationHelper.ChangeInfo changeInfo = infoList.get(i);
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo);
                }
            }
        }
    }

    private void endChangeAnimationIfNecessary(AnimationHelper.ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(AnimationHelper.ChangeInfo changeInfo, ViewHolder item) {
        boolean oldItem = false;
        if (changeInfo.newHolder == item) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder == item) {
            changeInfo.oldHolder = null;
            oldItem = true;
        } else {
            return false;
        }
        item.itemView.setAlpha(1);
        item.itemView.setTranslationX(0);
        item.itemView.setTranslationY(0);
        dispatchChangeFinished(item, oldItem);
        return true;
    }

    @Override
    public void endAnimation(@NonNull ViewHolder item) {
        View view = item.itemView;
        view.animate().cancel();
        for (int i = mPendingChanges.moves.size() - 1; i >= 0; i--) {
            AnimationHelper.MoveInfo moveInfo = mPendingChanges.moves.get(i);
            if (moveInfo.holder == item) {
                view.setTranslationX(0);
                view.setTranslationY(0);
                dispatchMoveFinished(item);
                mPendingChanges.moves.remove(item);
            }
        }
        endChangeAnimation(mPendingChanges.changes, item);
        if (mPendingChanges.removals.remove(item)) {
            view.setAlpha(1);
            dispatchRemoveFinished(item);
        }
        if (mPendingChanges.additions.remove(item)) {
            view.setAlpha(1);
            dispatchAddFinished(item);
        }
        for (int i = mChangesList.size() - 1; i >= 0; i--) {
            ArrayList<AnimationHelper.ChangeInfo> changes = mChangesList.get(i);
            endChangeAnimation(changes, item);
            if (changes.isEmpty()) {
                mChangesList.remove(changes);
            }
        }
        for (int i = mMovesList.size() - 1; i >= 0; i--) {
            ArrayList<AnimationHelper.MoveInfo> moves = mMovesList.get(i);
            for (int j = moves.size() - 1; j >= 0; j--) {
                AnimationHelper.MoveInfo moveInfo = moves.get(j);
                if (moveInfo.holder == item) {
                    view.setTranslationY(0);
                    view.setTranslationX(0);
                    dispatchMoveFinished(item);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        mMovesList.remove(moves);
                    }
                    break;
                }
            }
        }
        for (int i = mAdditionsList.size() - 1; i >= 0; i--) {
            ArrayList<ViewHolder> additions = mAdditionsList.get(i);
            if (additions.remove(item)) {
                view.setAlpha(1);
                dispatchAddFinished(item);
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions);
                }
            }
        }
        dispatchFinishedWhenDone();
    }

    @Override
    public boolean isRunning() {
        return (!mPendingChanges.additions.isEmpty() ||
                !mPendingChanges.changes.isEmpty() ||
                !mPendingChanges.moves.isEmpty() ||
                !mPendingChanges.removals.isEmpty() ||
                !mMoveAnimations.isEmpty() ||
                !mRemoveAnimations.isEmpty() ||
                !mAddAnimations.isEmpty() ||
                !mChangeAnimations.isEmpty() ||
                !mMovesList.isEmpty() ||
                !mAdditionsList.isEmpty() ||
                !mChangesList.isEmpty());
    }

    private void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    @Override
    public void endAnimations() {
        int count = mPendingChanges.moves.size();
        for (int i = count - 1; i >= 0; i--) {
            AnimationHelper.MoveInfo item = mPendingChanges.moves.get(i);
            View view = item.holder.itemView;
            view.setTranslationY(0);
            view.setTranslationX(0);
            dispatchMoveFinished(item.holder);
            mPendingChanges.moves.remove(i);
        }
        count = mPendingChanges.removals.size();
        for (int i = count - 1; i >= 0; i--) {
            ViewHolder item = mPendingChanges.removals.get(i);
            dispatchRemoveFinished(item);
            mPendingChanges.removals.remove(i);
        }
        count = mPendingChanges.additions.size();
        for (int i = count - 1; i >= 0; i--) {
            ViewHolder item = mPendingChanges.additions.get(i);
            item.itemView.setAlpha(1);
            dispatchAddFinished(item);
            mPendingChanges.additions.remove(i);
        }
        count = mPendingChanges.changes.size();
        for (int i = count - 1; i >= 0; i--) {
            endChangeAnimationIfNecessary(mPendingChanges.changes.get(i));
        }
        mPendingChanges.clear();
        if (!isRunning()) {
            return;
        }
        int listCount = mMovesList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<AnimationHelper.MoveInfo> moves = mMovesList.get(i);
            count = moves.size();
            for (int j = count - 1; j >= 0; j--) {
                AnimationHelper.MoveInfo moveInfo = moves.get(j);
                ViewHolder item = moveInfo.holder;
                View view = item.itemView;
                view.setTranslationY(0);
                view.setTranslationX(0);
                dispatchMoveFinished(moveInfo.holder);
                moves.remove(j);
                if (moves.isEmpty()) {
                    mMovesList.remove(moves);
                }
            }
        }
        listCount = mAdditionsList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<ViewHolder> additions = mAdditionsList.get(i);
            count = additions.size();
            for (int j = count - 1; j >= 0; j--) {
                ViewHolder item = additions.get(j);
                item.itemView.setAlpha(1);
                dispatchAddFinished(item);
                additions.remove(j);
                if (additions.isEmpty()) {
                    mAdditionsList.remove(additions);
                }
            }
        }
        listCount = mChangesList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<AnimationHelper.ChangeInfo> changes = mChangesList.get(i);
            count = changes.size();
            for (int j = count - 1; j >= 0; j--) {
                endChangeAnimationIfNecessary(changes.get(j));
                if (changes.isEmpty()) {
                    mChangesList.remove(changes);
                }
            }
        }
        cancelAll(mRemoveAnimations);
        cancelAll(mMoveAnimations);
        cancelAll(mAddAnimations);
        cancelAll(mChangeAnimations);
        dispatchAnimationsFinished();
    }

    private void cancelAll(List<ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            ViewCompat.animate(viewHolders.get(i).itemView).cancel();
        }
    }

    public void subscribeOnRemoveAnimationEnd(@NonNull OnAnimationEndListener onRemoveAnimationEndListener) {
        mOnRemoveAnimationEndListeners.add(onRemoveAnimationEndListener);
    }

    public void subscribeOnAddAnimationEnd(@NonNull OnAnimationEndListener onAddAnimationEndListener) {
        mOnAddAnimationEndListeners.add(onAddAnimationEndListener);
    }

    private interface UnVoidFunction<PAR> {
        void apply(PAR param);
    }
}
