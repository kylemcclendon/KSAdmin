package feather_clip

/**
  * Created by Kyle on 3/27/2016.
  */
class FeatherClip {
  @EventHandler(ignoreCancelled = true) def ClipWing(event: Nothing) {
    val player: Nothing = event.getPlayer
    if (player.hasPermission("permissions.restrict.nointeract")) {
      event.setCancelled(true)
      return
    }
    val e: Nothing = event.getRightClicked
    val eloc: Nothing = e.getLocation
    if ((player.getItemInHand.getType eq Material.SHEARS) && (e.getType eq EntityType.CHICKEN)) {
      val c: Nothing = e.asInstanceOf[Nothing]
      val i: Nothing = new Nothing(Material.FEATHER, 1)
      eloc.getWorld.dropItemNaturally(eloc, i)
      if (player.getGameMode ne GameMode.CREATIVE) {
        player.getItemInHand.setDurability((player.getItemInHand.getDurability + 1).asInstanceOf[Short])
        if (player.getItemInHand.getDurability >= 238) {
          player.getWorld.playSound(player.getLocation, Sound.ITEM_BREAK, 1.0F, 1.0F)
          player.setItemInHand(null)
        }
      }
      if (c.getHealth > 0.0D) {
        c.setHealth(c.getHealth - 1.0D)
        c.playEffect(EntityEffect.HURT)
        if (c.getAge >= 0) {
          player.getWorld.playSound(eloc, Sound.CHICKEN_HURT, 10.0F, 1.0F)
        }
        else {
          player.getWorld.playSound(eloc, Sound.CHICKEN_HURT, 10.0F, 2.0F)
        }
        if (c.getHealth <= 0.0D) {
          c.damage(200.0D)
          eloc.getWorld.dropItemNaturally(eloc, new Nothing(Material.RAW_CHICKEN, 1))
        }
      }
    }
  }
}
