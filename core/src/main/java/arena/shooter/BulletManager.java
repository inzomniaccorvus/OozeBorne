package arena.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class BulletManager {
    public Array<Bullet> bullets;

    public BulletManager() {
        bullets = new Array<>();
    }

    public void update(float delta) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (bullet.isOffScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) {
                bullets.removeIndex(i);
            }
        }
    }

    public void draw(ShapeRenderer shape) {
        shape.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            shape.circle(bullet.x, bullet.y, bullet.size);
        }
    }

    public void clear() {
        bullets.clear();
    }
}
