package arena.shooter;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Enemy {
    public float x, y;
    public float size;
    public float speed;
    public int hp;
    public Color color;
    public int scoreValue;

    public Enemy(float x, float y, float size, float speed, int hp, Color color, int scoreValue) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.hp = hp;
        this.color = color;
        this.scoreValue = scoreValue;
    }

    public void update(float delta, float targetX, float targetY) {
        float dx = targetX - x;
        float dy = targetY - y;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        x += dx * speed * delta;
        y += dy * speed * delta;
    }

    public void draw(ShapeRenderer shape) {
        shape.setColor(color);
        shape.circle(x, y, size);
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void takeDamage(int amount) {
        hp -= amount;
    }

    public void applySeparation(Array<Enemy> enemies, float delta) {
        float separationRadius = 40f;
        float separationStrength = 60f;

        float pushX = 0f;
        float pushY = 0f;

        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy other = enemies.get(i);
            if(other == this) continue;

            float dx = x - other.x;
            float dy = y - other.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist < separationRadius && dist > 0) {
                float force = (separationRadius - dist) / separationRadius;
                pushX += (dx/dist) * force * separationStrength;
                pushY += (dy/dist) * force * separationStrength;
            }
        }
        x += pushX * delta;
        y += pushY * delta;
    }
}

