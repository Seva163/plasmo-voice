package su.plo.voice.paper.integration

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.sayandev.sayanvanish.bukkit.api.event.BukkitUserUnVanishEvent
import org.sayandev.sayanvanish.bukkit.api.event.BukkitUserVanishEvent
import su.plo.voice.api.server.PlasmoVoiceServer
import su.plo.voice.proto.packets.tcp.clientbound.PlayerDisconnectPacket

class SayanVanishIntegration(
    private val voiceServer: PlasmoVoiceServer,
) : Listener {

    @EventHandler
    fun onPlayerHide(event: BukkitUserVanishEvent) {
        val bukkitPlayer = event.user.player() ?: return
        val player = voiceServer.playerManager.getPlayerByInstance(bukkitPlayer)
        if (!player.hasVoiceChat()) return

        voiceServer.tcpPacketManager.broadcast(
            PlayerDisconnectPacket(player.instance.uuid),
        ) { other ->
            other.instance.uuid != player.instance.uuid
        }
    }

    @EventHandler
    fun onPlayerShow(event: BukkitUserUnVanishEvent) {
        val bukkitPlayer = event.user.player() ?: return
        val player = voiceServer.playerManager.getPlayerByInstance(bukkitPlayer)
        if (!player.hasVoiceChat()) return

        voiceServer.tcpPacketManager.broadcastPlayerInfoUpdate(player)
    }
}
