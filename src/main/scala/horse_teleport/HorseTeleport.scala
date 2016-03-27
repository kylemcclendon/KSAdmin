package horse_teleport

/**
  * Created by Kyle on 3/27/2016.
  */
class HorseTeleport {
  @org.bukkit.event.EventHandler(ignoreCancelled = true) def onHorseTP(event: Nothing) {
    val input: String = event.getMessage
    if ((input.startsWith("/teleport")) || (input.startsWith("/tp")) || (input.startsWith("/warp")) || (input.startsWith("/home")) || (input.startsWith("/spawn"))) {
      val p: Nothing = event.getPlayer
      if ((p.isInsideVehicle) && (p.getVehicle.getType eq EntityType.HORSE)) {
        val h: Nothing = p.getVehicle.asInstanceOf[Nothing]
        if (h.isTamed) {
          p.leaveVehicle
          val message: String = event.getMessage.substring(1)
          val w: Nothing = p.getWorld
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
