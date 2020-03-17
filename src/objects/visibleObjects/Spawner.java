package objects.visibleObjects;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import objects.behavior.StaticObject;

import java.util.List;
import java.util.Random;

public class Spawner extends StaticObject {
    private SpawnArea spawnArea;

    public Spawner(double x, double y, BorderPane borderPane) {
        super(x, y, borderPane, new Image("file:resources/images/spawner.png"));
        this.spawnArea = new SpawnArea();
    }

    public void spawnSheeps(List<Sheep> sheeps, int numOfSheeps, BorderPane root) {
        Random rand = new Random();
        int initDirection;
        double initSpeedX = rand.nextDouble();

        for (int i = 0; i < numOfSheeps; i++) {
            double spawnPosX = spawnArea.minX + (spawnArea.maxX - spawnArea.minX) * rand.nextDouble();
            double spawnPosY = spawnArea.minY + (spawnArea.maxY - spawnArea.minY) * rand.nextDouble();

            initDirection = rand.nextInt(100) >= 50 ? 1 : -1;

            sheeps.add(new Sheep(spawnPosX, spawnPosY, initDirection, initSpeedX, root));
        }
    }

    // inner class
    private class SpawnArea {
        private double minX = x + 10;
        private double maxX = minX + 90;
        private double minY = y + 90;
        private double maxY = minY + 30;
    }
}
