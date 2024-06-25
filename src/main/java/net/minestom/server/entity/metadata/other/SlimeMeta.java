package net.minestom.server.entity.metadata.other;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.metadata.MobMeta;
import org.jetbrains.annotations.NotNull;

public class SlimeMeta extends MobMeta {
    public static final byte OFFSET = MobMeta.MAX_OFFSET;
    public static final byte MAX_OFFSET = OFFSET + 1;

    public SlimeMeta(@NotNull Entity entity, @NotNull Metadata metadata) {
        super(entity, metadata);
    }

    public int getSize() {
        return super.metadata.getIndex(OFFSET, 1);
    }

    public void setSize(int value) {
        this.consumeEntity((entity) -> {
            double boxSize = entity.getEntityType().registry().height() * value;
            entity.setBoundingBox(boxSize, boxSize, boxSize);
        });
        super.metadata.setIndex(OFFSET, Metadata.VarInt(value));
    }

}
