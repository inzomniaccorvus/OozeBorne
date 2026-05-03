package arena.shooter.util;

public interface Damageable {
    void takeDamage(int amount);
    boolean isDead();
}
