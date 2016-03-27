package categorized_warps

import java.io.{File, FileWriter, IOException}

import scala.collection.immutable.HashMap
import org.bukkit.{Bukkit, ChatColor}
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.plugin.Plugin

class CategorizedWarps {
  var plugin: Plugin = null
  var newWarps: File = null
  var warps: Map[String, List[String]] = Map()
  var categories: Map[String, String] = Map()
  private[kadmin] val cats: String = ChatColor.GOLD + "Categories: worlds, hyrule, bigtowns, smalltowns, bigbuilds, utilities, old"
  private[kadmin] val use: String = ChatColor.RED + "Usage: /cwarps [category] [page number]"
  var Old = List("Old", "Old's Spawn", "Rapture", "Old's Waypoint Hub", "Cilly", "Great City of Old", "BMB", "Beyond the Mysterious Beyond", "RenekTown", "Melpy's Old Town", "RavineTown", "DD622's Old Town", "Arena", "PVP Arena", "CTF", "Capture the Flag", "Zombie", "Nazi Zombies", "Exziron", "Capture the Flag")
  var Hyrule = List("hyrule", "Hyrule hub", "forest", "Sacred Forest Meadow", "water", "Lake Hylia", "shadow", "Graveyard", "light", "Temple of Time")
  var BigTowns = List("PortSerenity/ps/Metro", "quicksilver20, DD622, SnowLeopard, Paradoxagent", "SkyCity", "Sundav", "Terra", "idmb", "Tulloch", "ERROR372", "Fuzz", "DD622", "Misteria", "ERROR372", "Zion", "Exziron", "HolOfSol", "Solsetur")
  var SmallTowns = List("Tenuto", "ERROR372 + DD622", "HolidayTown", "stella96", "Otok", "Solsetur", "Riverside", "SnowLeopard448", "TwoRivers", "Solsetur", "NewRussia", "Electricut, meeko305, aesusure", "Jehlan", "Myuufasa", "Cabins", "ERROR372", "Baragh", "Sundav", "Mooncrest", "ace714", "Bubble", "DD622", "IceHut", "oilsands5")
  var BigBuilds = List("Lotus", "DD622", "Travencal", "quicksilver20", "AstaShip", "Lioness", "Tanith", "quicksilver20 + DD622", "UFO", "DD622", "Winterland", "DD622", "mirkwood", "Sundav", "Bloodlust", "oilsands5", "LinkOverLook", "DD622", "zelda", "DD622", "Winterfell", "Sundav", "Ship", "DD622", "Tardis", "quicksilver20", "DDBook", "DD622", "Windfish", "DD622", "Ark", "Community Effort", "DPRanch", "DonPretzel", "Donut", "DD622", "Kanto", "fatrat92", "iTower", "unknown", "Itsumoniyoru", "unknown", "Andy", "ICU27", "Air", "Exziron", "Dragon", "DD622", "Maze", "DD622", "ddpark", "DD622")
  var Utilities = List("Tanith", "Main Spawn/Info", "TWC", "Tanith Warp Center", "cube", "Sheep Farm", "moo", "Sheep Farm", "Idaho", "Villager Farm", "sheep", "idmb's sheep farm")
  var Worlds = List("Tanith", "PVE Survival World", "Hyrule", "Ocarina of Time Recreation", "Temp", "Temporary World", "PVP", "PVP-based World", "Old", "Creative Alpha World")

  def CategorizedWarps(ksadmin: Plugin) {
    plugin = ksadmin
    initCategories
  }

  private def initCategories {
    warps = Map("hyrule" -> this.Hyrule,
      "bigtowns" -> this.BigTowns,
      "smalltowns"-> this.SmallTowns,
      "bigbuilds" ->BigBuilds,
      "utilities" -> Utilities,
      "old" -> Old,
      "worlds" -> Worlds)
    categories = Map("hyrule" -> "Hyrule",
      "bigtowns" -> "BigTowns",
      "smalltowns" -> "SmallTowns",
      "bigbuilds" -> "BigBuilds",
      "utilities" -> "Utilities",
      "old"-> "Old",
      "worlds" -> "Worlds")
    newWarps = new File(plugin.getDataFolder, "newWarps.yml")
  }

  @SuppressWarnings(Array("deprecation")) def onCommand(sender: CommandSender, cmd: Command, label: String, args: Array[String]): Boolean = {
    if (cmd.getName.equalsIgnoreCase("cwarps")) {
      if (args.length < 1) {
        sender.sendMessage(this.cats)
        sender.sendMessage(this.use)
        return true
      }
      if (args.length == 1) {
        val cat: String = args(0).toLowerCase
        if (Option(warps(cat)).isDefined) {
          val warp = warps(cat)
          val category = categories(cat)
          sender.sendMessage(ChatColor.GOLD + "---------------" + category + " Pages: " + Math.ceil(warp.length / 10.0D).toInt + "---------------")
          sender.sendMessage(ChatColor.GOLD + "----------Format: Warp - Builder(s)/Description----------")
          var i: Int = 1
          while (i < 10) {
            {
              if (i >= warp.length) {
                return true
              }
              sender.sendMessage(ChatColor.AQUA + warp(i - 1) + " - " + warp(i))
            }
            i += 2
          }
        }
        else {
          sender.sendMessage(ChatColor.RED + "Invalid Category")
        }
      }
      else if (args.length == 2) {
        if (args(0).equalsIgnoreCase("info") && sender.getName.equals("@")) {
          val x = args(1)
          val p: Player = Bukkit.getServer.getPlayer(x)
          onCommand(p, cmd, "", new Array[String](0))
          return true
        }
        if (args(1).matches("[0-9]+")) {
          try {
            val num: Int = args(1).toInt
            val pnum: Int = (num - 1) * 10
            val cat: String = args(0).toLowerCase
            if (Option(warps(cat)).isDefined) {
              val h = warps(cat)
              val category = categories(cat)
              sender.sendMessage(ChatColor.GOLD + "---------------" + category + " Page: " + num + "/" + Math.ceil(h.length / 10.0D).toInt + "---------------")
              sender.sendMessage(ChatColor.GOLD + "----------Format: Warp - Builder(s)/Description----------")
              var i: Int = 0
              while (i < 10) {
                {
                  if (pnum + i >= h.length) {
                    return true
                  }
                  sender.sendMessage(ChatColor.AQUA + h(pnum + i) + " - " + h(pnum + (i + 1)))
                }
                i += 2
              }
            }
            else {
              sender.sendMessage(ChatColor.RED + "Invalid Category")
            }
          }
          catch {
            case e: NumberFormatException => sender.sendMessage(ChatColor.RED + "Number is too large!"); return true
          }
        }
        sender.sendMessage(ChatColor.RED + "Invalid Number")
      }
      else {
        sender.sendMessage(ChatColor.RED + "Usage: /cwarps [category] [page number]")
      }
      return true
    }
    false
  }

  @EventHandler def setWarpListen(event: PlayerCommandPreprocessEvent) {
    val msg: String = event.getMessage
    val parts: Array[String] = msg.split(" ")
    if (!event.getPlayer.isOp && parts.length > 1 && parts(0).equalsIgnoreCase("/warps") && parts(1).equalsIgnoreCase("list")) {
      event.getPlayer.sendMessage(ChatColor.RED + "Please use '/cwarps' to access the warps list")
      event.setCancelled(true)
    }
    else if (event.getPlayer.isOp && (parts.length == 2) && parts(0).equalsIgnoreCase("/setwarp")) {
      val warp: String = parts(1)
      try {
        val fStream: FileWriter = new FileWriter(newWarps, true)
        fStream.append(warp)
        fStream.append(System.getProperty("line.separator"))
        fStream.flush()
        fStream.close()
      }
      catch {
        case ex: IOException => System.out.println("Could not write to text file!")
      }
    }
  }
}
