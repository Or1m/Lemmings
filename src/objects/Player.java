package objects;

import java.util.Comparator;

// class representing physical player with his score
public class Player implements Comparable<Player> {
    private String name;
    private int score;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public int compareTo(Player p) {
        return getName().compareTo(p.getName());
    }

    public static class scoreComparator implements Comparator<Player>
    {
        public int compare(Player p1, Player p2)
        {
            int score1 = p1.getScore();
            int score2 = p2.getScore();

            return score1 - score2;
        }
    }

    public String toString()
    {
        return name + ": " + score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }


    private String getName() {
        return name;
    }

    private int getScore() {
        return score;
    }
}
