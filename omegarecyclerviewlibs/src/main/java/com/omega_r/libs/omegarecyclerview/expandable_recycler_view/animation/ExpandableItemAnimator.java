package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class ExpandableItemAnimator extends SimpleItemAnimator {
    private static final String TAG = ExpandableItemAnimator.class.getName();

    private ArrayList<ViewHolder> mPendingRemovals = new ArrayList<>();
    private ArrayList<ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();


    private ArrayList<ArrayList<ViewHolder>> mAdditionsList = new ArrayList<>();
    private ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();


    private ArrayList<ViewHolder> mAddAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mMoveAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mRemoveAnimations = new ArrayList<>();
    private ArrayList<ViewHolder> mChangeAnimations = new ArrayList<>();

    protected abstract void onRemoveStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void setupRemoveAnimation(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onRemoveCancel(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onRemoveEnd(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onAddStart(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void setupAddAnimation(ViewPropertyAnimator animation, OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onAddCancel(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    protected abstract void onAddEnd(OmegaExpandableRecyclerView.Adapter.ChildViewHolder holder);

    @Override
    public void runPendingAnimations() {
        boolean removalsPending = !mPendingRemovals.isEmpty();
        boolean movesPending = !mPendingMoves.isEmpty();
        boolean changesPending = !mPendingChanges.isEmpty();
        boolean additionsPending = !mPendingAdditions.isEmpty();

        if (removalsPending) runRemoveActions();
        if (movesPending) runRemoveActions(removalsPending);
        if (changesPending) runChangesActions(removalsPending);
        if (additionsPending) runAddActions(removalsPending, movesPending, changesPending);
    }

    private void runRemoveActions() {
        proceedWithAnimationHelper(mPendingRemovals, new UnVoidFunction<ViewHolder>() {
            @Override
            public void apply(ViewHolder param) {
                animateRemoveImpl(param);
            }
        });
    }

    private void runRemoveActions(boolean hasRemovals) {
        final ArrayList<MoveInfo> moves = new ArrayList<>(mPendingMoves);
        mMovesList.add(moves);
        mPendingMoves.clear();
        Runnable mover = new Runnable() {
            @Override
            public void run() {
                for (MoveInfo moveInfo : moves) {
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
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
        final ArrayList<ChangeInfo> changes = new ArrayList<>(mPendingChanges);
        mChangesList.add(changes);
        mPendingChanges.clear();
        Runnable changer = new Runnable() {
            @Override
            public void run() {
                for (ChangeInfo change : changes) {
                    animateChangeImpl(change);
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
        final ArrayList<ViewHolder> additions = new ArrayList<>(mPendingAdditions);
        mAdditionsList.add(additions);
        mPendingAdditions.clear();
        Runnable adder = new Runnable() {
            public void run() {
                proceedWithAnimationHelper(additions, new UnVoidFunction<ViewHolder>() {
                    @Override
                    public void apply(ViewHolder param) {
                        animateAddImpl(param);
                    }
                });
                mAdditionsList.remove(additions);
            }
        };
        if (hasRemovals || hasMoves || hasChanges) {
            long removeDuration = hasRemovals ? getRemoveDuration() : 0;
            long moveDuration = hasMoves ? getMoveDuration() : 0;
            long changeDuration = hasChanges ? getChangeDuration() : 0;
            long totalDelay = removeDuration + Math.max(moveDuration, changeDuration);
            View view = additions.get(0).itemView;
            ViewCompat.postOnAnimationDelayed(view, adder, totalDelay);
        } else {
            adder.run();
        }
    }

    private void proceedWithAnimationHelper(List<ViewHolder> holders, UnVoidFunction<ViewHolder> func) {
        float heightSum = 0f;
        int childChangesCount = 0;
        for (int i = holders.size() - 1; i >= 0; i--) {
            ViewHolder holder = holders.get(i);
            if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder childViewHolder =
                        (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder;
                AnimationHelper helper = new AnimationHelper();
                helper.previousChangedHoldersHeight = heightSum;
                childViewHolder.animationHelper = helper;

                childChangesCount++;
                heightSum += holder.itemView.getHeight();
            }
        }

        int positionInChanges = 0;
        for (ViewHolder holder : holders) {
            if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
                OmegaExpandableRecyclerView.Adapter.ChildViewHolder childHolder =
                        (OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder;
                childHolder.animationHelper.totalChanges = childChangesCount;
                childHolder.animationHelper.positionInChanges = positionInChanges++;
            }

            func.apply(holder);
        }
        holders.clear();
    }

    @Override
    public boolean animateRemove(final ViewHolder holder) {
        endAnimation(holder);
        mPendingRemovals.add(holder);
        return true;
    }

    private void animateRemoveImpl(ViewHolder holder) {
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
        final ViewPropertyAnimator animation = holder.itemView.animate();
        setupRemoveAnimation(animation, holder);
        animation
                .setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        dispatchRemoveStarting(holder);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        onRemoveCancel(holder);
                    }

                    public void onAnimationEnd(Animator animator) {
                        animation.setListener(null);
                        onRemoveEnd(holder);
                        dispatchRemoveFinished(holder);
                        mRemoveAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                })
                .start();
    }

    @Override
    public boolean animateAdd(final ViewHolder holder) {
        endAnimation(holder);
        mPendingAdditions.add(holder);
        return true;
    }

    private void animateAddImpl(final ViewHolder holder) {
        if (holder instanceof OmegaExpandableRecyclerView.Adapter.ChildViewHolder) {
            animateAddChild((OmegaExpandableRecyclerView.Adapter.ChildViewHolder) holder);
        } else {
            animateAddGroup(holder);
        }
        mAddAnimations.add(holder);
    }

    private void animateAddGroup(final ViewHolder holder) {
        holder.itemView.setAlpha(0f);
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
        holder.itemView.setAlpha(0f);

        final ViewPropertyAnimator animation = holder.itemView.animate();
        setupAddAnimation(animation, holder);
        animation
                .setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        dispatchAddStarting(holder);
                        holder.itemView.setAlpha(1f);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        onAddCancel(holder);
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

            this.mPendingMoves.add(new MoveInfo(holder, fromX, fromY, toX, toY));
            return true;
        }
    }

    private void animateMoveImpl(final ViewHolder holder, int fromX, int fromY, int toX, int toY) {
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
                newHolder.itemView.setAlpha(0.0F);
            }

            this.mPendingChanges.add(new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY));
            return true;
        }
    }

    private void animateChangeImpl(final ChangeInfo changeInfo) {
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

    private void endChangeAnimation(List<ChangeInfo> infoList, ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = infoList.get(i);
            if (endChangeAnimationIfNecessary(changeInfo, item)) {
                if (changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                    infoList.remove(changeInfo);
                }
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, ViewHolder item) {
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
        for (int i = mPendingMoves.size() - 1; i >= 0; i--) {
            MoveInfo moveInfo = mPendingMoves.get(i);
            if (moveInfo.holder == item) {
                view.setTranslationX(0);
                view.setTranslationY(0);
                dispatchMoveFinished(item);
                mPendingMoves.remove(item);
            }
        }
        endChangeAnimation(mPendingChanges, item);
        if (mPendingRemovals.remove(item)) {
            view.setAlpha(1);
            dispatchRemoveFinished(item);
        }
        if (mPendingAdditions.remove(item)) {
            view.setAlpha(1);
            dispatchAddFinished(item);
        }
        for (int i = mChangesList.size() - 1; i >= 0; i--) {
            ArrayList<ChangeInfo> changes = mChangesList.get(i);
            endChangeAnimation(changes, item);
            if (changes.isEmpty()) {
                mChangesList.remove(changes);
            }
        }
        for (int i = mMovesList.size() - 1; i >= 0; i--) {
            ArrayList<MoveInfo> moves = mMovesList.get(i);
            for (int j = moves.size() - 1; j >= 0; j--) {
                MoveInfo moveInfo = moves.get(j);
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
        return (!mPendingAdditions.isEmpty() ||
                !mPendingChanges.isEmpty() ||
                !mPendingMoves.isEmpty() ||
                !mPendingRemovals.isEmpty() ||
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
        int count = mPendingMoves.size();
        for (int i = count - 1; i >= 0; i--) {
            MoveInfo item = mPendingMoves.get(i);
            View view = item.holder.itemView;
            view.setTranslationY(0);
            view.setTranslationX(0);
            dispatchMoveFinished(item.holder);
            mPendingMoves.remove(i);
        }
        count = mPendingRemovals.size();
        for (int i = count - 1; i >= 0; i--) {
            ViewHolder item = mPendingRemovals.get(i);
            dispatchRemoveFinished(item);
            mPendingRemovals.remove(i);
        }
        count = mPendingAdditions.size();
        for (int i = count - 1; i >= 0; i--) {
            ViewHolder item = mPendingAdditions.get(i);
            item.itemView.setAlpha(1);
            dispatchAddFinished(item);
            mPendingAdditions.remove(i);
        }
        count = mPendingChanges.size();
        for (int i = count - 1; i >= 0; i--) {
            endChangeAnimationIfNecessary(mPendingChanges.get(i));
        }
        mPendingChanges.clear();
        if (!isRunning()) {
            return;
        }
        int listCount = mMovesList.size();
        for (int i = listCount - 1; i >= 0; i--) {
            ArrayList<MoveInfo> moves = mMovesList.get(i);
            count = moves.size();
            for (int j = count - 1; j >= 0; j--) {
                MoveInfo moveInfo = moves.get(j);
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
            ArrayList<ChangeInfo> changes = mChangesList.get(i);
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

    private static class MoveInfo {
        public ViewHolder holder;
        public int fromX;
        public int fromY;
        public int toX;
        public int toY;

        private MoveInfo(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    private static class ChangeInfo {
        ViewHolder oldHolder, newHolder;
        public int fromX;
        public int fromY;
        public int toX;
        public int toY;

        private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
            this(oldHolder, newHolder);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    private interface UnVoidFunction<PAR> {
        void apply(PAR param);
    }
}
