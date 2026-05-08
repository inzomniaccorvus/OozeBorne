package arena.shooter.systems;

import arena.shooter.core.Constants;
import arena.shooter.entities.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class CollisionSystem {

    private float playerCX(Player p) { return p.x + p.size * 1.5f; }
    private float playerCY(Player p) { return p.y + p.size * 2.0f; }
    private static final float P_RX = 25f;
    private static final float P_RY = 55f;

    private boolean playerHit(Player p, float tx, float ty) {
        float dx = tx - playerCX(p);
        float dy = ty - playerCY(p);
        return (dx * dx) / (P_RX * P_RX) + (dy * dy) / (P_RY * P_RY) < 1f;
    }

    public int checkBulletEnemyCollisions(BulletManager bulletManager, EnemyManager enemyManager,
                                          ParticleSystem particleSystem, Sound hitSound,
                                          Sound explosionSound, Player player) {
        Array<Bullet> bullets = bulletManager.bullets;
        Array<Enemy> enemies = enemyManager.enemies;
        int score = 0;

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            for (int j = enemies.size - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);
                float dist = dist(bullet.x, bullet.y, enemy.x, enemy.y);
                if (dist < enemy.collisionRadius) {
                    if (enemy instanceof AmalgamEnemy && ((AmalgamEnemy) enemy).reflecting) {
                        float toPlayerX = enemy.x - player.x;
                        float toPlayerY = enemy.y - player.y;
                        float toPlayerDist = dist(0, 0, toPlayerX, toPlayerY);
                        float leadFactor = 0.6f;
                        float predictTime = Math.min(toPlayerDist / bullet.speed, 0.5f);
                        float futurePlayerX = player.x + player.velX * predictTime * leadFactor;
                        float futurePlayerY = player.y + player.velY * predictTime * leadFactor;
                        float dx = futurePlayerX - enemy.x;
                        float dy = futurePlayerY - enemy.y;
                        float len = dist(0, 0, dx, dy);
                        bullet.dirX = dx / len;
                        bullet.dirY = dy / len;
                        bullet.speed *= 1.25f;
                        bullet.size *= 1.5f;
                        ((AmalgamEnemy) enemy).bossBullets.add(bullet);
                        bullets.removeIndex(i);
                        break;
                    }
                    enemy.takeDamage(bullet.damage);
                    hitSound.play(0.6f);
                    particleSystem.spawnDamageNumber(enemy.x, enemy.y, bullet.damage);
                    bulletManager.bulletsHit++;
                    bullets.removeIndex(i);
                    if (enemy.isDead()) {
                        explosionSound.play(0.8f);
                        particleSystem.spawnDeathParticles(enemy.x, enemy.y, enemy.color);
                        if (enemy instanceof SplitterEnemy) {
                            ((SplitterEnemy) enemy).split(enemies);
                        }
                        enemies.removeIndex(j);
                        score += enemy.scoreValue;
                    }
                    break;
                }
            }
        }
        return score;
    }

    public void checkPlayerEnemyCollisions(Player player, EnemyManager enemyManager) {
        for (int i = enemyManager.enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemyManager.enemies.get(i);
            float dx = enemy.x - playerCX(player);
            float dy = enemy.y - playerCY(player);
            float dist = dist(0, 0, dx, dy);
            if (dist < enemy.bodyRadius + P_RX) {
                player.takeDamage(Constants.DAMAGE_VALUE);
                enemy.applyKnockback(dx / dist, dy / dist);
            }
        }
    }

    public void checkEnemyBulletPlayerCollisions(Player player, EnemyManager enemyManager) {
        for (Enemy enemy : enemyManager.enemies) {
            if (!(enemy instanceof ShooterEnemy || enemy instanceof AmalgamEnemy)) continue;
            Array<Bullet> bullets = (enemy instanceof ShooterEnemy)
                ? ((ShooterEnemy) enemy).enemyBullets
                : ((AmalgamEnemy) enemy).bossBullets;
            for (int i = bullets.size - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                if (playerHit(player, bullet.x, bullet.y)) {
                    player.takeDamage(Constants.DAMAGE_VALUE);
                    bullets.removeIndex(i);
                }
            }
        }
    }

    private float dist(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
