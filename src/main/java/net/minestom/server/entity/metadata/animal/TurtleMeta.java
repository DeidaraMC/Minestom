package net.minestom.server.entity.metadata.animal;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.metadata.AbstractAgeableMeta;
import net.minestom.server.entity.metadata.PathfinderMobMeta;
import org.jetbrains.annotations.NotNull;

public class TurtleMeta extends AnimalMeta {
    public static final byte OFFSET = AnimalMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 6;

    public TurtleMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

    public @NotNull Point getHomePosition() {
        return super.metadata.getIndex(OFFSET, Vec.ZERO);
    }

    public void setBlockPosition(@NotNull Point value) {
        super.metadata.setIndex(OFFSET, Metadata.BlockPosition(value));
    }

    @Override
    public void setBaby(boolean value) {
        if (isBaby() == value) return;
        this.consumeEntity((entity) -> {
            BoundingBox bb = entity.getEntityType().registry().boundingBox();
            if (value) entity.setBoundingBox(bb.width() * 0.3, bb.height() * 0.3, bb.depth() * 0.3);
            else entity.setBoundingBox(bb);
        });
        super.metadata.setIndex(AbstractAgeableMeta.OFFSET, Metadata.Boolean(value));
    }

    public boolean isHasEgg() {
        return super.metadata.getIndex(OFFSET + 1, false);
    }

    public void setHasEgg(boolean value) {
        super.metadata.setIndex(OFFSET + 1, Metadata.Boolean(value));
    }

    public boolean isLayingEgg() {
        return super.metadata.getIndex(OFFSET + 2, false);
    }

    public void setLayingEgg(boolean value) {
        super.metadata.setIndex(OFFSET + 2, Metadata.Boolean(value));
    }

    public @NotNull Point getTravelPosition() {
        return super.metadata.getIndex(OFFSET + 3, Vec.ZERO);
    }

    public void setTravelPosition(@NotNull Point value) {
        super.metadata.setIndex(OFFSET + 3, Metadata.BlockPosition(value));
    }

    public boolean isGoingHome() {
        return super.metadata.getIndex(OFFSET + 4, false);
    }

    public void setGoingHome(boolean value) {
        super.metadata.setIndex(OFFSET + 4, Metadata.Boolean(value));
    }

    public boolean isTravelling() {
        return super.metadata.getIndex(OFFSET + 5, false);
    }

    public void setTravelling(boolean value) {
        super.metadata.setIndex(OFFSET + 5, Metadata.Boolean(value));
    }

}
