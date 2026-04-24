package arena.shooter;

import com.badlogic.gdx.Gdx;
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

    public WaveManager() {
        waves = new Array<Wave>();
        betweenWaves = true;
        gameWon = false;
        introTimer = 3f;
        currentWave = 0;
        currentEdge = 0;
        spawnIndex = 0;
        edgeChangeTimer = 0;

        //Placeholder for all 10 waves
        for (int i = 0; i < numberOfWaves; i++) {
            waves.add(new Wave(1, 1, 1, 1, 1, 2f, -1, 80f));
        }
    }

    public void spawnWave(Wave wave, Array<Enemy> enemies) {

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

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

    public void update(Array<Enemy> enemies, float delta) {
            if (spawnIndex >= waves.get(currentWave).totalCount && enemies.size == 0) {
                betweenWaves = true;
                introTimer = 3f;
                spawnIndex = 0;
                spawnTimer = 0f;
                edgeChangeTimer = 0f;
                currentWave++;
                if (currentWave >= waves.size) {
                    gameWon = false;
                    return;
                }
                return;
            }
            spawnTimer += delta;
            edgeChangeTimer += delta;
            spawnWave(waves.get(currentWave), enemies);
        }

    public void tickIntro(float delta) {
        introTimer -= delta;
        if (introTimer <= 0) {
            betweenWaves = false;
        }
    }
    public void clear() {
        waves.clear();
    }
}
