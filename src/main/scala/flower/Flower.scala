package flower

import org.bukkit.block.BlockFace
import org.bukkit.{ChatColor, Material}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.entity.Player

class Flower extends CommandExecutor{
  def onCommand(sender: CommandSender, cmd: Command, label: String, args: Array[String]): Boolean =  {
    sender match {
      case player: Player =>
        if (cmd.getName.equalsIgnoreCase("flower")) {
          if (!player.hasPermission("kadmin.flower")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to do this!")
            return true
          }
          if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /flower <radius>")
            return true
          }
          val playerLoc = player.getLocation
          val pX = playerLoc.getX
          val pY = playerLoc.getY
          val pZ = playerLoc.getZ
          var radius = 0
          try {
            radius = args(0).toInt
          }
          catch {
            case e: NumberFormatException =>
              player.sendMessage(ChatColor.RED + "Usage: /flower <radius>")
              return true
          }
          (-radius to radius).foreach {
            x =>
              (-radius to radius).foreach {
                y =>
                  (-radius to radius).foreach {
                    z =>
                      val block = player.getWorld.getBlockAt(pX.toInt + x, pY.toInt + y, pZ.toInt + z)
                      if ((block.getType eq Material.GRASS) && (block.getRelative(BlockFace.UP).getType eq Material.AIR) && (block.getRelative(BlockFace.UP).getLightLevel > 7)) {
                        val c = block.getRelative(BlockFace.UP)
                        val r = (75.0D * Math.random).toInt
                        if (r == 1) {
                          c.setType(Material.DOUBLE_PLANT)
                          val r2 = (5.0D * Math.random).toInt.toByte
                          c.setData(r2)
                        }
                        else if ((r == 7) || (r == 15)) {
                          val r2 = (8.0D * Math.random).toInt.toByte
                          c.setType(Material.RED_ROSE)
                          c.setData(r2)
                        }
                        else if (r == 13) {
                          val yellow = (1.0D * Math.random).toInt
                          if (yellow == 0) {
                            c.setType(Material.YELLOW_FLOWER)
                          }
                          else {
                            c.setType(Material.AIR)
                          }
                        }
                        else if (r % 2 == 0) {
                          c.setType(Material.AIR)
                        }
                        else {
                          c.setType(Material.LONG_GRASS)
                          c.setData(1.toByte)
                        }
                      }
                  }
              }
          }
          sender.sendMessage(ChatColor.AQUA + "Flowered!")
        }
      case _ =>
        sender.sendMessage(ChatColor.RED + "Command can only be used by a player!")
    }
    true
  }
}
