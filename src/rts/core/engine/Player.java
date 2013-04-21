package rts.core.engine;

/**
 *
 * This class represent a simple Player.
 *
 * @author Vincent PIRAULT
 *
 */
public class Player {

    private static final int START_MAX_MONEY = 10000;

    public int packetId;

    private String pseudo;
    private int spawn;
    private int color;
    private int maxMoney;
    private int money;
    private int id;
    private int teamId;
    private int tecLevel;
    private boolean isPlayer;
    private boolean isAI;

    public Player() {
        maxMoney = START_MAX_MONEY;
    }

    public void update(Player player) {
        this.pseudo = player.pseudo;
        this.color = player.color;
        this.maxMoney = player.maxMoney;
        this.money = player.money;
        this.teamId = player.teamId;
        this.tecLevel = player.tecLevel;
    }

    public boolean addMoney(int money) {
        if (this.money + money <= maxMoney) {
            this.money += money;
            return true;
        } else {
            if (this.money < maxMoney) {
                this.money = maxMoney;
            }
        }
        return false;
    }

    public boolean removeMoney(int money) {
        if (this.money - money >= 0) {
            this.money -= money;
            return true;
        } else {
            return false;
        }
    }

    public void increaseMaxMoney() {
        maxMoney += 2500;
    }

    public void decreaseMaxMoney() {
        maxMoney -= 2500;
        if (money > maxMoney) {
            money = maxMoney;
        }
    }

    // Getters and setter

    public int getColor() {
        return color;
    }

    public int getSpawn() {
        return spawn;
    }

    public void setSpawn(int spawn) {
        this.spawn = spawn;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMaxMoney() {
        return maxMoney;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public boolean isPlayer() {
        return isPlayer;
    }

    public boolean isAI() {
        return isAI;
    }

    public void setTecLevel(int tecLevel) {
        this.tecLevel = tecLevel;
    }

    public int getTecLevel() {
        return tecLevel;
    }

    public void setPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }

    public void setAI(boolean isAI) {
        this.isAI = isAI;
    }

}
