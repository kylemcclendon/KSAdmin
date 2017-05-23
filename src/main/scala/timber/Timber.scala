package timber

import org.bukkit.{GameMode, Material, Sound}
import org.bukkit.block.{Block, BlockFace}
import org.bukkit.entity.Player
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.block.{Action, BlockBreakEvent}
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.{EquipmentSlot, ItemStack}
import org.bukkit.Location
import org.bukkit.enchantments.Enchantment

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters.collectionAsScalaIterableConverter

class Timber extends Listener{
  @EventHandler(ignoreCancelled = true) def goldToolMining(event: BlockBreakEvent) {
    val player = event.getPlayer
    val block = event.getBlock
    val item = player.getInventory.getItemInMainHand
    if (item.getType.equals(Material.GOLD_PICKAXE)) {
      if (block.getType.equals(Material.IRON_ORE)) {
        for (i <- block.getDrops.asScala) {
          if (i.getType.equals(Material.IRON_ORE)) {
            block.getWorld.dropItemNaturally(block.getLocation, new ItemStack(Material.IRON_INGOT, 1))
          }
          else {
            block.getWorld.dropItemNaturally(block.getLocation, i)
          }
        }
        val durability = afterMine(block, item, player, 1)
        if (durability >= 33) {
          player.getWorld.playSound(player.getLocation, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)
          player.getInventory.setItemInMainHand(null)
        }
        player.getInventory.getItemInMainHand.setDurability(durability)
        event.setCancelled(true)
      }
      else if (block.getType.equals(Material.GOLD_ORE)) {
        for (i <- block.getDrops.asScala) {
          if (i.getType.equals(Material.GOLD_ORE)) {
            block.getWorld.dropItemNaturally(block.getLocation, new ItemStack(Material.GOLD_INGOT, 1))
          }
          else {
            block.getWorld.dropItemNaturally(block.getLocation, i)
          }
        }
        val durability = afterMine(block, item, player, 1)
        if (durability >= 33) {
          player.getWorld.playSound(player.getLocation, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)
          player.getInventory.setItemInMainHand(null)
        }
        player.getInventory.getItemInMainHand.setDurability(durability)
        event.setCancelled(true)
      }
      else if (block.getType.equals(Material.STONE)) {
        for (i <- block.getDrops.asScala) {
          if (i.getType.equals(Material.COBBLESTONE)) {
            block.getWorld.dropItemNaturally(block.getLocation, new ItemStack(Material.STONE, 1))
          }
          else {
            block.getWorld.dropItemNaturally(block.getLocation, i)
          }
        }
        val durability = afterMine(block, item, player, 1)
        if (durability >= 33) {
          player.getWorld.playSound(player.getLocation, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)
          player.getInventory.setItemInMainHand(null)
        }
        player.getInventory.getItemInMainHand.setDurability(durability)
        event.setCancelled(true)
      }
    }
    else if (item.getType.equals(Material.GOLD_SPADE)) {
      if (block.getType.equals(Material.GRAVEL)) {
        for (i <- block.getDrops.asScala) {
          if (i.getType.equals(Material.GRAVEL)) {
            block.getWorld.dropItemNaturally(block.getLocation, new ItemStack(Material.FLINT, 1))
          }
          else {
            block.getWorld.dropItemNaturally(block.getLocation, i)
          }
        }
        val durability = afterMine(block, item, player, 1)
        if (durability >= 33) {
          player.getWorld.playSound(player.getLocation, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)
          player.getInventory.setItemInMainHand(null)
        }
        player.getInventory.getItemInMainHand.setDurability(durability)
        event.setCancelled(true)
      }
    }
    else if (item.getType.equals(Material.GOLD_AXE) && (block.getType.equals(Material.LOG) || block.getType.equals(Material.LOG_2))) {
      val treeBlocks: List[Block] = treeWalk(block, 2147483646)
      if (treeBlocks.nonEmpty) {
        val logs = for (bl <- treeBlocks) yield {
          val blockType = bl.getType
          if (bl != null) {
            bl.breakNaturally
          }
          if (blockType.equals(Material.LOG) || blockType.equals(Material.LOG_2)) 1 else 0
        }

        val durability = afterMine(block, item, player, logs.sum / 20 + 1)
        if (durability >= 33) {
          player.getWorld.playSound(player.getLocation, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)
          player.getInventory.setItemInMainHand(null)
        }
        player.getInventory.getItemInMainHand.setDurability(durability)
      }
    }
  }

  def treeWalk(base: Block, max: Int): List[Block] = {
    var count = 0
    val radius = 5
    var isATree = false
    val blocks: ListBuffer[Block] = ListBuffer()
    val myQueue: collection.mutable.Queue[Block] = collection.mutable.Queue()
    myQueue.enqueue(base)
    val x = base.getX
    val z = base.getZ

    while (myQueue.nonEmpty && (count < max)) {
      count += 1
      val baseBlock = myQueue.dequeue()
      blocks += baseBlock
      if ((!isATree) && (baseBlock.getType.equals(Material.LEAVES) || baseBlock.getType.equals(Material.LEAVES_2))) {
        isATree = true
      }
      if ((Math.abs(baseBlock.getX - x) <= radius) && (Math.abs(baseBlock.getZ - z) <= radius)) {
        if (isTreePiece(baseBlock, BlockFace.UP) && (!blocks.contains(baseBlock.getRelative(BlockFace.UP))) && (!myQueue.contains(baseBlock.getRelative(BlockFace.UP)))) {
          myQueue.enqueue(baseBlock.getRelative(BlockFace.UP))
        }
        if ((Math.abs(baseBlock.getX - x) <= radius) && (Math.abs(baseBlock.getZ - z) <= radius)) {
          if (isTreePiece(baseBlock, BlockFace.NORTH) && (!blocks.contains(baseBlock.getRelative(BlockFace.NORTH))) && (!myQueue.contains(baseBlock.getRelative(BlockFace.NORTH)))) {
            myQueue.enqueue(baseBlock.getRelative(BlockFace.NORTH))
          }
          if ((Math.abs(baseBlock.getX - x) <= radius) && (Math.abs(baseBlock.getZ - z) <= radius)) {
            if (isTreePiece(baseBlock, BlockFace.SOUTH) && (!blocks.contains(baseBlock.getRelative(BlockFace.SOUTH))) && (!myQueue.contains(baseBlock.getRelative(BlockFace.SOUTH)))) {
              myQueue.enqueue(baseBlock.getRelative(BlockFace.SOUTH))
            }
            if ((Math.abs(baseBlock.getX - x) <= radius) && (Math.abs(baseBlock.getZ - z) <= radius)) {
              if (isTreePiece(baseBlock, BlockFace.EAST) && (!blocks.contains(baseBlock.getRelative(BlockFace.EAST))) && (!myQueue.contains(baseBlock.getRelative(BlockFace.EAST)))) {
                myQueue.enqueue(baseBlock.getRelative(BlockFace.EAST))
              }
              if ((Math.abs(baseBlock.getX - x) <= radius) && (Math.abs(baseBlock.getZ - z) <= radius)) {
                if (isTreePiece(baseBlock, BlockFace.WEST) && (!blocks.contains(baseBlock.getRelative(BlockFace.WEST))) && (!myQueue.contains(baseBlock.getRelative(BlockFace.WEST)))) {
                  myQueue.enqueue(baseBlock.getRelative(BlockFace.WEST))
                }
                if ((Math.abs(baseBlock.getX - x) <= radius) && (Math.abs(baseBlock.getZ - z) <= radius)) {
                  if (isTreePiece(baseBlock, BlockFace.DOWN) && (!blocks.contains(baseBlock.getRelative(BlockFace.DOWN))) && (!myQueue.contains(baseBlock.getRelative(BlockFace.DOWN)))) myQueue.enqueue(baseBlock.getRelative(BlockFace.DOWN))
                }
              }
            }
          }
        }
      }
    }
    if(isATree) blocks.toList else Nil
  }

  private def isTreePiece(base: Block, f: BlockFace): Boolean = {
    if ((base.getRelative(f) != null) && (base.getRelative(f).getType.equals(Material.LOG) || base.getRelative(f).getType.equals(Material.LOG_2) || base.getRelative(f).getType.equals(Material.LEAVES) || base.getRelative(f).getType.equals(Material.LEAVES_2))) {
      return true
    }
    false
  }

  @EventHandler(ignoreCancelled = true) def hoeLand(event: PlayerInteractEvent) {
    val hand = if(event.getHand eq EquipmentSlot.HAND) "Main" else "Off"
    val item = event.getItem
    if ((item != null) && item.getType.equals(Material.GOLD_HOE) && (event.getClickedBlock != null) && (event.getClickedBlock.getType.equals(Material.DIRT) || event.getClickedBlock.getType.equals(Material.GRASS) || event.getClickedBlock.getType.equals(Material.MYCEL)) && event.getAction.equals(Action.RIGHT_CLICK_BLOCK)) {
      val middle = event.getClickedBlock.getLocation
      (-1 until 2).foreach {
        x =>
          (-1 until 2).foreach {
            z =>
              val newblock: Location = new Location(middle.getWorld, middle.getX + x, middle.getY, middle.getZ + z)
              if (newblock.getBlock.getType.equals(Material.DIRT) || newblock.getBlock.getType.equals(Material.GRASS) || newblock.getBlock.getType.equals(Material.MYCEL)) {
                newblock.getBlock.setType(Material.SOIL)
              }
          }
      }
      middle.getWorld.playSound(middle, Sound.BLOCK_GRAVEL_STEP, 1.0F, 0.7F)
      if (event.getPlayer.getGameMode ne GameMode.CREATIVE) {
        event.getItem.setDurability((event.getItem.getDurability + 1).asInstanceOf[Short])
        if (event.getItem.getDurability >= 33) {
          event.getPlayer.getWorld.playSound(event.getPlayer.getLocation, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)
          if(hand eq "Main") event.getPlayer.getInventory.setItemInMainHand(null) else event.getPlayer.getInventory.setItemInOffHand(null)
        }
      }
      event.setCancelled(true)
      return
    }
  }

  private def afterMine(block: Block, i: ItemStack, p: Player, durabilityIncrement: Int): Short = {
    block.getDrops.clear()
    block.setType(Material.AIR)
    if (p.getGameMode ne GameMode.CREATIVE) {
      val durabilityModifier: Float = (100F/(i.getEnchantmentLevel(Enchantment.DURABILITY)+1))/100F
      (i.getDurability + Math.max(durabilityIncrement*durabilityModifier, 1)).asInstanceOf[Short]
    }
    else{
      i.getDurability
    }
  }
}
