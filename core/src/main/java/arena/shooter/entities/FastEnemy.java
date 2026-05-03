package arena.shooter.entities;

import com.badlogic.gdx.graphics.Color;

public class FastEnemy extends Enemy {
    public FastEnemy(float x, float y, float speed) {
        super(x, y, 12f, speed, 1, Color.YELLOW, 15);
    }

}
