package net.minestom.server.event.entity;

import net.minestom.server.entity.Entity;
import net.minestom.server.event.Event;
import org.jetbrains.annotations.NotNull;

public class EntityPassengerEvent implements Event {
    private final Entity passenger;
    private final Entity vehicle;
    private final boolean dismounting;

    public EntityPassengerEvent(@NotNull Entity passenger, @NotNull Entity vehicle, boolean dismounted) {
        this.passenger = passenger;
        this.vehicle = vehicle;
        this.dismounting = dismounted;
    }

    public @NotNull Entity getPassenger() {
        return passenger;
    }

    public @NotNull Entity getVehicle() {
        return vehicle;
    }

    public boolean dismounted() {
        return dismounting;
    }
}