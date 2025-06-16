package gay.`object`.caduceus.networking.handler

import dev.architectury.networking.NetworkManager.PacketContext
import gay.`object`.caduceus.config.CaduceusConfig
import gay.`object`.caduceus.networking.msg.*

fun CaduceusMessageS2C.applyOnClient(ctx: PacketContext) = ctx.queue {
    when (this) {
        is MsgSyncConfigS2C -> {
            CaduceusConfig.onSyncConfig(serverConfig)
        }

        // add more client-side message handlers here
    }
}
