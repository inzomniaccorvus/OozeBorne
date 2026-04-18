package arena.shooter;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DamageNumber{
    public float x;
    public float y;
    public float life;
    public float MaxLife;
    public int amount;

    public DamageNumber(float x, float y, int amount){
        this.x = x;
        this.y = y;
        this.amount = amount;
        this.life = 0.6f;
        this.MaxLife = life;
    }
    public void update(float delta){
        y+= 40f * delta;
        life -= delta;
    }

    public void draw(SpriteBatch batch, BitmapFont font){
        float alpha = life/MaxLife;
        font.setColor(1f,1f,0f,alpha);
        font.draw(batch, Integer.toString(amount), x, y);
    }

    public boolean isDead(){
        return life <= 0;
    }
}
