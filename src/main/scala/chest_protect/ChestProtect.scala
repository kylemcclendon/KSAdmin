package chest_protect

import org.bukkit.block.Chest
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.{InventoryClickEvent, InventoryType}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.{ChatColor, Material}

import scala.collection.JavaConverters._

class ChestProtect extends Listener{

  @EventHandler(ignoreCancelled = true)
  def clickChestInventory(event: InventoryClickEvent){
    if(event.getInventory.getType.equals(InventoryType.CHEST)){
      val book = event.getInventory.getItem(0)
      if(book != null && book.getType.equals(Material.WRITTEN_BOOK) && book.getItemMeta.asInstanceOf[BookMeta].getTitle.equals("Owners")){
        val im = book.getItemMeta
        val bm = im.asInstanceOf[BookMeta]

        val nameList = bm.getPages.asScala.flatMap(page => page.split('\n')).toList

        if(bm.getAuthor != event.getWhoClicked.getName && !event.getWhoClicked.isOp && !isAuthorized(nameList, event.getWhoClicked.getName)){
          event.getWhoClicked.sendMessage(ChatColor.RED + "You are not authorized to interact with this chest!")
          event.setCancelled(true)
        }

      }
    }
  }

  @EventHandler
  def destroyChest(event: BlockBreakEvent){
    if(event.getBlock.getType.equals(Material.CHEST)) {
      val c = event.getBlock.getState.asInstanceOf[Chest]
      val book = c.getBlockInventory.getItem(0)

      if (book != null && book.getType.equals(Material.WRITTEN_BOOK) && book.getItemMeta.asInstanceOf[BookMeta].getTitle.equals("Owners")) {
        val im = book.getItemMeta
        val bm = im.asInstanceOf[BookMeta]

        val nameList = bm.getPages.asScala.flatMap(page => page.split('\n')).toList

        if (bm.getAuthor != event.getPlayer.getName && !event.getPlayer.isOp && !isAuthorized(nameList, event.getPlayer.getName)) {
          event.getPlayer.sendMessage(ChatColor.RED + "You are not authorized to destroy with this chest!")
          event.setCancelled(true)
        }
      }
      else{
        //check for double chest
        val bpos1 = if(event.getBlock.getLocation().add(-1, 0, 0).getBlock.getType.equals(Material.CHEST)) -1 else 0
        val bpos2 = if(event.getBlock.getLocation().add(+1, 0, 0).getBlock.getType.equals(Material.CHEST)) 1 else 0
        val bpos3 = if(event.getBlock.getLocation().add(0, 0, -1).getBlock.getType.equals(Material.CHEST)) -1 else 0
        val bpos4 = if(event.getBlock.getLocation().add(0, 0, +1).getBlock.getType.equals(Material.CHEST)) 1 else 0

        if(bpos1+bpos2+bpos3+bpos4 != 0){
          //Found double chest
          val block = event.getBlock.getLocation.add(bpos1 + bpos2, 0, bpos3 + bpos4).getBlock

          val c = block.getState.asInstanceOf[Chest]
          val book = c.getBlockInventory.getItem(0)

          if (book != null && book.getType.equals(Material.WRITTEN_BOOK) && book.getItemMeta.asInstanceOf[BookMeta].getTitle.equals("Owners")) {
            val im = book.getItemMeta
            val bm = im.asInstanceOf[BookMeta]

            val nameList = bm.getPages.asScala.flatMap(page => page.split('\n')).toList

            if (bm.getAuthor != event.getPlayer.getName && !event.getPlayer.isOp && !isAuthorized(nameList, event.getPlayer.getName)) {
              event.getPlayer.sendMessage(ChatColor.RED + "You are not authorized to destroy with this chest!")
              event.setCancelled(true)
            }
          }
        }
      }
    }
  }

  def isAuthorized(values: List[String], playerName: String): Boolean = {
    for(value <- values){
      if(value.contains('*')){
        val valArray = value.split('*')
        val name = valArray(0).replace("§0", "")
        if(playerName.contains(name)){
          return true
        }
      }

      val name = value.replace("§0", "")

      if(playerName.equals(name)){
        return true
      }
    }
    false
  }
}
