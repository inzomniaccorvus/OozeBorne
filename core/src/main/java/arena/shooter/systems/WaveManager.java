package arena.shooter.systems;

import arena.shooter.entities.*;
import com.badlogic.gdx.utils.Array;

public class WaveManager {
    public Array<Wave> waves;
    public int currentWave;
    public boolean betweenWaves;
    public boolean gameWon;
    public float introTimer;
    public int spawnIndex;
    public float spawnTimer;
    public int currentEdge;
    public float edgeChangeTimer;
    private float edgeChangeInterval = 8f;

    public boolean bossSpawned;
    public int musicZone;

    public WaveManager() {
        waves = new Array<Wave>();
        betweenWaves = true;
        gameWon = false;
        introTimer = 3f;
        currentWave = 9;
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
                        enemies.add(new FastEnemy(spawnX, spawnY, wave.speed * 2f));
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
        if (betweenWaves) return;

        if (currentWave < 2) musicZone = 0;
        else if (currentWave < 4) musicZone = 1;
        else if (currentWave < 6) musicZone = 2;
        else if (currentWave < 9) musicZone = 3;
        else musicZone = 4;

        if (currentWave == 9) {
            spawnTimer += delta;
            edgeChangeTimer += delta;
            updateWave10(waves.get(currentWave), enemies);
            if (bossSpawned && enemies.size == 0) {
                gameWon = true;
            }
            return;
        }

        if (spawnIndex >= waves.get(currentWave).totalCount && enemies.size == 0) {
            betweenWaves = true;
            introTimer = 3f;
            spawnIndex = 0;
            spawnTimer = 0f;
            edgeChangeTimer = 0f;
            currentWave++;
            return;
        }

        spawnTimer += delta;
        edgeChangeTimer += delta;
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
                    enemies.add(new AmalgamEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed * 0.5f, true));
                    offsetX = (float) (Math.random() * 40f) - 20f;
                    offsetY = (float) (Math.random() * 40f) - 20f;
                    enemies.add(new AmalgamEnemy(enemy.x + offsetX, enemy.y + offsetY, wave.speed * 0.5f, true));
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
        waves.clear();
        waves.add(new Wave(6, 0, 0, 0, 0, 2.5f, -1, 85f));       // wave 1
        waves.add(new Wave(6, 3, 0, 0, 0, 2f, -1, 95f));          // wave 2
        waves.add(new Wave(6, 4, 1, 0, 0, 1.8f, -1, 100f));       // wave 3
        waves.add(new Wave(5, 4, 2, 1, 0, 1.6f, -1, 108f));       // wave 4
        waves.add(new Wave(5, 4, 2, 2, 1, 1.4f, -1, 115f));       // wave 5
        waves.add(new Wave(5, 4, 3, 3, 1, 1.3f, -1, 120f));       // wave 6
        waves.add(new Wave(4, 5, 3, 3, 2, 1.2f, -1, 128f));       // wave 7
        waves.add(new Wave(5, 5, 3, 4, 2, 1f, -1, 135f));         // wave 8
        waves.add(new Wave(4, 6, 3, 4, 3, 0.9f, -1, 145f));       // wave 9
        waves.add(new Wave(0, 0, 0, 0, 0, 1f, -1, 150f));         // wave 10 boss
    }
}
