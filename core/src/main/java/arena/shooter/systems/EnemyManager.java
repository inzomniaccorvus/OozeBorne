package arena.shooter.systems;

import arena.shooter.core.GameAssets;
import arena.shooter.entities.AmalgamEnemy;
import arena.shooter.entities.Bullet;
import arena.shooter.entities.Enemy;
import arena.shooter.entities.ShooterEnemy;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {
    public Array<Enemy> enemies;

    public EnemyManager() {
        enemies = new Array<>();
    }

    public void update(WaveManager waveManager, float delta, float playerCenterX, float playerCenterY) {

        waveManager.update(enemies, delta);

        for (Enemy enemy : enemies) {
            enemy.update(delta, playerCenterX, playerCenterY);
            if (!enemy.knockBacked) {
                enemy.applySeparation(enemies, delta);
            }

            if (enemy instanceof ShooterEnemy && !enemy.knockBacked) {
                ((ShooterEnemy) enemy).updateBullets(delta);
            }

            if (enemy instanceof AmalgamEnemy) {
                ((AmalgamEnemy) enemy).updateBullets(delta);
            }

        }
    }


    public void draw(SpriteBatch batch, GameAssets assets) {
        for (Enemy enemy : enemies) {
            enemy.draw(batch, assets);
        }

        for (Enemy enemy : enemies) {
            Array<Bullet> bullets = null;
            if (enemy instanceof ShooterEnemy) bullets = ((ShooterEnemy) enemy).enemyBullets;
            if (enemy instanceof AmalgamEnemy) bullets = ((AmalgamEnemy) enemy).bossBullets;
            if (bullets != null) {
                for (Bullet bullet : bullets) {
                    float wobble = 1f + 0.15f * (float) Math.sin(bullet.rotation * 0.05f);
                    batch.draw(assets.goopTexture, bullet.x - bullet.size, bullet.y - bullet.size, bullet.size * 6 * wobble, bullet.size * 6 * wobble);
                }
            }
        }
    }

    public void clear() {
        enemies.clear();
    }

}
