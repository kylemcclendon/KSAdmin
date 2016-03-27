package flower

/**
  * Created by Kyle on 3/27/2016.
  */
class Flower {
  public Boolean onCommand(sender: Nothing, cmd: Nothing, label: String, args: Array[String]) {
    if ((sender.isInstanceOf[Nothing])) {
      val player: Nothing = sender.asInstanceOf[Nothing]
      if (cmd.getName.equalsIgnoreCase("flower")) {
        if (!player.hasPermission("kadmin.flower")) {
          player.sendMessage(ChatColor.RED + "You do not have permission to do this!")
          return true
        }
        if (args.length != 1) {
          player.sendMessage(ChatColor.RED + "Usage: /flower <radius>")
          return true
        }
        val playerLoc: Nothing = player.getLocation
        val pX: Double = playerLoc.getX
        val pY: Double = playerLoc.getY
        val pZ: Double = playerLoc.getZ
        var radius: Int = 0
        try {
          radius = args(0).toInt
        }
        catch {
          case e: NumberFormatException => {
            player.sendMessage(ChatColor.RED + "Usage: /flower <radius>")
            return true
          }
        }
        var x: Int = -radius
        while (x <= radius) {
          {
            var y: Int = -radius
            while (y <= radius) {
              {
                var z: Int = -radius
                while (z <= radius) {
                  {
                    val b: Nothing = player.getWorld.getBlockAt(pX.toInt + x, pY.toInt + y, pZ.toInt + z)
                    if ((b.getType eq Material.GRASS) && (b.getRelative(BlockFace.UP).getType eq Material.AIR) && (b.getRelative(BlockFace.UP).getLightLevel > 7)) {
                      val c: Nothing = b.getRelative(BlockFace.UP)
                      val r: Int = (75.0D * Math.random).toInt
                      if (r == 1) {
                        c.setType(Material.DOUBLE_PLANT)
                        val r2: Byte = (5.0D * Math.random).toInt.toByte
                        c.setData(r2)
                      }
                      else if ((r == 7) || (r == 15)) {
                        val r2: Byte = (8.0D * Math.random).toInt.toByte
                        c.setType(Material.RED_ROSE)
                        c.setData(r2)
                      }
                      else if (r == 13) {
                        val yellow: Int = (1.0D * Math.random).toInt
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
                  ({
                    z += 1; z - 1
                  })
                }
              }
              ({
                y += 1; y - 1
              })
            }
          }
          ({
            x += 1; x - 1
          })
        }
        sender.sendMessage(ChatColor.AQUA + "Flowered!")
      }
    }
    else {
      sender.sendMessage(ChatColor.RED + "Command can only be used by a player!")
    }
    return true
  }
}
