package exploration

import org.bukkit.{Bukkit, Location}

/**
  * Created by Kyle on 3/27/2016.
  */
class Exploration extends Runnable{
  var OOB: Boolean = false

  def run {
    do {
      {
        try {
          Thread.sleep(10000L)
        }
        catch {
          case ex: InterruptedException => {
            ex.printStackTrace
          }
        }
        import scala.collection.JavaConversions._
        for (p <- Bukkit.getServer.getOnlinePlayers) {
          if (!p.isOp) {
            val location: Location = p.getLocation
            val x: Double = location.getX
            val z: Double = location.getZ
            var newX: Int = x.toInt
            var newZ: Int = z.toInt
            if (location.getWorld.getName.equals("Hyrule")) {
              if ((x > 510.0D) || (x < -880.0D) || (z > 990.0D) || (z < -400.0D)) {
                this.OOB = true
                if (x > 510.0D) {
                  newX = 510
                }
                else if (x < -880.0D) {
                  newX = 64656
                }
                if (z > 990.0D) {
                  newZ = 990
                }
                else if (z < -400.0D) {
                  newZ = 65136
                }
              }
            }
            else if ((x > 15000.0D) || (x < -15000.0D) || (z > 15000.0D) || (z < -15000.0D)) {
              this.OOB = true
              if (x > 15000.0D) {
                newX = 15000
              }
              else if (x < -15000.0D) {
                newX = 50536
              }
              if (z < -15000.0D) {
                newZ = 50536
              }
              else if (z > 15000.0D) {
                newZ = 15000
              }
            }
            if (this.OOB) {
              var e: Nothing = null
              if (p.isInsideVehicle) {
                e = p.getVehicle
              }
              p.leaveVehicle
              val world: Nothing = p.getWorld
              val newY: Int = world.getHighestBlockYAt(newX, newZ)
              p.teleport(new Nothing(world, newX, newY, newZ, location.getYaw, location.getPitch))
              if (e != null) {
                e.teleport(p)
              }
              p.sendMessage(org.bukkit.ChatColor.RED + "You've reached the edge of the explorable world!")
              this.OOB = false
            }
          }
        }
      }
    } while (KSAdmin.threadVar)
  }
}
