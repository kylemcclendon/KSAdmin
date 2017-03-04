package restart

import java.util.logging.Logger

import ksadmin.KSAdmin
import org.bukkit.plugin.Plugin
import org.bukkit.{Bukkit, ChatColor, scheduler}
import org.bukkit.scheduler.BukkitRunnable

case class Restart(instance: KSAdmin) {
  val log: Logger = Logger.getLogger("Minecraft")
  private var remaining: Int = 60
  var plugin: Plugin = instance
  initRestarts()

  @SuppressWarnings(Array("deprecation")) final protected def initRestarts() {
    Bukkit.getServer.getScheduler.scheduleSyncRepeatingTask(this.plugin, new BukkitRunnable() {
      def run() {
        Bukkit.getServer.broadcastMessage(ChatColor.RED + "Server Restarting in " + remaining + " minutes")
        remaining -= 10
      }
    }, 792000L, 12000L)
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new BukkitRunnable() {
      def run() {
        Bukkit.getServer.broadcastMessage(ChatColor.RED + "Server Restarting in 5 minutes")
      }
    }, 858000L)
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new BukkitRunnable() {
      def run() {
        Bukkit.getServer.broadcastMessage(ChatColor.RED + "Server Restarting in 1 minute")
      }
    }, 862800L)
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new scheduler.BukkitRunnable() {
      def run() {
        import collection.JavaConversions._
        for (p1 <- Bukkit.getServer.getOnlinePlayers) {
          p1.kickPlayer("Server Is Restarting")
        }
        Bukkit.getServer.shutdown()
      }
    }, 864000L)
    System.out.println("Auto-Restart schedulers prepared.")
  }
}
