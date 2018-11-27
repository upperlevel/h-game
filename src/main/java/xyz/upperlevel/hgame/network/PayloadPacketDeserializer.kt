package xyz.upperlevel.hgame.network

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException

class PayloadPacketDeserializer(private val protocol: Protocol) : JsonDeserializer<PayloadPacket>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(p: JsonParser, ctx: DeserializationContext): PayloadPacket {
        val oc = p.codec
        val tree = oc.readTree<TreeNode>(p)
        val rawId = tree.get("id") as JsonNode
        val id = rawId.asInt()

        val clazz = protocol.fromId(id) ?: throw IllegalStateException("Illegal id found: $id")

        val payload = tree.get("data")
                .traverse(oc)
                .readValueAs(clazz)

        return PayloadPacket(id, payload)
    }
}
