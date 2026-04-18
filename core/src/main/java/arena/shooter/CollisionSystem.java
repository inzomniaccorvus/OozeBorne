package arena.shooter;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;

public class CollisionSystem {

    public int checkBulletEnemyCollisions(BulletManager bulletManager, EnemyManager enemyManager,
                                            ParticleSystem particleSystem, Sound hitSound,
                                            Sound explosionSound ) {
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

                if (distance < bullet.size + enemy.size) {
                    enemy.takeDamage(1);
                    hitSound.play(0.6f);
                    particleSystem.spawnDamageNumber(enemy.x, enemy.y, 1);
                    bullets.removeIndex(i);

                    if (enemy.isDead()) {
                        explosionSound.play(0.8f);
                        particleSystem.spawnDeathParticles(enemy.x, enemy.y, enemy.color);
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

            float distanceX = enemy.x - player.centerX();
            float distanceY = enemy.y - player.centerY();
            float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            if (distance < enemy.size + player.size / 2) {
                player.takeDamage(10);
            }
        }
    }
}
