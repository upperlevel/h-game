package xyz.upperlevel.hgame.world.entity;

import xyz.upperlevel.hgame.input.TriggerInputActionPacket;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.world.character.Actor;
import xyz.upperlevel.hgame.world.character.Character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class EntityRegistry {
    // TODO: use actors instead of entities
    // TODO: fix concurrency issue (both instance an entity at the same time)
    // resolution: only the server can spawn, the client should request the server and then let it spawn
    private List<EntityFactory<?>> factories = new ArrayList<>();
    private Map<Class<?>, Integer> factoryIdByType = new HashMap<>();

    private Map<Integer, Actor> entities = new HashMap<>();
    private int nextId = 0;

    private Endpoint endpoint;

    public Stream<Actor> getEntities() {
        return entities.values().stream();
    }

    private Actor spawn0(int type, float x, float y, boolean left) {
        var factory = factories.get(type);

        var entity = factory.personify(nextId++);
        entity.x = x;
        entity.y = y;
        entity.setLeft(left);
        entities.put(entity.getId(), entity);
        return entity;
    }

    public Actor spawn(Class<? extends Character> entityType, float x, float y, boolean left) {
        int id = factoryIdByType.getOrDefault(entityType, -1);
        if (id == -1) throw new IllegalArgumentException("Entity " + entityType + " not registered!");

        endpoint.send(new EntitySpawnPacket(id, x, y, left));

        return spawn0(id, x, y, left);
    }

    public void registerType(Class<?> type, EntityFactory<? extends Actor> actorFactory) {
        factoryIdByType.put(type, factories.size());
        factories.add(actorFactory);
    }

    public void initEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;

        endpoint.getEvents().register(EntitySpawnPacket.class, packet -> {
            spawn0(packet.getEntityTypeId(), packet.getX(), packet.getY(), packet.isFacingLeft());
        });
        endpoint.getEvents().register(TriggerInputActionPacket.class, packet -> {
            entities.get(packet.getActorId())
                    .getInput()
                    .onNetworkAction(packet.getActionId());
        });
    }
}
