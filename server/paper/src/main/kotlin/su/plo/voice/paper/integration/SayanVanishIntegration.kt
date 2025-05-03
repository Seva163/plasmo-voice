package su.plo.voice.paper.integration

import org.sayandev.sayanvanish.bukkit.api.event.BukkitUserVanishEvent;
import org.sayandev.sayanvanish.bukkit.api.event.BukkitUserUnVanishEvent;
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.proto.packets.tcp.clientbound.PlayerDisconnectPacket

class SayanVanishIntegration(
    private val voiceServer: PlasmoVoiceServer,
) : Listener {

    @EventHandler
    fun onPlayerHide(event: BukkitUserVanishEvent) {
        val player = voiceServer.playerManager.getPlayerById(event.getUser().getUniqueID(), true)
        if (!player.hasVoiceChat()) return

        voiceServer.tcpPacketManager.broadcast(
            PlayerDisconnectPacket(player.instance.uuid),
        ) { other ->
            other.instance.uuid != player.instance.uuid && !other.instance.canSee(player.instance)
        }
    }

    @EventHandler
    fun onPlayerShow(event: BukkitUserUnVanishEvent) {
        val player = voiceServer.playerManager.getPlayerByInstance(event.getUser().getUniqueID(), true)
        if (!player.hasVoiceChat()) return

        voiceServer.tcpPacketManager.broadcastPlayerInfoUpdate(player)
    }
}
