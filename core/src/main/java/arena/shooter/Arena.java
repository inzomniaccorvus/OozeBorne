package arena.shooter;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Arena {
    public Array<Rectangle> obstacles;
    private int mapIndex;

    public Arena() {
        this.obstacles = new Array<>();

    }

    public void buildMap(int mapIndex) {
        switch (mapIndex) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    public void draw(ShapeRenderer shape) {
        for (Rectangle obstacle : obstacles) {
            shape.rect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }
    }

    public boolean collidesWithAny(float x, float y, float size) {
        for (Rectangle rect : obstacles) {
            float closestX = Math.max(rect.x, Math.min(x, rect.x + rect.width));
            float closestY = Math.max(rect.y, Math.min(y, rect.y + rect.height));
            float dx = x - closestX;
            float dy = y - closestY;
            if (dx * dx + dy * dy < size * size) return true;
        }
        return false;
    }
}
