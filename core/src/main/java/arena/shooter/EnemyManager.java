package arena.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class EnemyManager {
    public Array<Enemy> enemies;
    private float spawnTimer;
    private float baseSpawnInterval;

    public EnemyManager() {
        enemies = new Array<>();
        spawnTimer = 0f;
        baseSpawnInterval = 2f;
    }

    public void update(float delta, float survivalTime, float playerCenterX, float playerCenterY) {
        spawnTimer += delta;
        float currentSpawnInterval = Math.max(0.3f, baseSpawnInterval - survivalTime * 0.02f);
        if (spawnTimer >= currentSpawnInterval) {
            spawnTimer = 0f;
            spawnEnemy(survivalTime);
        }

        for (Enemy enemy : enemies) {
            enemy.update(delta, playerCenterX, playerCenterY);
            enemy.applySeparation(enemies, delta);

            if (enemy instanceof ShooterEnemy) {
                ((ShooterEnemy) enemy).updateBullets(delta);
            }
        }
    }

    private void spawnEnemy(float survivalTime) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        int edge = (int) (Math.random() * 4);
        float spawnX = 0, spawnY = 0;

        switch (edge) {
            case 0:
                spawnX = (float) (Math.random() * screenWidth);
                spawnY = screenHeight;
                break;
            case 1:
                spawnX = (float) (Math.random() * screenWidth);
                spawnY = 0;
                break;
            case 2:
                spawnX = 0;
                spawnY = (float) (Math.random() * screenHeight);
                break;
            case 3:
                spawnX = screenWidth;
                spawnY = (float) (Math.random() * screenHeight);
                break;
        }

        float scaledSpeed = 80f + survivalTime * 0.5f;
        float roll = (float) Math.random();

        if (roll < 0.5f) {
            enemies.add(new BasicEnemy(spawnX, spawnY, scaledSpeed));
        } else if (roll < 0.7f) {
            enemies.add(new FastEnemy(spawnX, spawnY, scaledSpeed * 1.8f));
        } else if (roll < 0.9f) {
            enemies.add(new TankEnemy(spawnX, spawnY, scaledSpeed * 0.5f));
        } else {
            enemies.add(new ShooterEnemy(spawnX, spawnY, scaledSpeed));
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
        }
    }

    public void clear() {
        enemies.clear();
        spawnTimer = 0f;
    }

}
