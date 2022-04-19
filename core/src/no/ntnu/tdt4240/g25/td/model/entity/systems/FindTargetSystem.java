package no.ntnu.tdt4240.g25.td.model.entity.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.systems.IntervalIteratingSystem;
import com.artemis.utils.IntBag;

import no.ntnu.tdt4240.g25.td.model.entity.components.HasTargetComponent;
import no.ntnu.tdt4240.g25.td.model.entity.components.MobComponent;
import no.ntnu.tdt4240.g25.td.model.entity.components.TowerComponent;
import no.ntnu.tdt4240.g25.td.model.entity.components.PositionComponent;

@All(TowerComponent.class)
@Exclude(HasTargetComponent.class)
public class FindTargetSystem extends IntervalIteratingSystem {

    ComponentMapper<TowerComponent> mTower;
    ComponentMapper<PositionComponent> mPosition;
    ComponentMapper<HasTargetComponent> mTarget;
    EntitySubscription enemySubscription;

    /**
     * Creates a new IntervalEntityProcessingSystem.
     *
     * @param interval the interval in seconds between processing of entities.
     */
    public FindTargetSystem(float interval) {
        super(Aspect.all(TowerComponent.class).exclude(HasTargetComponent.class), interval);
    }


    @Override
    public void initialize() {
        super.initialize();
        enemySubscription = world.getAspectSubscriptionManager()
                .get(Aspect.all(MobComponent.class, PositionComponent.class));
    }


    @Override
    protected void process(int entityId) {
        IntBag enemies = enemySubscription.getEntities();
        TowerComponent tower = mTower.get(entityId);
        PositionComponent transform = mPosition.get(entityId);

        // Prefer to target the closest enemy, start by setting the target to null
        // and then iterate through all enemies and find the closest one
        var closest = Float.MAX_VALUE;
        var closestEnemy = -1;
        for (int i = 0; i < enemies.size(); i++) {
            int enemy = enemies.get(i);
            PositionComponent enemyTransform = mPosition.get(enemy);
            var distance = transform.get().dst(enemyTransform.get());
            if (distance > tower.range) continue;
            if (distance < closest) {
                closest = distance;
                closestEnemy = enemy;
            }
        }
        // if we found an enemy, set the target
        if (closest != Float.MAX_VALUE) {
            var target = mTarget.create(entityId);
            target.targetId = closestEnemy;

        }
    }
}
