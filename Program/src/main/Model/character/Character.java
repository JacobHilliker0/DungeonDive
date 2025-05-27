package main.Model.character;

import main.Model.util.Point;
import main.Model.util.Direction;

public abstract class Character {
    private int health;
    private Point position;

    public Character(final int health, final Point position) {
        this.health = health;
        this.position = position;
    }

    public abstract int attack(final Character target);

    public void takeDamage(final int damage) {
        this.health = Math.max(0, this.health - damage);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void move(final Direction direction) {
        switch (direction) {
            case NORTH:
                position = new Point(position.getX(), position.getY() - 1);
                break;
            case SOUTH:
                position = new Point(position.getX(), position.getY() + 1);
                break;
            case EAST:
                position = new Point(position.getX() + 1, position.getY());
                break;
            case WEST:
                position = new Point(position.getX() - 1, position.getY());
                break;
        }
    }

    public int getHealth() {
        return health;
    }

    public Point getPosition() {
        return position;
    }

    public void setHealth(final int health) {
        this.health = health;
    }

    public void setPosition(final Point position) {
        this.position = position;
    }
}
