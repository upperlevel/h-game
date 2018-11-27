package xyz.upperlevel.hgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import lombok.Getter;
import xyz.upperlevel.hgame.event.EventListener;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.network.NetSide;
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent;
import xyz.upperlevel.hgame.world.character.Actor;
import xyz.upperlevel.hgame.world.character.impl.Santy;
import xyz.upperlevel.hgame.world.entity.EntityRegistry;

import java.util.stream.Stream;

import static xyz.upperlevel.hgame.GdxUtil.runSync;

public class World {
    public static final float ACTOR_MOVE_SPEED = 0.05f;
    public static final float ACTOR_JUMP_SPEED = 2f;

    @Getter
    public float height;

    @Getter
    public float gravity;

    @Getter
    public float groundHeight;

    private EntityRegistry entityRegistry = new EntityRegistry();

    @Getter
    private Actor player;

    private boolean isMaster;

    public World() {
        height = 5.0f;
        gravity = 9.8f;
        groundHeight = 1.0f;
    }

    public void onGameStart() {
        int x = 20 / 4;
        if (isMaster) x += 20 / 2;
        entityRegistry.spawn(Santy.class, x, groundHeight, isMaster, p ->  player = p);
    }

    public Stream<Actor> getEntities() {
        return entityRegistry.getEntities();
    }

    public void update(Endpoint endpoint) { // TODO endpoint here?
        if (!isReady()) return;
        // Inputs
        Input input = Gdx.input;
        player.getInput()
                .getActions()
                .stream()
                .filter(a -> a.getTrigger().check(input))
                .forEach(action -> action.trigger(endpoint));

        // Updates
        entityRegistry.getEntities().forEach(e -> e.update(this));
    }

    public void initEndpoint(Endpoint endpoint) {
        isMaster = endpoint.getSide() == NetSide.MASTER;

        entityRegistry.registerType(Santy.class, new Santy()::personify);
        entityRegistry.initEndpoint(endpoint);

        endpoint.getEvents().register(EventListener.listener(
                ConnectionOpenEvent.class,
                e -> runSync(this::onGameStart)
        ));
    }

    public boolean isReady() {
        // TODO: isn't there a cleaner way to do this? like waiting in another screen
        return player != null;
    }
}
