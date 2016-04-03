package horse_teleport

import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.entity.{EntityType, Horse}

class HorseTeleport extends Listener{
  @EventHandler(ignoreCancelled = true)
  def onHorseTP(event: PlayerCommandPreprocessEvent) {
    val input = event.getMessage
    if (input.startsWith("/teleport") || input.startsWith("/tp") || input.startsWith("/warp") || input.startsWith("/home") || input.startsWith("/spawn")) {
      val p = event.getPlayer
      if (p.isInsideVehicle && (p.getVehicle.getType eq EntityType.HORSE)) {
        val h: Horse = p.getVehicle.asInstanceOf[Horse]
        if (h.isTamed) {
          p.leaveVehicle
          val world = p.getWorld
          p.performCommand(input.substring(1))
          if (world eq p.getWorld) {
            h.teleport(p)
          }
          event.setCancelled(true)
          return
        }
      }
    }
  }
}
