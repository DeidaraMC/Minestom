package net.minestom.server.entity;

import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.collision.PhysicsResult;
import net.minestom.server.collision.ShapeImpl;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.ProjectileMeta;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.entity.EntityShootEvent;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithEntityEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.*;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerProjectile extends LivingEntity {
    private final Entity shooter;
    private long cooldown = 0;

    public PlayerProjectile(Entity shooter, EntityType type) {
        super(type);
        this.shooter = shooter;
        this.hasCollision = false;
        setup();
    }

    private void setup() {
        super.hasPhysics = false;
        if (getEntityMeta() instanceof ProjectileMeta) {
            ((ProjectileMeta) getEntityMeta()).setShooter(this.shooter);
        }
    }

    public void shoot(Point start, double power, double spread) {
        this.shoot(start, shooter.getPosition().direction(), power, spread);
    }

    @Override
    public CompletableFuture<Void> setInstance(@NotNull Instance instance, @NotNull Pos spawnPosition) {
        var res = super.setInstance(instance, spawnPosition);

        Pos insideBlock = checkInsideBlock(instance);
        // Check if we're inside of a block
        if (insideBlock != null) {
            var e = new ProjectileCollideWithBlockEvent(this, Pos.fromPoint(spawnPosition), instance.getBlock(spawnPosition));
            MinecraftServer.getGlobalEventHandler().call(e);
        }

        return res;
    }

    public void shoot(@NotNull Point start, @NotNull Vec direction, double power, double spread) {
        var instance = shooter.getInstance();
        if (instance == null) return;

        Random random = ThreadLocalRandom.current();
        spread *= 0.007499999832361937D;
        Vec velocity = direction.add(random.nextGaussian() * spread, random.nextGaussian() * spread, random.nextGaussian() * spread)
                .mul(MinecraftServer.TICK_PER_SECOND * power);

        final EntityShootEvent shootEvent = new EntityShootEvent(this.shooter, this, start, power, spread);
        EventDispatcher.call(shootEvent);
        if (shootEvent.isCancelled()) {
            remove();
            return;
        }

        //set velocity immediately before setting instance
        setVelocity(velocity);
        this.setInstance(instance, Pos.fromPoint(start).withDirection(direction)).whenComplete((result, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
            }
        });

        cooldown = System.currentTimeMillis();
    }

    private Pos checkInsideBlock(@NotNull Instance instance) {
        var iterator = this.getBoundingBox().getBlocks(this.getPosition());

        while (iterator.hasNext()) {
            var block = iterator.next();
            Block b = instance.getBlock(block);
            var hit = b.registry().collisionShape().intersectBox(this.getPosition().sub(block), this.getBoundingBox());
            if (hit) return Pos.fromPoint(block);
        }

        return null;
    }


    /**
     * Don't want this to do anything when on projectile entities, handled differently in tick method
     */
    @Override
    protected void synchronizePosition(boolean includeSelf) {
    }

    @Override
    public void tick(long time) {
        final Pos posBefore = getPosition();
        super.tick(time);
        final Pos posNow = getPosition();

        Vec diff = Vec.fromPoint(posNow.sub(posBefore));
        PhysicsResult result = CollisionUtils.handlePhysics(
                instance, this.getChunk(),
                this.getBoundingBox(),
                posBefore, diff,
                null, true
        );

        if (cooldown + 1000 < System.currentTimeMillis()) {
            float yaw = (float) Math.toDegrees(Math.atan2(diff.x(), diff.z()));
            float pitch = (float) Math.toDegrees(Math.atan2(diff.y(), Math.sqrt(diff.x() * diff.x() + diff.z() * diff.z())));
            sendPacketsToViewers(new EntityTeleportPacket(getEntityId(), position.withYaw(yaw).withPitch(pitch), onGround));
            sendPacketsToViewers(getVelocityPacket());
            cooldown = System.currentTimeMillis();
        }

        PhysicsResult collided = CollisionUtils.checkEntityCollisions(instance, this.getBoundingBox(), posBefore, diff, 3, (e) -> e != this, result);
        if (collided != null && collided.collisionShapes()[0] != shooter) {
            if (collided.collisionShapes()[0] instanceof Entity entity) {
                var e = new ProjectileCollideWithEntityEvent(this, collided.newPosition(), entity);
                MinecraftServer.getGlobalEventHandler().call(e);
                return;
            }
        }

        if (result.hasCollision()) {
            Block hitBlock = null;
            Point hitPoint = null;
            if (result.collisionShapes()[0] instanceof ShapeImpl block) {
                hitBlock = block.block();
                hitPoint = result.collisionPoints()[0];
            }
            if (result.collisionShapes()[1] instanceof ShapeImpl block) {
                hitBlock = block.block();
                hitPoint = result.collisionPoints()[1];
            }
            if (result.collisionShapes()[2] instanceof ShapeImpl block) {
                hitBlock = block.block();
                hitPoint = result.collisionPoints()[2];
            }

            if (hitBlock == null) return;

            var e = new ProjectileCollideWithBlockEvent(this, Pos.fromPoint(hitPoint), hitBlock);
            MinecraftServer.getGlobalEventHandler().call(e);
        }
    }

    /**
     * Update the position without sending teleport packets
     */
    @Override
    public void refreshPosition(@NotNull final Pos newPosition, boolean ignoreView) {
        final var previousPosition = this.position;
        final Pos position = ignoreView ? previousPosition.withCoord(newPosition) : newPosition;
        if (position.equals(lastSyncedPosition)) return;
        this.position = position;
        this.previousPosition = previousPosition;
        if (!position.samePoint(previousPosition)) refreshCoordinate(position);
    }
}
