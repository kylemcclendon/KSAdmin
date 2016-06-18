package maintenance

import java.util.logging.Logger

import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.{EventPriority, Listener}

class Maintenance extends Listener{
  val log: Logger = Logger.getLogger("Minecraft")

  @org.bukkit.event.EventHandler(priority = EventPriority.LOWEST) def login(event: PlayerLoginEvent) {
    val p = event.getPlayer
    if (Bukkit.hasWhitelist && !p.isOp) {
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server Under Maintenance.\n Watch from direct.kylemc.net/map")
    }
  }
}
