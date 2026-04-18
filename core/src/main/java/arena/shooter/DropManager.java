package arena.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class DropManager {
    private Array<float[]> weaponDrops;
    private Array<float[]> powerups;

    private float weaponDropTimer;
    private float powerupTimer;

    private static final float WEAPON_DROP_INTERVAL = 15f;
    private static final float POWERUP_INTERVAL = 8f;
    private static final float WEAPON_DROP_LIFETIME = 10f;
    private static final float SPAWN_PADDING = 50f;
    private static final float DROP_PICKUP_RADIUS = 30f;
    private static final float DROP_RENDER_RADIUS = 12f;
    private static final float POWERUP_RENDER_SIZE = 20f;

    private static final int WEAPON_DROP_SHOTGUN = 0;
    private static final int WEAPON_DROP_RAPID = 1;
    private static final int POWERUP_HEAL = 0;
    private static final int POWERUP_SPEED = 1;
    private static final int POWERUP_FIRERATE = 2;

    public DropManager() {
        weaponDrops = new Array<>();
        powerups = new Array<>();
    }

    public void update(float delta, Player player, Sound pickupSound) {
        weaponDropTimer += delta;
        if (weaponDropTimer >= WEAPON_DROP_INTERVAL) {
            weaponDropTimer = 0f;
            spawnWeaponDrop();
        }

        powerupTimer += delta;
        if (powerupTimer >= POWERUP_INTERVAL) {
            powerupTimer = 0f;
            spawnPowerup();
        }

        updateWeaponDrops(delta, player, pickupSound);
        updatePowerups(player, pickupSound);
    }

    private void spawnWeaponDrop() {
        float spawnX = SPAWN_PADDING + (float) (Math.random() * (Gdx.graphics.getWidth() - SPAWN_PADDING * 2));
        float spawnY = SPAWN_PADDING + (float) (Math.random() * (Gdx.graphics.getHeight() - SPAWN_PADDING * 2));
        float weaponType = Math.random() < 0.5 ? WEAPON_DROP_SHOTGUN : WEAPON_DROP_RAPID;
        weaponDrops.add(new float[]{spawnX, spawnY, weaponType, WEAPON_DROP_LIFETIME});
    }

    private void spawnPowerup() {
        float spawnX = SPAWN_PADDING + (float) (Math.random() * (Gdx.graphics.getWidth() - SPAWN_PADDING * 2));
        float spawnY = SPAWN_PADDING + (float) (Math.random() * (Gdx.graphics.getHeight() - SPAWN_PADDING * 2));
        float powerupType = (float) (int) (Math.random() * 3);
        powerups.add(new float[]{spawnX, spawnY, powerupType});
    }

    private void updateWeaponDrops(float delta, Player player, Sound pickupSound) {
        for (int i = weaponDrops.size - 1; i >= 0; i--) {
            float[] drop = weaponDrops.get(i);
            drop[3] -= delta;

            if (drop[3] <= 0) {
                weaponDrops.removeIndex(i);
                continue;
            }

            float distanceX = drop[0] - player.centerX();
            float distanceY = drop[1] - player.centerY();
            float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            if (distance < DROP_PICKUP_RADIUS) {
                Player.WeaponType weaponType = (drop[2] == WEAPON_DROP_SHOTGUN)
                        ? Player.WeaponType.SHOTGUN
                        : Player.WeaponType.RAPID_FIRE;
                player.equipWeapon(weaponType);
                pickupSound.play(1f);
                weaponDrops.removeIndex(i);
            }
        }
    }

    private void updatePowerups(Player player, Sound pickupSound) {
        for (int i = powerups.size - 1; i >= 0; i--) {
            float[] powerup = powerups.get(i);
            float distanceX = powerup[0] - player.centerX();
            float distanceY = powerup[1] - player.centerY();
            float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);

            if (distance < DROP_PICKUP_RADIUS) {
                if (powerup[2] == POWERUP_HEAL) player.heal(20);
                else if (powerup[2] == POWERUP_SPEED) player.applySpeedBoost();
                else if (powerup[2] == POWERUP_FIRERATE) player.applyFirerateBoost();
                pickupSound.play(1f);
                powerups.removeIndex(i);
            }
        }
    }

    public void draw(ShapeRenderer shape) {
        for (float[] drop : weaponDrops) {
            shape.setColor(drop[2] == WEAPON_DROP_SHOTGUN ? Color.ORANGE : Color.GREEN);
            shape.circle(drop[0], drop[1], DROP_RENDER_RADIUS);
        }

        for (float[] powerup : powerups) {
            if (powerup[2] == POWERUP_HEAL) shape.setColor(Color.GREEN);
            else if (powerup[2] == POWERUP_SPEED) shape.setColor(Color.CYAN);
            else shape.setColor(Color.YELLOW);
            shape.rect(powerup[0] - POWERUP_RENDER_SIZE / 2, powerup[1] - POWERUP_RENDER_SIZE / 2,
                    POWERUP_RENDER_SIZE, POWERUP_RENDER_SIZE);
        }
    }

    public void clear() {
        weaponDrops.clear();
        powerups.clear();
        weaponDropTimer = 0f;
        powerupTimer = 0f;
    }
}
