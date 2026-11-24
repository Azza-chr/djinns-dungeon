package tn.designpattern.djinnsdungeon.model.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pattern COMPOSITE - le "composite" : un groupe (horde) de combattants.
 * Ses membres peuvent etre des ennemis seuls OU d'autres groupes.
 * Chaque appel est transmis aux membres : le groupe delegue.
 */
public class EnemyGroup implements Combatant {

    private final String name;
    private final List<Combatant> members = new ArrayList<>();

    public EnemyGroup(String name) {
        this.name = name;
    }

    public void add(Combatant member) {
        if (member == this) {
            throw new IllegalArgumentException("Un groupe ne peut pas se contenir lui-meme");
        }
        members.add(member);
    }

    public void remove(Combatant member) {
        members.remove(member);
    }

    /** Vue en lecture seule, utile pour l'interface graphique. */
    public List<Combatant> getMembers() {
        return Collections.unmodifiableList(members);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getHp() {
        int total = 0;
        for (Combatant member : members) {
            total += member.getHp();
        }
        return total;
    }

    @Override
    public boolean isAlive() {
        for (Combatant member : members) {
            if (member.isAlive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Les degats ne peuvent pas etre negatifs");
        }
        for (Combatant member : members) {
            member.takeDamage(amount);   // le groupe delegue a chaque membre
        }
    }
}