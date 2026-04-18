package arena.shooter;

import com.badlogic.gdx.graphics.Color;

public class TankEnemy extends Enemy {
    public TankEnemy(float x, float y, float speed) {
        super(x, y, 35f, speed, 10, Color.PURPLE);
    }
}

