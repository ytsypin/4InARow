import java.io.Serializable;
import java.util.Objects;

public class Participant implements Serializable {
    private String name;
    private boolean isBot;
    private int turnsPlayed;

    public Participant(String name, boolean isBot){
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
        Participant participant = (Participant) o;
        return isBot == participant.isBot &&
                turnsPlayed == participant.turnsPlayed &&
                Objects.equals(name, participant.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isBot, turnsPlayed);
    }

    public void addTurnPlayed() {
        turnsPlayed++;
    }
}
