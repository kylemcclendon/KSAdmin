package webhook

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.bukkit.{ChatColor, Material}
import org.bukkit.command.{Command, CommandExecutor, CommandSender}
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.{AsyncPlayerChatEvent, PlayerBucketEmptyEvent}
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WebhookIntegration extends Listener with CommandExecutor {
  private val url = "https://hooks.slack.com/services/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"
  private val httpClient = new DefaultHttpClient()

  @EventHandler(ignoreCancelled = true)
  def placeTNT(event: BlockPlaceEvent) {
    if (event.getBlock.getType.equals(Material.TNT)) {
      val location = event.getBlock.getLocation
      val message = s"{${event.getPlayer.getName} placed tnt at: ${location.getWorld.getName}: ${location.getX},${location.getY},${location.getZ}}"

      val f = Future {
        var httpPost = new HttpPost("https://discordapp.com/api/webhooks/287113395663142912/-DCEbdI5IY0tH-UhsiXUy28lTYqhxM4QOzNVRklDD85RDu9k0HJ-rqoh6EWwKOoaSqUU")
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
    val message = s"{${event.getPlayer.getName} poured lava at: ${location.getWorld.getName}: ${location.getX},${location.getY},${location.getZ}}"

    if (event.getBucket.equals(Material.LAVA_BUCKET)) {
      val f = Future {
        var httpPost = new HttpPost("https://discordapp.com/api/webhooks/287113395663142912/-DCEbdI5IY0tH-UhsiXUy28lTYqhxM4QOzNVRklDD85RDu9k0HJ-rqoh6EWwKOoaSqUU")
        val params = new StringEntity(s"""{\"content\":\"$message\"}""")
        params.setContentType("application/json")
        httpPost.setEntity(params)

        httpClient.execute(httpPost)
      }
    }
  }

  @EventHandler
  def onAllCapsChat(event: AsyncPlayerChatEvent): Unit = {
    val message = event.getMessage

    if (message.matches("^([A-Z.\\s\\d!;.,:]){10,}$")) {
      val f = Future {
        val capsMessage = event.getPlayer.getName + ": " + message
        var httpPost = new HttpPost("https://discordapp.com/api/webhooks/287113395663142912/-DCEbdI5IY0tH-UhsiXUy28lTYqhxM4QOzNVRklDD85RDu9k0HJ-rqoh6EWwKOoaSqUU")
        val params = new StringEntity(s"""{\"content\":\"$capsMessage\"}""")
        params.setContentType("application/json")
        httpPost.setEntity(params)

        httpClient.execute(httpPost)
      }
    }
  }

  def closeClient(): Unit = {
    httpClient.close()
  }

  override def onCommand(sender: CommandSender, command: Command, label: String, args: Array[String]): Boolean = {
    if (command.getName.equals("msgOps")) {
      if (args.length > 0) {
        var message = args.mkString(" ")
        var httpPost = new HttpPost("https://discordapp.com/api/webhooks/287113395663142912/-DCEbdI5IY0tH-UhsiXUy28lTYqhxM4QOzNVRklDD85RDu9k0HJ-rqoh6EWwKOoaSqUU")
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
