//package timber
//
///**
//  * Created by Kyle on 3/27/2016.
//  */
//class Timber {
//  @EventHandler(ignoreCancelled = true) def goldToolMining(event: Nothing) {
//    val p: Nothing = event.getPlayer
//    val b: Nothing = event.getBlock
//    val item: Nothing = p.getItemInHand
//    if (item.getType.equals(Material.GOLD_PICKAXE)) {
//      if (b.getType.equals(Material.IRON_ORE)) {
//        import scala.collection.JavaConversions._
//        for (i <- b.getDrops) {
//          if (i.getType.equals(Material.IRON_ORE)) {
//            b.getWorld.dropItemNaturally(b.getLocation, new Nothing(Material.IRON_INGOT, 1))
//          }
//          else {
//            b.getWorld.dropItemNaturally(b.getLocation, i)
//          }
//        }
//        afterMine(b, item, p)
//        event.setCancelled(true)
//        return
//      }
//      if (b.getType.equals(Material.GOLD_ORE)) {
//        import scala.collection.JavaConversions._
//        for (i <- b.getDrops) {
//          if (i.getType.equals(Material.GOLD_ORE)) {
//            b.getWorld.dropItemNaturally(b.getLocation, new Nothing(Material.GOLD_INGOT, 1))
//          }
//          else {
//            b.getWorld.dropItemNaturally(b.getLocation, i)
//          }
//        }
//        afterMine(b, item, p)
//        event.setCancelled(true)
//        return
//      }
//      if (b.getType.equals(Material.STONE)) {
//        import scala.collection.JavaConversions._
//        for (i <- b.getDrops) {
//          if (i.getType.equals(Material.COBBLESTONE)) {
//            b.getWorld.dropItemNaturally(b.getLocation, new Nothing(Material.STONE, 1))
//          }
//          else {
//            b.getWorld.dropItemNaturally(b.getLocation, i)
//          }
//        }
//        afterMine(b, item, p)
//        event.setCancelled(true)
//        return
//      }
//    }
//    if (item.getType.equals(Material.GOLD_SPADE)) {
//      if (b.getType.equals(Material.GRAVEL)) {
//        import scala.collection.JavaConversions._
//        for (i <- b.getDrops) {
//          if (i.getType.equals(Material.GRAVEL)) {
//            b.getWorld.dropItemNaturally(b.getLocation, new Nothing(Material.FLINT, 1))
//          }
//          else {
//            b.getWorld.dropItemNaturally(b.getLocation, i)
//          }
//        }
//      }
//      afterMine(b, item, p)
//      event.setCancelled(true)
//      return
//    }
//    if ((item.getType.equals(Material.GOLD_AXE)) && ((b.getType.equals(Material.LOG)) || (b.getType.equals(Material.LOG_2)))) {
//      val treeBlocks: util.List[Nothing] = new util.ArrayList[Nothing]
//      var logs: Int = 0
//      if (treeWalk(treeBlocks, b, 2147483646)) {
//        import scala.collection.JavaConversions._
//        for (bl <- treeBlocks) {
//          if ((bl.getType.equals(Material.LOG)) || (bl.getType.equals(Material.LOG_2))) {
//            logs += 1
//          }
//          if (bl != null) {
//            bl.breakNaturally
//          }
//        }
//      }
//      if (p.getGameMode ne GameMode.CREATIVE) {
//        item.setDurability((item.getDurability + (logs / 20 + 1)).asInstanceOf[Short])
//        if (item.getDurability >= 33) {
//          p.setItemInHand(null)
//        }
//      }
//    }
//  }
//
//  def treeWalk(blocks: util.List[Nothing], base: Nothing, max: Int): Boolean = {
//    var count: Int = 0
//    val radius: Int = 5
//    var imatree: Boolean = false
//    val myQueue: util.Queue[Nothing] = new util.LinkedList[Nothing]
//    myQueue.add(base)
//    val x: Int = base.getX
//    val z: Int = base.getZ
//    while ((!myQueue.isEmpty) && (count < max)) {
//      {
//        count += 1
//        base = myQueue.poll.asInstanceOf[Nothing]
//        blocks.add(base)
//        if ((!imatree) && ((base.getType.equals(Material.LEAVES)) || (base.getType.equals(Material.LEAVES_2)))) {
//          imatree = true
//        }
//        if ((Math.abs(base.getX - x) <= radius) && (Math.abs(base.getZ - z) <= radius)) {
//          if ((isTreePiece(base, BlockFace.UP)) && (!blocks.contains(base.getRelative(BlockFace.UP))) && (!myQueue.contains(base.getRelative(BlockFace.UP)))) {
//            myQueue.add(base.getRelative(BlockFace.UP))
//          }
//          if ((Math.abs(base.getX - x) <= radius) && (Math.abs(base.getZ - z) <= radius)) {
//            if ((isTreePiece(base, BlockFace.NORTH)) && (!blocks.contains(base.getRelative(BlockFace.NORTH))) && (!myQueue.contains(base.getRelative(BlockFace.NORTH)))) {
//              myQueue.add(base.getRelative(BlockFace.NORTH))
//            }
//            if ((Math.abs(base.getX - x) <= radius) && (Math.abs(base.getZ - z) <= radius)) {
//              if ((isTreePiece(base, BlockFace.SOUTH)) && (!blocks.contains(base.getRelative(BlockFace.SOUTH))) && (!myQueue.contains(base.getRelative(BlockFace.SOUTH)))) {
//                myQueue.add(base.getRelative(BlockFace.SOUTH))
//              }
//              if ((Math.abs(base.getX - x) <= radius) && (Math.abs(base.getZ - z) <= radius)) {
//                if ((isTreePiece(base, BlockFace.EAST)) && (!blocks.contains(base.getRelative(BlockFace.EAST))) && (!myQueue.contains(base.getRelative(BlockFace.EAST)))) {
//                  myQueue.add(base.getRelative(BlockFace.EAST))
//                }
//                if ((Math.abs(base.getX - x) <= radius) && (Math.abs(base.getZ - z) <= radius)) {
//                  if ((isTreePiece(base, BlockFace.WEST)) && (!blocks.contains(base.getRelative(BlockFace.WEST))) && (!myQueue.contains(base.getRelative(BlockFace.WEST)))) {
//                    myQueue.add(base.getRelative(BlockFace.WEST))
//                  }
//                  if ((Math.abs(base.getX - x) <= radius) && (Math.abs(base.getZ - z) <= radius)) {
//                    if ((isTreePiece(base, BlockFace.DOWN)) && (!blocks.contains(base.getRelative(BlockFace.DOWN))) && (!myQueue.contains(base.getRelative(BlockFace.DOWN)))) myQueue.add(base.getRelative(BlockFace.DOWN))
//                  }
//                }
//              }
//            }
//          }
//        }
//      }
//    }
//    return imatree
//  }
//
//  private def isTreePiece(base: Nothing, f: Nothing): Boolean = {
//    if ((base.getRelative(f) != null) && ((base.getRelative(f).getType.equals(Material.LOG)) || (base.getRelative(f).getType.equals(Material.LOG_2)) || (base.getRelative(f).getType.equals(Material.LEAVES)) || (base.getRelative(f).getType.equals(Material.LEAVES_2)))) {
//      return true
//    }
//    return false
//  }
//
//  @EventHandler(ignoreCancelled = true) def hoeLand(event: Nothing) {
//    if ((event.getPlayer.getItemInHand != null) && (event.getPlayer.getItemInHand.getType.equals(Material.GOLD_HOE)) && (event.getClickedBlock != null) && ((event.getClickedBlock.getType.equals(Material.DIRT)) || (event.getClickedBlock.getType.equals(Material.GRASS)) || (event.getClickedBlock.getType.equals(Material.MYCEL))) && (event.getAction.equals(Action.RIGHT_CLICK_BLOCK))) {
//      val middle: Nothing = event.getClickedBlock.getLocation
//      var x: Int = -1
//      while (x < 2) {
//        {
//          var z: Int = -1
//          while (z < 2) {
//            {
//              val newblock: Nothing = new Nothing(middle.getWorld, middle.getX + x, middle.getY, middle.getZ + z)
//              if ((newblock.getBlock.getType.equals(Material.DIRT)) || (newblock.getBlock.getType.equals(Material.GRASS)) || (newblock.getBlock.getType.equals(Material.MYCEL))) {
//                newblock.getBlock.setType(Material.SOIL)
//              }
//            }
//            ({
//              z += 1; z - 1
//            })
//          }
//        }
//        ({
//          x += 1; x - 1
//        })
//      }
//      middle.getWorld.playSound(middle, Sound.STEP_GRAVEL, 1.0F, 0.7F)
//      if (event.getPlayer.getGameMode ne GameMode.CREATIVE) {
//        event.getItem.setDurability((event.getItem.getDurability + 1).asInstanceOf[Short])
//        if (event.getItem.getDurability >= 33) {
//          event.getPlayer.getWorld.playSound(event.getPlayer.getLocation, Sound.ITEM_BREAK, 1.0F, 1.0F)
//          event.getPlayer.setItemInHand(null)
//        }
//      }
//      event.setCancelled(true)
//      return
//    }
//  }
//
//  private def afterMine(block: Nothing, i: Nothing, p: Nothing) {
//    block.getDrops.clear
//    block.setType(Material.AIR)
//    if (p.getGameMode ne GameMode.CREATIVE) {
//      i.setDurability((i.getDurability + 1).asInstanceOf[Short])
//      if (i.getDurability >= 33) {
//        p.getWorld.playSound(p.getLocation, Sound.ITEM_BREAK, 1.0F, 1.0F)
//        p.setItemInHand(null)
//      }
//    }
//  }
//}
