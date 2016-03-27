package bonemeal

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.TreeType
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class Bonemeal {
  @SuppressWarnings(Array("deprecation"))
  @EventHandler(ignoreCancelled = true) def onInteract(event: PlayerInteractEvent) {
    if (event.getAction eq Action.RIGHT_CLICK_BLOCK) {
      val item = event.getItem
      var block = event.getClickedBlock
      if (block == null || !isBonemeal(item)) {
        return
      }
      block.getType match {
        case Material.CARROT =>
        case Material.CROPS =>
        case Material.MELON_STEM =>
        case Material.NETHER_WARTS =>
        case Material.POTATO =>
        case Material.PUMPKIN_STEM =>
          block.setData(7.toByte)
        case Material.SAPLING =>
          var l: Location = block.getLocation
          var bigtree: Boolean = false
          var treeType: TreeType = null
          val data: Byte = block.getData
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
          val grew: Boolean = l.getWorld.generateTree(l, treeType)
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
    if ((block.getRelative(BlockFace.NORTH) != null) && (block.getRelative(BlockFace.NORTH).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.NORTH).getData eq data) && (block.getRelative(BlockFace.EAST) != null) && (block.getRelative(BlockFace.EAST).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.EAST).getData eq data) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST) != null) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getData eq data)) {
      return block.getRelative(BlockFace.NORTH)
    }
    if ((block.getRelative(BlockFace.NORTH) != null) && (block.getRelative(BlockFace.NORTH).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.NORTH).getData eq data) && (block.getRelative(BlockFace.WEST) != null) && (block.getRelative(BlockFace.WEST).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.WEST).getData eq data) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST) != null) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getData eq data)) {
      return block.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST)
    }
    if ((block.getRelative(BlockFace.SOUTH) != null) && (block.getRelative(BlockFace.SOUTH).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.SOUTH).getData eq data) && (block.getRelative(BlockFace.WEST) != null) && (block.getRelative(BlockFace.WEST).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.WEST).getData eq data) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST) != null) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getData eq data)) {
      return block.getRelative(BlockFace.WEST)
    }
    if ((block.getRelative(BlockFace.SOUTH) != null) && (block.getRelative(BlockFace.SOUTH).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.SOUTH).getData eq data) && (block.getRelative(BlockFace.EAST) != null) && (block.getRelative(BlockFace.EAST).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.EAST).getData eq data) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST) != null) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getType.equals(Material.SAPLING)) && (block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getData eq data)) {
      return block
    }
    null
  }

  private def isBonemeal(item: ItemStack): Boolean = {
    (item != null) && (item.getType eq Material.INK_SACK) && (item.getDurability eq 15)
  }
}
