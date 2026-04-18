package arena.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;


public class DropManager {
    Array<Drop> drops;

    private float weaponDropTimer;
    private float powerupTimer;

    private static final float WEAPON_DROP_INTERVAL = 15f;
    private static final float POWERUP_INTERVAL = 8f;
    private static final float WEAPON_DROP_LIFETIME = 10f;
    private static final float SPAWN_PADDING = 50f;
    private static final float DROP_PICKUP_RADIUS = 30f;
    private static final float DROP_RENDER_RADIUS = 12f;
    private static final float POWERUP_RENDER_SIZE = 20f;

    public DropManager() {
        drops = new Array<>();
    }


    private void spawnWeaponDrop() {
        float spawnX = SPAWN_PADDING + (float) (Math.random() * (Gdx.graphics.getWidth() - SPAWN_PADDING * 2));
        float spawnY = SPAWN_PADDING + (float) (Math.random() * (Gdx.graphics.getHeight() - SPAWN_PADDING * 2));
        Drop.Type[] weaponTypes = {Drop.Type.SHOTGUN, Drop.Type.RAPID, Drop.Type.BURST};
        Drop.Type type = weaponTypes[(int) (Math.random() * weaponTypes.length)];
        drops.add(new Drop(spawnX, spawnY, WEAPON_DROP_LIFETIME, type));
    }

    private void spawnPowerup() {
        float spawnX = SPAWN_PADDING + (float) (Math.random() * (Gdx.graphics.getWidth() - SPAWN_PADDING * 2));
        float spawnY = SPAWN_PADDING + (float) (Math.random() * (Gdx.graphics.getHeight() - SPAWN_PADDING * 2));
        Drop.Type[] powerupTypes = {Drop.Type.HEAL, Drop.Type.SPEED, Drop.Type.FIRERATE, Drop.Type.ARMORBUSTER};
        Drop.Type type = powerupTypes[(int) (Math.random() * powerupTypes.length)];
        drops.add(new Drop(spawnX, spawnY, -1f, type)); //powerup drops don't disappear
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

        for (int i = drops.size - 1; i >= 0; i--) {
            Drop drop = drops.get(i);
            if (drop.isWeapon()) {
                drop.lifeTime -= delta;
                if (drop.lifeTime <= 0) {
                    drops.removeIndex(i);
                    continue;
                }
            }
            float distanceX = drop.x - player.centerX();
            float distanceY = drop.y - player.centerY();
            float distance = (float) Math.sqrt(distanceX * distanceX + distanceY * distanceY);
            if (distance < DROP_PICKUP_RADIUS) {
                if (drop.isWeapon()) {
                    Player.WeaponType weaponType;
                    if (drop.type == Drop.Type.SHOTGUN) weaponType = Player.WeaponType.SHOTGUN;
                    else if (drop.type == Drop.Type.BURST) weaponType = Player.WeaponType.BURST;
                    else weaponType = Player.WeaponType.RAPID_FIRE;
                    player.equipWeapon(weaponType);
                } else {
                    if (drop.type == Drop.Type.HEAL) player.heal(20);
                    else if (drop.type == Drop.Type.SPEED) player.applySpeedBoost();
                    else if (drop.type == Drop.Type.FIRERATE) player.applyFirerateBoost();
                    else if (drop.type == Drop.Type.ARMORBUSTER) player.applyDamageBoost();
                }
                pickupSound.play(1f);
                drops.removeIndex(i);
            }

        }
    }


    public void draw(ShapeRenderer shape) {
        for (int i = drops.size - 1; i >= 0; i--) {
            Drop drop = drops.get(i);
            if (drop.type == Drop.Type.SHOTGUN) {
                shape.setColor(Color.ORANGE);
            } else if (drop.type == Drop.Type.RAPID) {
                shape.setColor(Color.GREEN);
            } else if (drop.type == Drop.Type.BURST) {
                shape.setColor(Color.PINK);
            } else if (drop.type == Drop.Type.SPEED) {
                shape.setColor(Color.CYAN);
            } else if (drop.type == Drop.Type.FIRERATE) {
                shape.setColor(Color.YELLOW);
            } else if (drop.type == Drop.Type.HEAL) {
                shape.setColor(Color.GREEN);
            } else if (drop.type == Drop.Type.ARMORBUSTER) {
                shape.setColor(Color.RED);
            }

            if (drop.isWeapon()) {
                shape.circle(drop.x, drop.y, DROP_RENDER_RADIUS);
            } else {
                shape.rect(drop.x - POWERUP_RENDER_SIZE / 2, drop.y - POWERUP_RENDER_SIZE / 2, POWERUP_RENDER_SIZE, POWERUP_RENDER_SIZE);
            }
        }
    }

    public void clear() {
        drops.clear();
        weaponDropTimer = 0f;
        powerupTimer = 0f;
    }
}
