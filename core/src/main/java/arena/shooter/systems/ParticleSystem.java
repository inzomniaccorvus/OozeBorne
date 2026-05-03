package arena.shooter.systems;

import arena.shooter.entities.Particle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class ParticleSystem {
    private Array<Particle> particles;
    private Array<DamageNumber> damageNumbers;

    public ParticleSystem() {
        particles = new Array<>();
        damageNumbers = new Array<>();
    }

    public void spawnDeathParticles(float x, float y, Color color) {
        int count = 6 + (int) (Math.random() * 4);
        for (int i = 0; i < count; i++) {
            float angle = (float) (Math.random() * 2 * Math.PI);
            particles.add(new Particle(x, y, (float) Math.cos(angle), (float) Math.sin(angle), color));
        }
    }

    public void spawnDamageNumber(float x, float y, int amount) {
        float offsetX = (float) (Math.random() * 20f) - 10f;
        float offsetY = (float) (Math.random() * 20f) - 10f;
        damageNumbers.add(new DamageNumber(x + offsetX, y + offsetY, amount));
    }

    public void update(float delta) {
        for (int i = particles.size - 1; i >= 0; i--) {
            particles.get(i).update(delta);
            if (particles.get(i).isDead()) particles.removeIndex(i);
        }

        for (int i = damageNumbers.size - 1; i >= 0; i--) {
            damageNumbers.get(i).update(delta);
            if (damageNumbers.get(i).isDead()) damageNumbers.removeIndex(i);
        }
    }

    public void drawShapes(ShapeRenderer shape) {
        for (Particle particle : particles) {
            particle.draw(shape);
        }
    }

    public void drawText(SpriteBatch batch, BitmapFont font) {
        for (DamageNumber damageNumber : damageNumbers) {
            damageNumber.draw(batch, font);
        }
    }

    public void clear() {
        particles.clear();
        damageNumbers.clear();
    }
}
