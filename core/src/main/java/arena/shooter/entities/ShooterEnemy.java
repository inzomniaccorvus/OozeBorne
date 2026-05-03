package arena.shooter.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class ShooterEnemy extends Enemy {

    float shootTimer;
    float shootInterval;
    public Array<Bullet> enemyBullets;

    public ShooterEnemy(float x, float y, float speed) {
        super(x, y, 20f, speed, 3, Color.MAGENTA, 20);
        this.shootTimer = 0;
        this.shootInterval = 2f;
        this.enemyBullets = new Array<Bullet>();
    }

    @Override
    public void update(float delta, float targetX, float targetY) {
        super.update(delta, targetX, targetY);
        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootTimer = 0f;
            float dx = targetX - x;
            float dy = targetY - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            dx /= distance;
            dy /= distance;
            Bullet bullet = new Bullet(x, y, dx, dy);
            enemyBullets.add(bullet);
        }
    }

    public void updateBullets(float delta) {
        for (int i = enemyBullets.size - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            bullet.update(delta);
            if (bullet.isOffScreen(1366,768)) {
                enemyBullets.removeIndex(i);
            }
        }
    }

    public void drawBullets(ShapeRenderer shape) {
        shape.setColor(Color.RED);
        for (Bullet bullet : enemyBullets) {
            shape.circle(bullet.x, bullet.y, bullet.size);
        }
    }
}
