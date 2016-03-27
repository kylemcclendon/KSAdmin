package maintenance

import java.util.logging.Logger

import net.kylemc.kadmin.Kadmin

/**
  * Created by Kyle on 3/27/2016.
  */
class Maintenance {
  private[kadmin] val log: Logger = Logger.getLogger("Minecraft")
  private val plugin: Kadmin = null

  def this(instance: Kadmin) {
    this()
    this.plugin = instance
    this.plugin.getServer.getPluginManager.registerEvents(this, this.plugin)
  }

  @org.bukkit.event.EventHandler(priority = EventPriority.LOWEST) def login(event: Nothing) {
    val p: Nothing = event.getPlayer
    if ((org.bukkit.Bukkit.hasWhitelist) && (!p.isOp)) {
      event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server Under Maintenance.\n Watch from direct.kylemc.net/map")
    }
  }
}
