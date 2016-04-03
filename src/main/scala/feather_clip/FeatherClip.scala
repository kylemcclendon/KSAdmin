package feather_clip

import org.bukkit.{Material, ChatColor, Sound, EntityEffect, GameMode}
import org.bukkit.entity.{Chicken, Entity, EntityType, Player}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.{EquipmentSlot, ItemStack}

class FeatherClip extends Listener {
  @EventHandler(ignoreCancelled = true)
  def ClipWing(event: PlayerInteractAtEntityEvent) {
    val player: Player = event.getPlayer
    if (player.hasPermission("permissions.restrict.nointeract")) {
      event.setCancelled(true)
      return
    }
    val e = event.getRightClicked
    event.getHand match{
      case EquipmentSlot.HAND =>
        clipWing(player, e, "Main")
      case EquipmentSlot.OFF_HAND =>
        clipWing(player, e, "Off")
      case _ =>
        event.getPlayer.sendMessage(ChatColor.DARK_PURPLE + "How the hell did you do that without using your hands?! O_o")
    }
  }

  private def clipWing(player: Player, entity: Entity, hand: String) = {
    val item =if (hand == "Main") player.getInventory.getItemInMainHand else player.getInventory.getItemInOffHand
    if ((item.getType eq Material.SHEARS) && (entity.getType eq EntityType.CHICKEN)) {
      val eloc = entity.getLocation
      val c: Chicken = entity.asInstanceOf[Chicken]
      val i: ItemStack = new ItemStack(Material.FEATHER, 1)
      eloc.getWorld.dropItemNaturally(eloc, i)

      val modifiedItem = setItemDurability(item, player.getGameMode)
      if(hand == "Main") player.getInventory.setItemInMainHand(modifiedItem) else player.getInventory.setItemInOffHand(modifiedItem)
      if(modifiedItem == null) player.getWorld.playSound(player.getLocation, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)

      if (c.getHealth > 0.0D) {
        c.setHealth(c.getHealth - 1.0D)
        c.playEffect(EntityEffect.HURT)
        if (c.getAge >= 0) {
          player.getWorld.playSound(eloc, Sound.ENTITY_CHICKEN_HURT, 10.0F, 1.0F)
        }
        else {
          player.getWorld.playSound(eloc, Sound.ENTITY_CHICKEN_HURT, 10.0F, 2.0F)
        }
        if (c.getHealth <= 0.0D) {
          c.damage(200.0D)
          eloc.getWorld.dropItemNaturally(eloc, new ItemStack(Material.RAW_CHICKEN, 1))
        }
      }
    }
  }

  private def setItemDurability(item: ItemStack, gameMode: GameMode): ItemStack = {
    if (gameMode == GameMode.CREATIVE) {
      item
    }
    else {
      if (item.getDurability >= 238) {
        null
      }
      else{
        val newItemStack = item
        newItemStack.setDurability((newItemStack.getDurability + 1).toShort)
        newItemStack
      }
    }
  }
}
