package gay.`object`.caduceus.networking.msg

import dev.architectury.networking.NetworkChannel
import dev.architectury.networking.NetworkManager.PacketContext
import gay.`object`.caduceus.Caduceus
import gay.`object`.caduceus.networking.CaduceusNetworking
import gay.`object`.caduceus.networking.handler.applyOnClient
import gay.`object`.caduceus.networking.handler.applyOnServer
import net.fabricmc.api.EnvType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import java.util.function.Supplier

sealed interface CaduceusMessage

sealed interface CaduceusMessageC2S : CaduceusMessage {
    fun sendToServer() {
        CaduceusNetworking.CHANNEL.sendToServer(this)
    }
}

sealed interface CaduceusMessageS2C : CaduceusMessage {
    fun sendToPlayer(player: ServerPlayer) {
        CaduceusNetworking.CHANNEL.sendToPlayer(player, this)
    }

    fun sendToPlayers(players: Iterable<ServerPlayer>) {
        CaduceusNetworking.CHANNEL.sendToPlayers(players, this)
    }
}

sealed interface CaduceusMessageCompanion<T : CaduceusMessage> {
    val type: Class<T>

    fun decode(buf: FriendlyByteBuf): T

    fun T.encode(buf: FriendlyByteBuf)

    fun apply(msg: T, supplier: Supplier<PacketContext>) {
        val ctx = supplier.get()
        when (ctx.env) {
            EnvType.SERVER, null -> {
                Caduceus.LOGGER.debug("Server received packet from {}: {}", ctx.player.name.string, this)
                when (msg) {
                    is CaduceusMessageC2S -> msg.applyOnServer(ctx)
                    else -> Caduceus.LOGGER.warn("Message not handled on server: {}", msg::class)
                }
            }
            EnvType.CLIENT -> {
                Caduceus.LOGGER.debug("Client received packet: {}", this)
                when (msg) {
                    is CaduceusMessageS2C -> msg.applyOnClient(ctx)
                    else -> Caduceus.LOGGER.warn("Message not handled on client: {}", msg::class)
                }
            }
        }
    }

    fun register(channel: NetworkChannel) {
        channel.register(type, { msg, buf -> msg.encode(buf) }, ::decode, ::apply)
    }
}
