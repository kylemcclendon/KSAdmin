package pvp

import org.bukkit.Material
import org.bukkit.entity.{Player, Snowball}
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.{PlayerItemConsumeEvent, PlayerTeleportEvent}

class PVP extends Listener {
  @EventHandler(ignoreCancelled = true)
  private def onEat(event: PlayerItemConsumeEvent) {
    val mat: Material = event.getItem.getType
    if(mat.equals(Material.ROTTEN_FLESH) || mat.equals(Material.SPIDER_EYE) || mat.equals(Material.POTION)) {
      return
    }
    val min: Double = Math.min(20.0D, event.getPlayer.getHealth + 1.0D)
    event.getPlayer.setHealth(min)
  }

  @EventHandler(ignoreCancelled = true)
  private def preventTele(event: PlayerTeleportEvent) {
    if (event.getTo.getWorld.getName.equals("PVP") && event.getCause.equals(PlayerTeleportEvent.TeleportCause.COMMAND) && !event.getPlayer.isOp){
      event.setCancelled(true)
      return
    }
  }
}