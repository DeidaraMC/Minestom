package net.minestom.server.entity;

import net.minestom.server.tag.Tag;

final class EntityTags {
    static final Tag<Boolean> REFRESHING_ACTIVE_HAND = Tag.Boolean("RefreshingActiveHand").defaultValue(false);
    static final Tag<Boolean> SETTING_SNEAKING = Tag.Boolean("SettingSneaking").defaultValue(false);
}
