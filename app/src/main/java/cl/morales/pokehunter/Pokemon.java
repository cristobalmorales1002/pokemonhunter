package cl.morales.pokehunter;

public class Pokemon {
    public String name;
    public int hp;
    public int attack;
    public int defense;
    public String cryUrl;

    public Pokemon(String name, int hp, int attack, int defense, String cryUrl) {
        this.name = name;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.cryUrl = cryUrl;
    }
}
