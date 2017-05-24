package webhook

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.bukkit.{ChatColor, Material}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player._
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WebhookIntegration extends Listener with CommandExecutor {
  private val player_reports_url = "https://discordapp.com/api/webhooks/287113395663142912/-DCEbdI5IY0tH-UhsiXUy28lTYqhxM4QOzNVRklDD85RDu9k0HJ-rqoh6EWwKOoaSqUU"
  private val player_events_url = "https://discordapp.com/api/webhooks/316782284814680064/lzyrvTf6BXb1J2dU9ITCGXu5fSeTc0ZLLENWoiMNN0iuRhlsIcozzuiNZCkT7IEtRx7k"
  private val httpClient = new DefaultHttpClient()

  @EventHandler(ignoreCancelled = true)
  def placeTNT(event: BlockPlaceEvent) {
    if (event.getBlock.getType.equals(Material.TNT) && event.getPlayer.getWorld.getName.equals("Temp")) {
      val location = event.getBlock.getLocation
      val message = s"{WARNING: ${event.getPlayer.getName} placed tnt at: ${location.getWorld.getName}: ${location.getX},${location.getY},${location.getZ}}"

      val f = Future {
        var httpPost = new HttpPost(player_reports_url)
        val params = new StringEntity(s"""{\"content\":\"$message\"}""")
        params.setContentType("application/json")
        httpPost.setEntity(params)

        httpClient.execute(httpPost)
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  def lavaBucket(event: PlayerBucketEmptyEvent): Unit = {
    val location = event.getPlayer.getLocation
    val message = s"{WARNING: ${event.getPlayer.getName} poured lava at: ${location.getWorld.getName}: ${location.getX},${location.getY},${location.getZ}}"

    if (event.getBucket.equals(Material.LAVA_BUCKET) && event.getPlayer.getWorld.getName.equals("Temp")) {
      val f = Future {
        var httpPost = new HttpPost(player_reports_url)
        val params = new StringEntity(s"""{\"content\":\"$message\"}""")
        params.setContentType("application/json")
        httpPost.setEntity(params)

        httpClient.execute(httpPost)
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  def onPlayerJoin(event: PlayerJoinEvent): Unit = {
    val message = s"${event.getPlayer.getName} joined in ${event.getPlayer.getWorld.getName}"
    val f = Future {
      var httpPost = new HttpPost(player_events_url)
      val params = new StringEntity(s"""{\"content\":\"$message\"}""")
      params.setContentType("application/json")
      httpPost.setEntity(params)

      httpClient.execute(httpPost)
    }
  }

  @EventHandler(ignoreCancelled = true)
  def onPlayerQuit(event: PlayerQuitEvent): Unit = {
    val message = s"${event.getPlayer.getName} quit"
    val f = Future {
      var httpPost = new HttpPost(player_events_url)
      val params = new StringEntity(s"""{\"content\":\"$message\"}""")
      params.setContentType("application/json")
      httpPost.setEntity(params)

      httpClient.execute(httpPost)
    }
  }

  @EventHandler(ignoreCancelled = true)
  def onPlayerDeath(event: PlayerDeathEvent): Unit = {
    val message = s"${event.getEntity.getName} died on ${event.getEntity.getWorld.getName}"
    val f = Future {
      var httpPost = new HttpPost(player_events_url)
      val params = new StringEntity(s"""{\"content\":\"$message\"}""")
      params.setContentType("application/json")
      httpPost.setEntity(params)

      httpClient.execute(httpPost)
    }
  }

  @EventHandler(ignoreCancelled = true)
  def onPlayerChangeWorld(event: PlayerChangedWorldEvent): Unit = {
    val message = s"${event.getPlayer.getName} changed worlds from ${event.getFrom.getName} to ${event.getPlayer.getWorld.getName}"
    val f = Future {
      var httpPost = new HttpPost(player_events_url)
      val params = new StringEntity(s"""{\"content\":\"$message\"}""")
      params.setContentType("application/json")
      httpPost.setEntity(params)

      httpClient.execute(httpPost)
    }
  }

  @EventHandler(ignoreCancelled = true)
  def onPlayerKick(event: PlayerKickEvent): Unit = {
    val message = s"${event.getPlayer.getName} kicked"
    val f = Future {
      var httpPost = new HttpPost(player_events_url)
      val params = new StringEntity(s"""{\"content\":\"$message\"}""")
      params.setContentType("application/json")
      httpPost.setEntity(params)

      httpClient.execute(httpPost)
    }
  }

  def closeClient(): Unit = {
    httpClient.close()
  }

  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    if (command.getName.equals("msgOps")) {
      if (args.length > 0) {
        var message = s"""${sender.getName}: ${args.mkString(" ")}"""
        var httpPost = new HttpPost(player_reports_url)
        val params = new StringEntity(s"""{\"content\":\"$message\"}""")
        params.setContentType("application/json")
        httpPost.setEntity(params)
        httpClient.execute(httpPost)
        sender.sendMessage(ChatColor.GOLD + "Message sent")
        return true
      }
      sender.sendMessage(ChatColor.RED + "Usage: /msgOps <message>")
      return true
    }
    false
  }



}
