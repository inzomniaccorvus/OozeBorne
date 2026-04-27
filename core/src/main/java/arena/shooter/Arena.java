package arena.shooter;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Arena {
    public Array<Rectangle> obstacles;

    public Arena(int mapIndex) {
        this.obstacles = new Array<>();
        buildMap(mapIndex);

    }

    public void buildMap(int mapIndex) {
        switch (mapIndex) {
            case 0:
                obstacles.add(new Rectangle(84, 388, 233, 185));   // top-left
                obstacles.add(new Rectangle(1047, 388, 233, 185));  // top-right
                obstacles.add(new Rectangle(84, 93, 233, 185));     // bottom-left
                obstacles.add(new Rectangle(1047, 93, 233, 185));   // bottom-right
                break;
            case 1:
                obstacles.add(new Rectangle(154, 453, 288, 175));  // upper-left
                obstacles.add(new Rectangle(923, 453, 288, 175));  // upper-right
                obstacles.add(new Rectangle(154, 138, 288, 175));  // lower-left
                obstacles.add(new Rectangle(923, 138, 288, 175));  // lower-right
                obstacles.add(new Rectangle(541, 298, 184, 175));  // center gear
                break;
            case 2:
                obstacles.add(new Rectangle(477, 613, 288, 155));  // top-center
                obstacles.add(new Rectangle(477, 18, 288, 155));   // bottom-center
                obstacles.add(new Rectangle(0, 263, 253, 245));    // left-center
                obstacles.add(new Rectangle(1112, 263, 253, 245)); // right-center
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
