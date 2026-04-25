package arena.shooter;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
            enemy.applySeparation(enemies, delta);

            if (enemy instanceof ShooterEnemy) {
                ((ShooterEnemy) enemy).updateBullets(delta);
            }

            if (enemy instanceof AmalgamEnemy) {
                ((AmalgamEnemy) enemy).updateBullets(delta);
            }

        }
    }

    public void draw(ShapeRenderer shape) {
        for (Enemy enemy : enemies) {
            enemy.draw(shape);
        }

        for (Enemy enemy : enemies) {
            if (enemy instanceof ShooterEnemy) {
                ((ShooterEnemy) enemy).drawBullets(shape);
            }

            if (enemy instanceof AmalgamEnemy) {
                ((AmalgamEnemy) enemy).drawBullets(shape);
            }
        }
    }

    public void clear() {
        enemies.clear();
    }

}
