package objects.behavior;

import objects.visibleObjects.Hole;
import objects.visibleObjects.Sheep;

public interface Collidable {
    double getX();
    double getY();
    double getWidth();
    double getHeight();
    int getDirection();

    // check for collision
    default boolean touch(Collidable collidableObject) {
        // corners of first object
        double firstMinX = this.getX();
        double firstMaxX = this.getX() + this.getWidth();
        double firstMinY = this.getY();
        double firstMaxY = this.getY() + this.getHeight();

        // corners of second object
        double secondMinX = collidableObject.getX();
        double secondMaxX = collidableObject.getX() + collidableObject.getWidth();
        double secondMinY = collidableObject.getY();
        double secondMaxY = collidableObject.getY() + collidableObject.getHeight();

        // other variables
        double firstMiddleX = this.getX() + this.getWidth() / 2;
        double firstMiddleY = this.getY() + this.getHeight() / 2;
        double secondMiddleX = secondMinX + collidableObject.getWidth() / 2;
        int direction = this.getDirection();

        if(collidableObject instanceof Hole)
            secondMaxX -= 30;

        // check if y of first object collide with y of second object
        boolean yCheck = firstMiddleY >= secondMinY && firstMiddleY <= secondMaxY || firstMinY >= secondMinY && firstMinY <= secondMaxY || firstMaxY >= secondMinY && firstMaxY <= secondMaxY;

        // check for x collision
        if(collidableObject instanceof Sheep)
            return ((direction == 1 && firstMaxX >= secondMinX && firstMaxX <= secondMaxX) || (direction == -1 && firstMinX <= secondMaxX && firstMaxX >= secondMaxX)) && yCheck;
        else
            return firstMaxX >= secondMiddleX && firstMiddleX <= secondMaxX && yCheck;
    }
}
