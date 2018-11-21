package xyz.upperlevel.hgame.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Optional;
import java.util.OptionalInt;

public class Protocol {
    private BiMap<Integer, Class<? extends Packet>> handle = HashBiMap.create();

    public void add(Class<? extends Packet> clazz) {
        var ann = clazz.getAnnotation(ProtocolId.class);
        if (ann == null) {
            throw new IllegalArgumentException("Cannot find @ProtocolId for class: " + clazz.getName());
        }

        var previous = handle.putIfAbsent(ann.value(), clazz);
        if (previous != null) {
            throw new IllegalStateException("Id " + ann.value() + " used for both " + previous.getName() + " and " + clazz.getName());
        }
    }

    public Optional<Class<? extends Packet>> fromId(int id) {
        return Optional.ofNullable(handle.get(id));
    }

    public OptionalInt fromClass(Class<? extends Packet> clazz) {
        int i = handle.inverse().getOrDefault(clazz, -1);
        if (i == -1) return OptionalInt.empty();
        return OptionalInt.of(i);
    }
}
