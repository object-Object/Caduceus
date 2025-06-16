package gay.`object`.caduceus.networking

import dev.architectury.networking.NetworkChannel
import gay.`object`.caduceus.Caduceus
import gay.`object`.caduceus.networking.msg.CaduceusMessageCompanion

object CaduceusNetworking {
    val CHANNEL: NetworkChannel = NetworkChannel.create(Caduceus.id("networking_channel"))

    fun init() {
        for (subclass in CaduceusMessageCompanion::class.sealedSubclasses) {
            subclass.objectInstance?.register(CHANNEL)
        }
    }
}
