package arena.shooter.systems;

import arena.shooter.core.Constants;
import arena.shooter.entities.Bullet;
import arena.shooter.entities.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class CollisionSystem {

    public int checkBulletEnemyCollisions(BulletManager bulletManager, EnemyManager enemyManager, ParticleSystem particleSystem, Sound hitSound, Sound explosionSound, Player player) {
        Array<Bullet> bullets = bulletManager.bullets;
        Array<Enemy> enemies = enemyManager.enemies;

        int score = 0;

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);

            for (int j = enemies.size - 1; j >= 0; j--) {
                Enemy enemy = enemies.get(j);

                float distanceX = enemy.x - bullet.x;
                float distanceY = enemy.y - bullet.y;
                float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

                if (distance < bullet.size + enemy.collisionRadius) {
                    if (enemy instanceof AmalgamEnemy && ((AmalgamEnemy) enemy).reflecting) {
                        float toPlayerX = enemy.x - player.x;
                        float toPlayerY = enemy.y - player.y;
                        float toPlayerDist = (float) Math.sqrt(toPlayerX * toPlayerX + toPlayerY * toPlayerY);
                        float leadFactor = 0.6f;
                        float predictTime = Math.min(toPlayerDist / bullet.speed, 0.5f);
                        float futurePlayerX = player.x + player.velX * predictTime * leadFactor;
                        float futurePlayerY = player.y + player.velY * predictTime * leadFactor;
                        float dx = futurePlayerX - enemy.x;
                        float dy = futurePlayerY - enemy.y;
                        float len = (float) Math.sqrt(dx * dx + dy * dy);
                        dx /= len;
                        dy /= len;
                        bullet.dirX = dx;
                        bullet.dirY = dy;
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

            float dx = enemy.x - (player.x + player.size * Constants.PLAYER_COLLISION_X);
            float dy = enemy.y - (player.y + player.size * Constants.PLAYER_COLLISION_Y);
            float rx = Constants.PLAYER_HIT_RX;
            float ry = Constants.PLAYER_HIT_RY;
            if ((dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) < 1.0f) {
                player.takeDamage(Constants.DAMAGE_VALUE);
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                enemy.applyKnockback(dx / distance, dy / distance);
            }
        }
    }

    public void checkEnemyBulletPlayerCollisions(Player player, EnemyManager enemyManager) {
        for (Enemy enemy : enemyManager.enemies) {
            if (!(enemy instanceof ShooterEnemy || enemy instanceof AmalgamEnemy)) continue;
            Array<Bullet> bullets;
            if (enemy instanceof ShooterEnemy) {
                bullets = ((ShooterEnemy) enemy).enemyBullets;
            } else {
                bullets = ((AmalgamEnemy) enemy).bossBullets;
            }
            for (int i = bullets.size - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                float dx = bullet.x - (player.x + player.size * Constants.PLAYER_COLLISION_X);
                float dy = bullet.y - (player.y + player.size * Constants.PLAYER_COLLISION_Y);
                float rx = Constants.PLAYER_HIT_RX;
                float ry = Constants.PLAYER_HIT_RY;
                if ((dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) < 1.0f) {
                    player.takeDamage(Constants.DAMAGE_VALUE);
                    bullets.removeIndex(i);
                }
            }
        }
    }

}
