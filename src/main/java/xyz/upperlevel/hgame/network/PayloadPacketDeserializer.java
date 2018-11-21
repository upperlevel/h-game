package xyz.upperlevel.hgame.network;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class PayloadPacketDeserializer extends JsonDeserializer<PayloadPacket> {
    private final Protocol protocol;

    public PayloadPacketDeserializer(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public PayloadPacket deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
        ObjectCodec oc = p.getCodec();
        var tree = oc.readTree(p);
        var rawId = (JsonNode) tree.get("id");
        int id = rawId.asInt();

        var clazz = protocol.fromId(id)
                .orElseThrow(() -> new IllegalStateException("Illegal id found: " + id));
        var payload = tree.get("data")
                .traverse(oc)
                .readValueAs(clazz);

        return new PayloadPacket(id, payload);
    }
}
