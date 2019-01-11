package com.omega_r.libs.omegarecyclerview.expandable_recycler_view.animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.omega_r.libs.omegarecyclerview.expandable_recycler_view.OmegaExpandableRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AnimationHelper {
    public int totalChanges;
    public int positionInChanges;
    public int height;
    public int width;
    public int visibleAdapterPosition;

    @NonNull
    private PendingChanges pendingChanges = new PendingChanges();

    @Nullable
    public OmegaExpandableRecyclerView.Adapter.ChildViewHolder upperViewHolder;

    @Nullable
    public OmegaExpandableRecyclerView.Adapter.ChildViewHolder lowerViewHolder;

    public void clear() {
        totalChanges = 0;
        positionInChanges = 0;
        upperViewHolder = null;
        lowerViewHolder = null;
        height = 0;
        width = 0;
        visibleAdapterPosition = -1;
        pendingChanges.clear();
    }

    @NonNull
    public PendingChanges getPendingChanges() {
        return pendingChanges;
    }

    public void setPendingChanges(@NonNull final PendingChanges pendingChanges) {
        this.pendingChanges.copy(pendingChanges);
    }

    public boolean havePendingChanges() {
        return !pendingChanges.changes.isEmpty();
    }

    public boolean havePendingMoves() {
        return !pendingChanges.moves.isEmpty();
    }

    public boolean havePendingRemovals() {
        return !pendingChanges.removals.isEmpty();
    }

    public boolean havePendingAdditions() {
        return !pendingChanges.additions.isEmpty();
    }

    public static class PendingChanges {
        @NonNull
        public List<RecyclerView.ViewHolder> removals = new ArrayList<>();

        @NonNull
        public List<RecyclerView.ViewHolder> additions = new ArrayList<>();

        @NonNull
        public List<MoveInfo> moves = new ArrayList<>();

        @NonNull
        public List<ChangeInfo> changes = new ArrayList<>();

        public PendingChanges() {
            // nothing
        }

        private void copy(@NonNull final PendingChanges other) {
            removals = new ArrayList<>(other.removals);
            additions = new ArrayList<>(other.additions);
            moves = new ArrayList<>(other.moves);
            changes = new ArrayList<>(other.changes);
        }

        public void clear() {
            removals.clear();
            additions.clear();
            moves.clear();
            changes.clear();
        }
    }

    public static class MoveInfo {
        public RecyclerView.ViewHolder holder;
        public int fromX;
        public int fromY;
        public int toX;
        public int toY;

        public MoveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    public static class ChangeInfo {
        RecyclerView.ViewHolder oldHolder, newHolder;
        public int fromX;
        public int fromY;
        public int toX;
        public int toY;

        public ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        public ChangeInfo(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
            this(oldHolder, newHolder);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }
}
