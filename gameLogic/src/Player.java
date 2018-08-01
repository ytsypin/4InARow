import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {
    private String name;
    private boolean isBot;
    private int turnsPlayed;

    public Player(String name, boolean isBot){
        this.name = name;
        this.isBot = isBot;
        this.turnsPlayed = 0;
    }

    public String getName() {
        return name;
    }

    public boolean isBot() {
        return isBot;
    }

    public int getTurnsPlayed() {
        return turnsPlayed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return isBot == player.isBot &&
                turnsPlayed == player.turnsPlayed &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isBot, turnsPlayed);
    }

    public void addTurnPlayed() {
        turnsPlayed++;
    }
}
