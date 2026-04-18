package arena.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shape;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;

    private com.badlogic.gdx.audio.Sound shootSound;
    private com.badlogic.gdx.audio.Sound hitSound;
    private com.badlogic.gdx.audio.Sound explosionSound;
    private com.badlogic.gdx.audio.Sound pickupSound;


    private boolean gameOver = false;
    private boolean mainMenu = true;

    com.badlogic.gdx.math.Vector3 mousevec = new com.badlogic.gdx.math.Vector3();

    private int score = 0;
    private float survivalTime = 0f;

    private Array<Bullet> bullets = new Array<>();

    private float playerX = 300;
    private float playerY = 250;
    private float playerSize = 30;
    private int playerHP = 100;
    private float playerInvincibleTimer = 0f;

    private Array<Enemy> enemies = new Array<>();
    private float spawnTimer = 0f;
    private float baseSpawnInterval = 2f;

    private float damageFlashTimer;

    private float shakeDuration = 0f;
    private float shakeIntensity = 5f;
    private float shakeX = 0f;
    private float shakeY = 0f;

    public enum WeaponType {
        PISTOL, SHOTGUN, RAPID_FIRE;
    }

    private float weaponTimer = 0f;
    private float weaponDuration = 10f;


    private WeaponType currentWeapon = WeaponType.PISTOL;

    private float fireTimer = 0f;

    private Array<float[]> weaponDrops = new Array<>();
    private float dropSpawnTimer = 0f;
    private float dropSpawnInterval = 15f;


    private Array<float[]> powerups = new Array<>();
    private float powerupDropTimer = 0f;
    private float powerupDropInterval = 8f;
    private float speedBoostTimer = 0f;
    private float firerateBoostTimer = 0f;

    private Array<Particle> particles = new Array<>();
    private Array<DamageNumber> damageNumbers = new Array<>();

    @Override
    public void create() {
        shape = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        shootSound = Gdx.audio.newSound(Gdx.files.internal("laserShoot.wav"));
        hitSound = Gdx.audio.newSound(Gdx.files.internal("hitHurt.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        pickupSound = Gdx.audio.newSound(Gdx.files.internal("powerUp.wav"));
    }

    @Override
    public void render() {
        if (mainMenu) {
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
            batch.begin();
            font.draw(batch, "ARENA SHOOTER", Gdx.graphics.getWidth() / 2f - 90, Gdx.graphics.getHeight() / 2f + 40);
            font.draw(batch, "Press ENTER to Start", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 20);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                mainMenu = false;
            }
            return;
        }

        if (gameOver) {
            ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
            batch.begin();
            font.draw(batch, "GAME OVER", Gdx.graphics.getWidth() / 2f - 60, Gdx.graphics.getHeight() / 2f + 40);
            font.draw(batch, "Score: " + score, Gdx.graphics.getWidth() / 2f - 40, Gdx.graphics.getHeight() / 2f);
            font.draw(batch, "Time: " + (int) survivalTime + "s", Gdx.graphics.getWidth() / 2f - 40, Gdx.graphics.getHeight() / 2f - 40);
            font.draw(batch, "Press R to Restart", Gdx.graphics.getWidth() / 2f - 80, Gdx.graphics.getHeight() / 2f - 80);
            batch.end();

            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                restartGame();
            }
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        float speed = speedBoostTimer > 0 ? 350f : 200f;

        survivalTime += delta;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) playerY += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) playerX -= speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) playerX += speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) playerY -= speed * delta;
        fireTimer -= delta;

        mousevec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousevec);

        float mouseX = mousevec.x;
        float mouseY = mousevec.y;

        float dx = mouseX - playerCenterX();
        float dy = mouseY - playerCenterY();

        float len = (float) Math.sqrt(dx * dx + dy * dy);
        dx /= len;
        dy /= len;

        switch (currentWeapon) {
            case PISTOL:
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && fireTimer <= 0) {
                    bullets.add(new Bullet(playerCenterX(), playerCenterY(), dx, dy));
                    shootSound.play(0.4f);
                    fireTimer = firerateBoostTimer > 0 ? 0.1f : 0.3f;
                }
                break;
            case SHOTGUN:
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && fireTimer <= 0) {
                    float baseAngle = (float) Math.atan2(dy, dx);
                    float[] offsets = new float[]{-0.35f, -0.175f, 0f, 0.175f, 0.35f};
                    for (float offset : offsets) {
                        float angle = baseAngle + offset;
                        bullets.add(new Bullet(playerCenterX(), playerCenterY(), (float) Math.cos(angle), (float) Math.sin(angle)));
                        shootSound.play(0.4f);
                    }
                    fireTimer = firerateBoostTimer > 0 ? 0.3f : 0.6f;
                }
                break;
            case RAPID_FIRE:
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && fireTimer <= 0) {
                    bullets.add(new Bullet(playerCenterX(), playerCenterY(), dx, dy));
                    shootSound.play(0.4f);
                    fireTimer = firerateBoostTimer > 0 ? 0.05f : 0.1f;
                }
                break;
        }

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);
            if (bullet.isOffScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) bullets.removeIndex(i);
        }

        playerX = Math.max(0, Math.min(Gdx.graphics.getWidth() - playerSize, playerX));
        playerY = Math.max(0, Math.min(Gdx.graphics.getHeight() - playerSize, playerY));


        spawnTimer += delta;
        float spawnInterval = Math.max(0.3f, baseSpawnInterval - survivalTime * 0.02f);
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            spawnEnemy();
        }

        for (Enemy e : enemies) {
            e.update(delta, playerCenterX(), playerCenterY());
            e.applySeperation(enemies, delta);
        }

        for (int i = particles.size - 1; i >= 0; i--) {
            particles.get(i).update(delta);
            if (particles.get(i).isDead()) {
                particles.removeIndex(i);
            }
        }

        for (int i = damageNumbers.size - 1; i >= 0; i--) {
            damageNumbers.get(i).update(delta);
            if (damageNumbers.get(i).isDead()) {
                damageNumbers.removeIndex(i);
            }
        }

        dropSpawnTimer += delta;
        if (dropSpawnTimer >= dropSpawnInterval) {
            dropSpawnTimer = 0f;
            float padding = 50f;
            float dx2 = padding + (float) (Math.random() * (Gdx.graphics.getWidth() - padding * 2));
            float dy2 = padding + (float) (Math.random() * (Gdx.graphics.getHeight() - padding * 2));
            float type = (float) (Math.random() < 0.5 ? 0 : 1);
            weaponDrops.add(new float[]{dx2, dy2, type, 10f});
        }

        powerupDropTimer += delta;
        if (powerupDropTimer > powerupDropInterval) {
            powerupDropTimer = 0f;
            float padding = 50f;
            float px = padding + (float) (Math.random() * (Gdx.graphics.getWidth() - padding * 2));
            float py = padding + (float) (Math.random() * (Gdx.graphics.getHeight() - padding * 2));
            float type = (float) (int) (Math.random() * 3);
            powerups.add(new float[]{px, py, type});
        }
        speedBoostTimer -= delta;
        firerateBoostTimer -= delta;

        checkCollision();
        checkPlayerCollision();
        playerInvincibleTimer -= delta;
        damageFlashTimer -= delta;

        if (shakeDuration > 0) {
            shakeDuration -= delta;
            shakeX = (float) (Math.random() * shakeIntensity * 2) - shakeIntensity;
            shakeY = (float) (Math.random() * shakeIntensity * 2) - shakeIntensity;
        } else {
            shakeX = 0;
            shakeY = 0;
        }

        if (playerHP <= 0) {
            gameOver = true;
        }

        for (int i = weaponDrops.size - 1; i >= 0; i--) {
            float[] drop = weaponDrops.get(i);
            drop[3] -= delta;

            if (drop[3] <= 0) {
                weaponDrops.removeIndex(i);
                continue;
            }

            float ddx = drop[0] - playerCenterX();
            float ddy = drop[1] - playerCenterY();
            float dist = (float) Math.sqrt(ddx * ddx + ddy * ddy);

            if (dist < playerSize) {
                currentWeapon = drop[2] == 0 ? WeaponType.SHOTGUN : WeaponType.RAPID_FIRE;
                weaponTimer = weaponDuration;
                weaponDrops.removeIndex(i);
                pickupSound.play(1f);
            }
        }

        for (int i = powerups.size - 1; i >= 0; i--) {
            float[] powerup = powerups.get(i);
            float px = powerup[0] - playerCenterX();
            float py = powerup[1] - playerCenterY();
            float dist = (float) Math.sqrt(px * px + py * py);
            if (dist < playerSize) {
                if (powerup[2] == 0) playerHP = Math.min(100, playerHP + 20);
                else if (powerup[2] == 1) speedBoostTimer = 5f;
                else if (powerup[2] == 2) firerateBoostTimer = 5f;
                powerups.removeIndex(i);
                pickupSound.play(1f);
            }
        }

        if (currentWeapon != WeaponType.PISTOL) {
            weaponTimer -= delta;
            if (weaponTimer <= 0) {
                currentWeapon = WeaponType.PISTOL;
            }
        }

        camera.position.set(Gdx.graphics.getWidth() / 2f + shakeX, Gdx.graphics.getHeight() / 2f + shakeY, 0);
        camera.update();
        shape.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1.0f);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        shape.setColor(0.3f, 0f, 0f, 1f);
        shape.rect(10, 20, 200, 20);
        float hpRatio = playerHP / 100f;
        shape.setColor(1f - hpRatio, hpRatio, 0f, 1f);
        shape.rect(10, 20, 200 * hpRatio, 20);

        shape.setColor(damageFlashTimer > 0 ? Color.RED : Color.CYAN);
        shape.rect(playerX, playerY, playerSize, playerSize);
        shape.setColor(Color.YELLOW);
        for (Bullet bullet : bullets) {
            shape.circle(bullet.x, bullet.y, bullet.size);
        }

        for (Enemy e : enemies) {
            e.draw(shape);
        }
        for (Particle p : particles) {
            p.draw(shape);
        }
        for (float[] drop : weaponDrops) {
            shape.setColor(drop[2] == 0 ? Color.ORANGE : Color.GREEN);
            shape.circle(drop[0], drop[1], 12f);
        }
        for (float[] powerup : powerups) {
            if (powerup[2] == 0) {
                shape.setColor(Color.GREEN);
            } else if (powerup[2] == 1) {
                shape.setColor(Color.CYAN);
            } else {
                shape.setColor(Color.YELLOW);
            }
            shape.rect(powerup[0] - 10, powerup[1] - 10, 20, 20);
        }
        shape.end();


        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.WHITE);
        shape.line(playerCenterX(), playerCenterY(), mouseX, mouseY);
        shape.end();

        batch.begin();
        for (DamageNumber damageNumber : damageNumbers) {
            damageNumber.draw(batch, font);
        }
        font.setColor(Color.WHITE);
        //font.draw(batch, "HP: " + playerHP, 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, playerHP + "/100", 220, 36);
        font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Time: " + (int) survivalTime + "s", 10, Gdx.graphics.getHeight() - 70);
        font.draw(batch, "Weapon: " + currentWeapon, 10, Gdx.graphics.getHeight() - 100);
        if (speedBoostTimer > 0) {
            font.draw(batch, "Speed Boost: " + (int) speedBoostTimer + "s", 10, Gdx.graphics.getHeight() - 130);
        }
        if (firerateBoostTimer > 0) {
            font.draw(batch, "Firerate Boost: " + (int) firerateBoostTimer + "s", 10, Gdx.graphics.getHeight() - 160);
        }
        if (currentWeapon != WeaponType.PISTOL) {
            font.draw(batch, "Weapon time: " + (int) weaponTimer + "s", 10, Gdx.graphics.getHeight() - 180);
        }
        batch.end();


    }

    @Override
    public void dispose() {
        shape.dispose();
        batch.dispose();
        font.dispose();

        shootSound.dispose();
        hitSound.dispose();
        explosionSound.dispose();
        pickupSound.dispose();
    }

    private float playerCenterX() {
        return playerX + playerSize / 2;
    }

    private float playerCenterY() {
        return playerY + playerSize / 2;
    }

    private void spawnEnemy() {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();

        int edge = (int) (Math.random() * 4);
        float x = 0, y = 0;

        switch (edge) {
            case 0:
                x = (float) (Math.random() * screenW);
                y = screenH;
                break;
            case 1:
                x = (float) (Math.random() * screenW);
                y = 0;
                break;
            case 2:
                x = 0;
                y = (float) (Math.random() * screenH);
                break;
            case 3:
                x = screenW;
                y = (float) (Math.random() * screenH);
                break;
            default:
                break;
        }
        float scaleSpeed = 80f + survivalTime * 0.5f;
        float roll = (float) Math.random();
        if (roll < 0.6f) {
            enemies.add(new BasicEnemy(x, y, scaleSpeed));
        } else if (roll < 0.85f) {
            enemies.add(new FastEnemy(x, y, scaleSpeed * 1.8f));
        } else {
            enemies.add(new TankEnemy(x, y, scaleSpeed * 0.5f));
        }
    }

    private void checkCollision() {
        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);

            for (int j = enemies.size - 1; j >= 0; j--) {
                Enemy e = enemies.get(j);

                float dx = e.x - b.x;
                float dy = e.y - b.y;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);

                if (distance < b.size + e.size) {
                    e.takeDamage(1);
                    hitSound.play(0.6f);
                    damageNumbers.add(new DamageNumber(e.x, e.y, 1));
                    bullets.removeIndex(i);
                    if (e.isDead()) {
                        explosionSound.play(0.8f);
                        enemies.removeIndex(j);
                        score += 10;
                        spawnParticles(e.x, e.y, e.color);
                    }
                    break;
                }

            }

        }
    }

    private void checkPlayerCollision() {
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);

            float dx = e.x - playerCenterX();
            float dy = e.y - playerCenterY();
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance < e.size + playerSize / 2) {
                if (playerInvincibleTimer <= 0) {
                    playerHP -= 10;
                    playerInvincibleTimer = 0.5f;
                    damageFlashTimer = 0.2f;
                    shakeDuration = 0.2f;
                }
            }
        }
    }

    private void restartGame() {
        playerX = 300;
        playerY = 250;
        playerHP = 100;
        score = 0;
        survivalTime = 0f;
        spawnTimer = 0f;
        fireTimer = 0f;
        dropSpawnTimer = 0f;
        powerupDropTimer = 0f;
        speedBoostTimer = 0f;
        firerateBoostTimer = 0f;
        shakeDuration = 0f;
        damageFlashTimer = 0f;
        playerInvincibleTimer = 0f;
        currentWeapon = WeaponType.PISTOL;
        weaponTimer = 0f;
        enemies.clear();
        bullets.clear();
        weaponDrops.clear();
        powerups.clear();
        particles.clear();
        damageNumbers.clear();
        gameOver = false;
        mainMenu = false;
    }

    private void spawnParticles(float x, float y, Color color) {
        int count = 6 + (int) (Math.random() * 4);
        for (int i = 0; i < count; i++) {
            float angle = (float) (Math.random() * 2 * Math.PI);
            float dirX = (float) Math.cos(angle);
            float dirY = (float) Math.sin(angle);
            particles.add(new Particle(x, y, dirX, dirY, color));
        }
    }
}
