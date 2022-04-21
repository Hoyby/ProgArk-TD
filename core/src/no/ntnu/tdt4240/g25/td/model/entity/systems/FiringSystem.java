package no.ntnu.tdt4240.g25.td.model.entity.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.g25.td.model.entity.components.HasTargetComponent;
import no.ntnu.tdt4240.g25.td.model.entity.components.PositionComponent;
import no.ntnu.tdt4240.g25.td.model.entity.components.StateComponent;
import no.ntnu.tdt4240.g25.td.model.entity.components.TowerComponent;
import no.ntnu.tdt4240.g25.td.model.entity.factories.ProjectileFactory;

@All({TowerComponent.class, StateComponent.class, HasTargetComponent.class, PositionComponent.class})
public class FiringSystem extends IteratingSystem {

    public static int PROJECTILE_SPEED = 750;

    @Wire
    ProjectileFactory projectileFactory;
    ComponentMapper<TowerComponent> mTower;
    ComponentMapper<StateComponent> mState;
    ComponentMapper<HasTargetComponent> mTarget;
    ComponentMapper<PositionComponent> mPosition;

    @Override
    protected void process(int entityId) {
        HasTargetComponent target = mTarget.get(entityId);
        TowerComponent tower = mTower.get(entityId);

        tower.cooldown -= world.getDelta();
        if (tower.cooldown > 0 || !target.canShoot) {
            return; // exit here if the tower cannot fire, or if it is on cooldown
        }
        StateComponent state = mState.get(entityId);
        PositionComponent position = mPosition.get(entityId);
        // create the projectile and set the position to the tower position, velocity towards the target
        // and offset the position by the towers height for it to spawn roughly at the tip of the turret
        Vector2 startPosition = position.position.cpy();
        Vector2 enemyPosition = mPosition.get(target.targetId).position;
        Vector2 velocity = enemyPosition.cpy().sub(startPosition).setLength(PROJECTILE_SPEED);
        Vector2 offset = velocity.cpy().setLength(50);
        startPosition.add(offset);
        projectileFactory.create(startPosition.x, startPosition.y, velocity.x, velocity.y, tower.damage, tower.splashRadius);
        state.set(StateComponent.STATE_ATTACKING, false);
        tower.cooldown = 1/tower.fireRate;
    }
}
