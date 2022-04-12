package no.ntnu.tdt4240.g25.td.model.entity.components;

import com.artemis.Component;

import no.ntnu.tdt4240.g25.td.model.MobType;

public class MobComponent extends Component {

    public MobType type;
    public int health;
    public int maxHealth;

    public MobComponent(MobType type, int health, int maxHealth) {
        this.type = type;
        this.health = health;
        this.maxHealth = maxHealth;
    }

    public MobComponent(MobType mobType) {
        this(mobType, 100, 100);
    }

    public MobComponent() {
        this(MobType.NORMAL, 100, 100);
    }
}
