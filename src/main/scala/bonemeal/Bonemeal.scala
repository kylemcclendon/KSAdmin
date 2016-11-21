package bonemeal

import org.bukkit.{ChatColor, GameMode, Material, TreeType}
import org.bukkit.block.{Block, BlockFace}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.block.Action
import org.bukkit.event.player.{PlayerChangedWorldEvent, PlayerInteractEvent}
import org.bukkit.inventory.{EquipmentSlot, ItemStack}

class Bonemeal extends Listener{
  @EventHandler(ignoreCancelled = true) def onChangeWorld(event: PlayerChangedWorldEvent): Unit ={
    if(event.getPlayer.getWorld.getName.equals("Aiedail") || event.getPlayer.getWorld.getName.equals("aiedail")){
      event.getPlayer.setGameMode(GameMode.SURVIVAL)
    }
  }
  @SuppressWarnings(Array("deprecation"))
  @EventHandler(ignoreCancelled = true) def onInteract(event: PlayerInteractEvent) {
    if (event.getAction eq Action.RIGHT_CLICK_BLOCK) {
      var item = event.getItem
      var block = event.getClickedBlock
      if (block == null || !isBonemeal(item)) {
        return
      }
      block.getType match {
        case Material.CARROT | Material.CROPS | Material.MELON_STEM | Material.NETHER_WARTS | Material.POTATO | Material.PUMPKIN_STEM =>
          block.setData(7.toByte)
          event.getHand match {
            case EquipmentSlot.OFF_HAND =>
              event.getPlayer.getInventory.setItemInOffHand(setItemAmount(item, event.getPlayer.getGameMode))
            case EquipmentSlot.HAND =>
              event.getPlayer.getInventory.setItemInMainHand(setItemAmount(item, event.getPlayer.getGameMode))
            case _ =>
              event.getPlayer.sendMessage(ChatColor.DARK_PURPLE + "How the hell did you do that without using your hands?! O_o")
          }
        case Material.SAPLING =>
          event.getHand match {
            case EquipmentSlot.OFF_HAND =>
              event.getPlayer.getInventory.setItemInOffHand(setItemAmount(item, event.getPlayer.getGameMode))
            case EquipmentSlot.HAND =>
              event.getPlayer.getInventory.setItemInMainHand(setItemAmount(item, event.getPlayer.getGameMode))
            case _ =>
              event.getPlayer.sendMessage(ChatColor.DARK_PURPLE + "How the hell did you do that without using your hands?! O_o")
          }
          var l = block.getLocation
          var bigtree = false
          var treeType: TreeType = null
          val data = block.getData
          data match {
            case 0 =>
              treeType = TreeType.TREE
              if ((Math.random * 20.0D).toInt == 10) {
                treeType = TreeType.BIG_TREE
              }
            case 1 =>
              treeType = TreeType.REDWOOD
              if ((Math.random * 20.0D).toInt == 10) {
                treeType = TreeType.TALL_REDWOOD
              }
              val bl: Block = isBigTree(block, 1.toByte)
              if (bl != null) {
                treeType = TreeType.MEGA_REDWOOD
                block = bl
                bigtree = true
              }
            case 2 =>
              treeType = TreeType.BIRCH
              if ((Math.random * 20.0D).toInt == 10) {
                treeType = TreeType.TALL_BIRCH
              }
            case 3 =>
              treeType = TreeType.SMALL_JUNGLE
              val bl: Block = isBigTree(block, 3.toByte)
              if (bl != null) {
                treeType = TreeType.JUNGLE
                block = bl
                bigtree = true
              }
            case 4 =>
              treeType = TreeType.ACACIA
            case 5 =>
              val bl: Block = isBigTree(block, 5.toByte)
              if (bl != null) {
                treeType = TreeType.DARK_OAK
                block = bl
                bigtree = true
              }
              if (treeType == null) return
            case _ =>
              return
          }
          if (!bigtree) {
            block.setType(Material.AIR)
          }
          else {
            block.setType(Material.AIR)
            block.getRelative(BlockFace.EAST).setType(Material.AIR)
            block.getRelative(BlockFace.SOUTH).setType(Material.AIR)
            block.getRelative(BlockFace.SOUTH_EAST).setType(Material.AIR)
          }
          l = block.getLocation
          val grew = l.getWorld.generateTree(l, treeType)
          if (!grew) {
            if (bigtree) {
              block.setTypeIdAndData(6, data, false)
              block.getRelative(BlockFace.EAST).setTypeIdAndData(6, data, false)
              block.getRelative(BlockFace.SOUTH).setTypeIdAndData(6, data, false)
              block.getRelative(BlockFace.SOUTH_EAST).setTypeIdAndData(6, data, false)
            }
            else {
              block.setTypeIdAndData(6, data, false)
            }
            return
          }
        case _ =>
          return
      }
    }
  }

  @SuppressWarnings(Array("deprecation")) private def isBigTree(block: Block, data: Byte): Block = {
    if ((block.getRelative(BlockFace.NORTH) != null) && block.getRelative(BlockFace.NORTH).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.NORTH).getData == data) && (block.getRelative(BlockFace.EAST) != null) && block.getRelative(BlockFace.EAST).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.EAST).getData == data) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST) != null) && block.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getData == data)) {
      return block.getRelative(BlockFace.NORTH)
    }
    if ((block.getRelative(BlockFace.NORTH) != null) && block.getRelative(BlockFace.NORTH).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.NORTH).getData == data) && (block.getRelative(BlockFace.WEST) != null) && block.getRelative(BlockFace.WEST).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.WEST).getData == data) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST) != null) && block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getData == data)) {
      return block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST)
    }
    if ((block.getRelative(BlockFace.SOUTH) != null) && block.getRelative(BlockFace.SOUTH).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.SOUTH).getData == data) && (block.getRelative(BlockFace.WEST) != null) && block.getRelative(BlockFace.WEST).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.WEST).getData == data) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST) != null) && block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getData == data)) {
      return block.getRelative(BlockFace.WEST)
    }
    if ((block.getRelative(BlockFace.SOUTH) != null) && block.getRelative(BlockFace.SOUTH).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.SOUTH).getData == data) && (block.getRelative(BlockFace.EAST) != null) && block.getRelative(BlockFace.EAST).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.EAST).getData == data) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST) != null) && block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getType.equals(Material.SAPLING) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getData == data)) {
      return block
    }
    null
  }

  private def isBonemeal(item: ItemStack): Boolean = {
    (item != null) && (item.getType eq Material.INK_SACK) && (item.getDurability == 15)
  }

  private def setItemAmount(item: ItemStack, gameMode: GameMode): ItemStack = {
    if (gameMode == GameMode.CREATIVE) {
      item
    }
    else {
      if (item.getAmount > 1) {
        val newItemStack = item
        newItemStack.setAmount(newItemStack.getAmount - 1)
        newItemStack
      }
      else {
        null
      }
    }
  }
}
