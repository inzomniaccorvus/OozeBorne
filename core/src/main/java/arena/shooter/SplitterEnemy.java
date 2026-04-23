package arena.shooter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;


public class SplitterEnemy extends Enemy {
    public int numberOfSplits;

    public SplitterEnemy(float x, float y, float speed) {
        super(x, y, 35f, speed, 5, Color.CYAN, 30);
        numberOfSplits = 3;
    }

    public void split(Array<Enemy> enemies) {


        for (int i = 0; i < numberOfSplits; i++) {

            float roll = (float) Math.random();
            float offset = (float) (Math.random() * 50f) - 25f;

            if (roll < 0.5f) {
                enemies.add(new BasicEnemy(this.x + offset, this.y + offset, speed));
            } else {
                enemies.add(new FastEnemy(this.x + offset, this.y + offset, speed * 1.8f));
            }
        }
    }
}
