package horse_teleport

import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.entity.{EntityType, Horse}

/**
  * Created by Kyle on 3/27/2016.
  */
class HorseTeleport extends Listener{
  @org.bukkit.event.EventHandler(ignoreCancelled = true)
  def onHorseTP(event: PlayerCommandPreprocessEvent) {
    val input: String = event.getMessage
    if ((input.startsWith("/teleport")) || (input.startsWith("/tp")) || (input.startsWith("/warp")) || (input.startsWith("/home")) || (input.startsWith("/spawn"))) {
      val p: Player = event.getPlayer
      if ((p.isInsideVehicle) && (p.getVehicle.getType eq EntityType.HORSE)) {
        val h: Horse = p.getVehicle.asInstanceOf[Horse]
        if (h.isTamed) {
          p.leaveVehicle
          val message: String = event.getMessage.substring(1)
          val w: World = p.getWorld
          p.performCommand(message)
          if (w eq p.getWorld) {
            h.teleport(p)
          }
          event.setCancelled(true)
          return
        }
      }
    }
  }
}
