package arena.shooter.entities;

import com.badlogic.gdx.graphics.Color;

public class BasicEnemy extends Enemy {
    public BasicEnemy(float x, float y, float speed) {
        super(x, y, 20f, speed, 3, Color.RED, 10);
    }
}
