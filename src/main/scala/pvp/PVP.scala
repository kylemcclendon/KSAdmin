package pvp

import org.bukkit.Material
import org.bukkit.entity.{Player, Snowball}
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.{PlayerItemConsumeEvent, PlayerTeleportEvent}

class PVP extends Listener {
  @EventHandler(ignoreCancelled = true)
  private def onSnowballHit(event: EntityDamageByEntityEvent) {
    if (event.getDamager.isInstanceOf[Snowball]) {
      val p: Player = event.getEntity.asInstanceOf[Player]
      p.setHealth(Math.min(0, p.getHealth - 1.0D))
    }
  }

  @EventHandler(ignoreCancelled = true)
  private def onEat(event: PlayerItemConsumeEvent) {
    val mat: Material = event.getItem.getType
    if ((mat.equals(Material.ROTTEN_FLESH)) || (mat.equals(Material.SPIDER_EYE)) || (mat.equals(Material.POTION))) {
      return
    }
    val min: Double = Math.min(20.0D, event.getPlayer.getHealth + 1.0D)
    event.getPlayer.setHealth(min)
  }

  @EventHandler(ignoreCancelled = true)
  private def preventTele(event: PlayerTeleportEvent) {
    if ((event.getTo.getWorld.getName.equals("PVP")) && (event.getCause.equals(PlayerTeleportEvent.TeleportCause.COMMAND)) && (!event.getPlayer.isOp)) {
      event.setCancelled(true)
      return
    }
  }
}
