package objects.behavior;

public interface GameObject {
    // base functions of all game objects
    void initUI();
    void updateUI();
    void rotate();

    void addToLayer();
    void removeFromLayer();
}
