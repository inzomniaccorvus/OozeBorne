package arena.shooter;

public class Bullet {
    public float x,y;
    public float dirX, dirY;
    public float speed = 500f;
    public float size = 6f;
    public int damage = 1;

    public Bullet(float x, float y, float dirX, float dirY) {
        this.x = x;
        this.y = y;
        this.dirX = dirX;
        this.dirY = dirY;
    }

    public void update(float delta) {
        x += dirX * speed * delta;
        y += dirY * speed * delta;
    }

    public boolean isOffScreen(int screenWidth, int screenHeight) {
        return x<0 || x>screenWidth || y<0 || y>screenHeight;
    }
}
