package arena.shooter.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Particle {
    public float x;
    public float y;
    public float dirX;
    public float dirY;
    public float speed;
    public float life;
    public float maxLife;
    public Color  color;

    public Particle(float x, float y, float dirX, float dirY, Color color) {
        this.x = x;
        this.y = y;
        this.dirX = dirX;
        this.dirY = dirY;
        this.speed = 80f + (float) (Math.random() * 120f);
        this.life = 0.4f + (float) (Math.random() * 0.3f);
        this.maxLife = life;
        this.color = color;
    }

    public void update(float delta)
    {
        x += dirX * speed *delta;
        y += dirY * speed *delta;
        life -= delta;
        speed *= 0.95f;
    }
    public void draw(ShapeRenderer shape)
    {
        float alpha = life/maxLife;
        shape.setColor(color.r, color.g, color.b, alpha);
        float size = 4f * alpha;
        shape.circle(x,y,size);
    }
    public boolean isDead()
    {
        return life <= 0;
    }
}
