package webhook

import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.{AsyncPlayerChatEvent, PlayerBucketEmptyEvent}
import org.bukkit.event.{EventHandler, Listener}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WebhookIntegration extends Listener{
  private val url = "https://hooks.slack.com/services/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"
  private val httpClient = new DefaultHttpClient()

  @EventHandler(ignoreCancelled = true)
  def placeTNT(event: BlockPlaceEvent){
    if(event.getBlock.getType.equals(Material.TNT)){
      val location = event.getBlock.getLocation
      val message = s"{${event.getPlayer.getName} placed tnt at: ${location.getWorld.getName}: ${location.getX},${location.getY},${location.getZ}}"

      val f = Future {
        var httpPost = new HttpPost("https://discordapp.com/api/webhooks/287113395663142912/-DCEbdI5IY0tH-UhsiXUy28lTYqhxM4QOzNVRklDD85RDu9k0HJ-rqoh6EWwKOoaSqUU")
        val params = new StringEntity(s"""{\"content\":\"$message\"}""")
        params.setContentType("application/json")
        httpPost.setEntity(params)

        val response = httpClient.execute(httpPost)
      }
    }
  }

  @EventHandler(ignoreCancelled = true)
  def lavaBucket(event: PlayerBucketEmptyEvent): Unit ={
    val location = event.getPlayer.getLocation
    val message = s"{${event.getPlayer.getName} poured lava at: ${location.getWorld.getName}: ${location.getX},${location.getY},${location.getZ}}"

    val f = Future {
      var httpPost = new HttpPost("https://discordapp.com/api/webhooks/287113395663142912/-DCEbdI5IY0tH-UhsiXUy28lTYqhxM4QOzNVRklDD85RDu9k0HJ-rqoh6EWwKOoaSqUU")
      val params = new StringEntity(s"""{\"content\":\"$message\"}""")
      params.setContentType("application/json")
      httpPost.setEntity(params)

      val response = httpClient.execute(httpPost)
    }
  }

  @EventHandler
  def onAllCapsChat(event: AsyncPlayerChatEvent): Unit ={
    val message = event.getMessage

    if(message.matches("^([A-Z.\\s\\d!;.,:]){10,}$")){
      val f = Future {
        val capsMessage = event.getPlayer.getName + ": " + message
        var httpPost = new HttpPost("https://discordapp.com/api/webhooks/287113395663142912/-DCEbdI5IY0tH-UhsiXUy28lTYqhxM4QOzNVRklDD85RDu9k0HJ-rqoh6EWwKOoaSqUU")
        val params = new StringEntity(s"""{\"content\":\"$capsMessage\"}""")
        params.setContentType("application/json")
        httpPost.setEntity(params)

        val response = httpClient.execute(httpPost)
      }
    }
  }

  def closeClient(): Unit ={
    httpClient.close()
  }
}
