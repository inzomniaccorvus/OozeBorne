package arena.shooter.systems;

import com.badlogic.gdx.utils.Array;

public class Wave {
    public int basicCount, fastCount, tankCount, shooterCount, splitterCount, totalCount;
    public float spawnInterval;
    public int spawnEdge;
    public float speed;
    public Array<String> spawnQueue;

    public Wave(int basicCount, int fastCount, int tankCount, int shooterCount, int splitterCount, float spawnInterval, int spawnEdge, float speed) {
        spawnQueue = new Array<String>();
        this.basicCount = basicCount;
        this.fastCount = fastCount;
        this.tankCount = tankCount;
        this.shooterCount = shooterCount;
        this.splitterCount = splitterCount;
        this.spawnInterval = spawnInterval;
        this.spawnEdge = spawnEdge;
        this.speed = speed;
        this.totalCount = basicCount + fastCount + tankCount + shooterCount + splitterCount;

        for (int i = 0; i < basicCount; i++) spawnQueue.add("BASIC");
        for (int i = 0; i < fastCount; i++) spawnQueue.add("FAST");
        for (int i = 0; i < tankCount; i++) spawnQueue.add("TANK");
        for (int i = 0; i < shooterCount; i++) spawnQueue.add("SHOOTER");
        for (int i = 0; i < splitterCount; i++) spawnQueue.add("SPLITTER");

        spawnQueue.shuffle();
    }

}
