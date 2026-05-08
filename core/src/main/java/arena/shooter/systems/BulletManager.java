package arena.shooter.systems;

import arena.shooter.core.Constants;
import arena.shooter.core.GameAssets;
import arena.shooter.entities.Bullet;
import arena.shooter.util.Drawable;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class BulletManager implements Drawable {
    public Array<Bullet> bullets;
    public int bulletsFired;
    public int bulletsHit;

    public BulletManager() {
        bullets = new Array<>();
        bulletsFired = 0;
        bulletsHit = 0;
    }

    public void update(float delta) {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (bullet.isOffScreen(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT)) {
                bullets.removeIndex(i);
            }
        }
    }

//    public void draw(ShapeRenderer shape) {
//        shape.setColor(Color.YELLOW);
//        for (Bullet bullet : bullets) {
//            shape.circle(bullet.x, bullet.y, bullet.size);
//        }
//    }

    public void draw(SpriteBatch batch, GameAssets assets) {
        float rotation = 0;
        for (Bullet bullet : bullets) {
            rotation += 5f;
            batch.draw(assets.fireballTextureRegion, bullet.x - bullet.size, bullet.y - bullet.size, bullet.size, bullet.size, bullet.size * 6, bullet.size * 6, 1f, 1f, rotation);
        }
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
        bulletsFired++;
    }

    public void clear() {
        bullets.clear();
        bulletsFired = 0;
        bulletsHit = 0;
    }
}
