package xyz.upperlevel.hgame.network

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.DefaultHttpHeaders
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler
import java.net.URI
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class WebSocketClient(uri: String) {
    private val uri: URI = URI.create(uri)
    lateinit var channel: Channel

    fun connectAndDoHandshake() {
        val b = Bootstrap()
        if (uri.scheme != "ws") {
            throw IllegalArgumentException("Unsupported protocol: ${uri.scheme}")
        }

        val lock = ReentrantLock()
        val handshake = lock.newCondition()

        b.group(group)
                .channel(NioSocketChannel::class.java)
                .handler(object : ChannelInitializer<SocketChannel>() {
                    public override fun initChannel(channel: SocketChannel) {
                        val pipeline = channel.pipeline()
                        pipeline.addLast(HttpClientCodec())
                                .addLast(HttpObjectAggregator(65536))
                                .addLast(WebSocketClientCompressionHandler.INSTANCE)
                                .addLast(WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, DefaultHttpHeaders()), true))
                                .addLast(object : ChannelInboundHandlerAdapter() {
                                    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
                                        if (evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE) {
                                            pipeline.remove(this)
                                            lock.withLock { handshake.signal() }
                                        }
                                    }
                                })
                    }
                })

        channel = b.connect(uri.host, uri.port).sync().channel()
        lock.withLock { handshake.await() }
    }

    fun send(any: Any) {
        channel.writeAndFlush(any)
    }

    fun close() {
        channel.writeAndFlush(CloseWebSocketFrame())
        channel.closeFuture().sync()
    }

    companion object {
        private val group = NioEventLoopGroup()
    }
}