package arena.shooter.systems;

import arena.shooter.entities.*;
import com.badlogic.gdx.utils.Array;

public class WaveManager {
    public Array<Wave> waves;
    public int currentWave;
    private int numberOfWaves = 10;
    public boolean betweenWaves;
    public boolean gameWon;
    public float introTimer;
    public int spawnIndex;
    public float spawnTimer;
    public int currentEdge;
    public float edgeChangeTimer;
    private float edgeChangeInterval = 8f;

    public boolean bossSpawned;
    public int currentMap;

    public WaveManager() {
        waves = new Array<Wave>();
        betweenWaves = true;
        gameWon = false;
        introTimer = 3f;
        currentWave = 0;
        currentEdge = 0;
        spawnIndex = 0;
        edgeChangeTimer = 0;
        buildWaves();
    }

    public void spawnWave(Wave wave, Array<Enemy> enemies) {

        int screenWidth = 1366;
        int screenHeight = 768;

        if (wave.spawnEdge == -1) {
            if (edgeChangeTimer >= edgeChangeInterval) {
                edgeChangeTimer = 0;
                currentEdge = (int) (Math.random() * 4);
            }
        } else {
            currentEdge = wave.spawnEdge;
        }

        float spawnX = 0, spawnY = 0;

        switch (currentEdge) {
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

        if (spawnTimer >= wave.spawnInterval) {
            spawnTimer = 0f;
            if (spawnIndex < wave.spawnQueue.size) {
                String type = wave.spawnQueue.get(spawnIndex);

                switch (type) {
                    case "BASIC":
                        enemies.add(new BasicEnemy(spawnX, spawnY, wave.speed));
                        break;
                    case "FAST":
                        enemies.add(new FastEnemy(spawnX, spawnY, wave.speed * 1.8f));
                        break;
                    case "TANK":
                        enemies.add(new TankEnemy(spawnX, spawnY, wave.speed * 0.5f));
                        break;
                    case "SHOOTER":
                        enemies.add(new ShooterEnemy(spawnX, spawnY, wave.speed));
                        break;
                    case "SPLITTER":
                        enemies.add(new SplitterEnemy(spawnX, spawnY, wave.speed));
                        break;
                }
                spawnIndex++;
            }
        }

    }

    public void spawnWave10(Wave wave, Array<Enemy> enemies) {
        int screenWidth = 1366;
        int screenHeight = 768;

        if (wave.spawnEdge == -1) {
            if (edgeChangeTimer >= edgeChangeInterval) {
                edgeChangeTimer = 0;
                currentEdge = (int) (Math.random() * 4);
            }
        } else {
            currentEdge = wave.spawnEdge;
        }

        float spawnX = 0, spawnY = 0;

        switch (currentEdge) {
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

        enemies.add(new AmalgamEnemy(spawnX, spawnY, wave.speed * 0.25f));
    }

    public void update(Array<Enemy> enemies, float delta) {
        if (betweenWaves) {
            return;
        }

        if (spawnIndex >= waves.get(currentWave).totalCount && enemies.size == 0) {
            betweenWaves = true;
            introTimer = 3f;
            spawnIndex = 0;
            spawnTimer = 0f;
            edgeChangeTimer = 0f;
            currentWave++;
            if (currentWave < 2) currentMap = 0;
            else if (currentWave < 4) currentMap = 1;
            else if (currentWave < 6) currentMap = 2;
            else if (currentWave < 8) currentMap = 3;
            else currentMap = 4;
            if (currentWave >= waves.size) {
                gameWon = true;
                return;
            }
            return;
        }
        spawnTimer += delta;
        edgeChangeTimer += delta;
        if (currentWave == 9) {
            updateWave10(waves.get(currentWave), enemies);
            return;
        }
        spawnWave(waves.get(currentWave), enemies);
    }

    public void updateWave10(Wave wave, Array<Enemy> enemies) {
        if (!bossSpawned) {
            spawnWave10(wave, enemies);
            bossSpawned = true;
        }
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (enemy instanceof AmalgamEnemy) {
                if (((AmalgamEnemy) enemy).canSummon) {
                    float summonCount = 4;
                    ((AmalgamEnemy) enemy).canSummon = false;
                    for (int j = 0; j < summonCount; j++) {

                        float roll = (float) Math.random();
                        float offsetX = (float) (Math.random() * 40f) - 20f + enemy.size;
                        float offsetY = (float) (Math.random() * 40f) - 20f + enemy.size;

                        if (roll < 0.2f) {
                            enemies.add(new BasicEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed));

                        } else if (roll < 0.4f) {
                            enemies.add(new ShooterEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed));
                        } else if (roll < 0.6f) {
                            enemies.add(new TankEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed * 0.5f));
                        } else if (roll < 0.8f) {
                            enemies.add(new SplitterEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed * 0.5f));
                        } else {
                            enemies.add(new FastEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed * 1.8f));
                        }
                    }
                }

                if (((AmalgamEnemy) enemy).shouldSplit) {
                    ((AmalgamEnemy) enemy).shouldSplit = false;
                    float offsetX = (float) (Math.random() * 40f) - 20f;
                    float offsetY = (float) (Math.random() * 40f) - 20f;
                    enemies.add(new AmalgamEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed, true));
                    offsetX = (float) (Math.random() * 40f) - 20f;
                    offsetY = (float) (Math.random() * 40f) - 20f;
                    enemies.add(new AmalgamEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed, true));
                    enemies.removeIndex(i);
                }
            }
        }
    }

    public void tickIntro(float delta) {
        introTimer -= delta;
        if (introTimer <= 0) {
            betweenWaves = false;
        }
    }

    public void clear() {
        waves.clear();
        buildWaves();
        currentWave = 0;
        spawnIndex = 0;
        introTimer = 0;
        edgeChangeTimer = 0;
        betweenWaves = true;
        gameWon = false;
        bossSpawned = false;
    }

    private void buildWaves() {
        // Wave setup - I think I should comment for my own sanity maybe later.
        waves.add(new Wave(6, 0, 0, 0, 0, 2.5f, -1, 80f));
        waves.add(new Wave(5, 3, 0, 0, 0, 2f, -1, 90f));
        waves.add(new Wave(5, 4, 0, 0, 0, 1.8f, -1, 95f));
        waves.add(new Wave(4, 3, 2, 0, 0, 1.8f, -1, 100f));
        waves.add(new Wave(4, 2, 1, 2, 0, 1.5f, -1, 105f));
        waves.add(new Wave(3, 2, 2, 3, 0, 1.5f, -1, 110f));
        waves.add(new Wave(3, 3, 1, 1, 2, 1.3f, -1, 115f));
        waves.add(new Wave(4, 3, 2, 2, 2, 1.2f, -1, 120f));
        waves.add(new Wave(3, 5, 1, 2, 3, 1f, -1, 130f));
        waves.add(new Wave(0, 0, 0, 0, 0, 1f, -1, 140f));
    }
}
