package xyz.upperlevel.hgame.world.entity;

import xyz.upperlevel.hgame.input.TriggerInputActionPacket;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.network.NetSide;
import xyz.upperlevel.hgame.world.character.Actor;
import xyz.upperlevel.hgame.world.character.Character;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static xyz.upperlevel.hgame.GdxUtil.runSync;

public class EntityRegistry {
    // TODO: use actors instead of entities
    private List<EntityFactory<?>> factories = new ArrayList<>();
    private Map<Class<?>, Integer> factoryIdByType = new HashMap<>();
    private Queue<Consumer<Actor>> pendingSpawns = new ArrayDeque<>();

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

    public void spawn(Class<? extends Character> entityType, float x, float y, boolean left, Consumer<Actor> callback) {
        int id = factoryIdByType.getOrDefault(entityType, -1);
        if (id == -1) throw new IllegalArgumentException("Entity " + entityType + " not registered!");

        endpoint.send(new EntitySpawnPacket(id, x, y, left, false));

        if (endpoint.getSide() == NetSide.MASTER) {
            Actor actor = spawn0(id, x, y, left);
            callback.accept(actor);
        } else {
            pendingSpawns.add(callback);
        }
    }

    public void spawn(Class<? extends Character> entityType, float x, float y, boolean left) {
        spawn(entityType, x, y, left, e -> {});
    }

    protected void onNetSpawn(int typeId, float x, float y, boolean facingLeft, boolean isConfirm) {
        var entity = spawn0(typeId, x, y, facingLeft);

        if (endpoint.getSide() == NetSide.MASTER) {
            endpoint.send(new EntitySpawnPacket(typeId, x, y, facingLeft, true));
        } else if (isConfirm) {
            // We are the client and the server sent a confirmation to our request
            pendingSpawns.remove().accept(entity);
        }
    }

    public void registerType(Class<?> type, EntityFactory<? extends Actor> actorFactory) {
        factoryIdByType.put(type, factories.size());
        factories.add(actorFactory);
    }

    public void initEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;

        endpoint.getEvents().register(EntitySpawnPacket.class, packet -> {
            runSync(() ->  onNetSpawn(packet.getEntityTypeId(), packet.getX(), packet.getY(), packet.isFacingLeft(), packet.isConfirmation()));
        });
        endpoint.getEvents().register(TriggerInputActionPacket.class, packet -> {
            runSync(() ->  entities.get(packet.getActorId())
                    .getInput()
                    .onNetworkAction(packet.getActionId()));
        });
    }
}
