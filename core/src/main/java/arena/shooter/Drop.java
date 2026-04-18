package arena.shooter;


public class Drop {

    public enum Type {
        SHOTGUN, RAPID, BURST, HEAL, SPEED, FIRERATE, ARMORBUSTER
    }

    public float x;
    public float y;
    public float lifeTime;
    public Type type;

    public boolean isWeapon() {
        return type == Type.SHOTGUN || type == Type.BURST || type == Type.RAPID;
    }

    public Drop(float x, float y, float lifeTime, Type type) {
        this.x = x;
        this.y = y;
        this.lifeTime = lifeTime;
        this.type = type;
    }
}
