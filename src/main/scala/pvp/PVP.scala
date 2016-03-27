package pvp

/**
  * Created by Kyle on 3/27/2016.
  */
class PVP {
  @EventHandler(ignoreCancelled = true) private def onSnowballHit(event: Nothing) {
    if (event.getDamager.isInstanceOf[Nothing]) {
      val p: Nothing = event.getEntity.asInstanceOf[Nothing]
      p.setHealth(Math.min(0, p.getHealth - 1.0D))
    }
  }

  @EventHandler(ignoreCancelled = true) private def onEat(event: Nothing) {
    val mat: Nothing = event.getItem.getType
    if ((mat.equals(Material.ROTTEN_FLESH)) || (mat.equals(Material.SPIDER_EYE)) || (mat.equals(Material.POTION))) {
      return
    }
    val min: Double = Math.min(20.0D, event.getPlayer.getHealth + 1.0D)
    event.getPlayer.setHealth(min)
  }

  @EventHandler(ignoreCancelled = true) private def preventTele(event: Nothing) {
    if ((event.getTo.getWorld.getName.equals("PVP")) && (event.getCause.equals(PlayerTeleportEvent.TeleportCause.COMMAND)) && (!event.getPlayer.isOp)) {
      event.setCancelled(true)
      return
    }
  }
}
