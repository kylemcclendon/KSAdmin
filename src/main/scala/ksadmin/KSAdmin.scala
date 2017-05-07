package ksadmin

import java.io.{File, IOException}

import maintenance.Maintenance
import org.bukkit.inventory.FurnaceRecipe
import bonemeal.Bonemeal
import categorized_warps.CategorizedWarps
import chest_protect.ChestProtect
import feather_clip.FeatherClip
import flower.Flower
import horse_teleport.HorseTeleport
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.{ItemStack, ShapedRecipe, ShapelessRecipe}
import org.bukkit.plugin.java.JavaPlugin
import pvp.PVP
import restart.Restart
import webhook.WebhookIntegration
import timber.Timber

class KSAdmin extends JavaPlugin {
  var settingsFile: File = _
  var settings: YamlConfiguration = _
  var dFolder: Option[File] = None

  private val bm = new Bonemeal()
  private val cw = CategorizedWarps(this)
  private val fc = new FeatherClip
  private val ht = new HorseTeleport
  private val pvp = new PVP
  private val tim = new Timber
  private val cp = new ChestProtect
  private val wi = new WebhookIntegration

  override def onEnable() = {
    val pm = getServer.getPluginManager

    if(getServer.hasWhitelist){
      val maintenance = new Maintenance()
      pm.registerEvents(maintenance, this)
    }

    dFolder = Option(getDataFolder)

    if(dFolder.isEmpty){
      dFolder.get.mkdirs()
    }

    settingsFile = new File(dFolder.get, "config.yml")
    if(!settingsFile.exists()){
      settings = new YamlConfiguration()
      settings.set("Bonemeal", true)
      settings.set("CategorizedWarps", true)
      settings.set("ChestProtect", true)
      settings.set("Crafting", true)
      settings.set("Exploration", true)
      settings.set("Feather", true)
      settings.set("Flower", true)
      settings.set("HorseTeleport", true)
      settings.set("Hyrule", true)
      settings.set("PVP", true)
      settings.set("Timber", true)
      saveSettings
    }

    this.settings = YamlConfiguration.loadConfiguration(this.settingsFile)
    pm.registerEvents(cp, this)
    pm.registerEvents(wi, this)
    if (this.settings.getBoolean("Bonemeal")) {
      pm.registerEvents(bm, this)
    }
    if (this.settings.getBoolean("CategorizedWarps")) {
      pm.registerEvents(cw, this)
      getCommand("cwarps").setExecutor(cw)
      getCommand("shrug").setExecutor(cw)
      getCommand("quartz").setExecutor(cw)
    }
    if (this.settings.getBoolean("Crafting")) {
      val book = new ShapedRecipe(new ItemStack(Material.BOOK, 1))
      book.shape(" A ", " A ", " A ")
      book.setIngredient('A', Material.PAPER)
      getServer.addRecipe(book)
      val saddle= new ShapedRecipe(new ItemStack(Material.SADDLE, 1))
      saddle.shape("A A", "AAA", " B ")
      saddle.setIngredient('A', Material.LEATHER)
      saddle.setIngredient('B', Material.IRON_INGOT)
      getServer.addRecipe(saddle)
      val nametag = new ShapelessRecipe(new ItemStack(Material.NAME_TAG, 1))
      nametag.addIngredient(Material.STRING)
      nametag.addIngredient(Material.WOOD)
      nametag.addIngredient(Material.INK_SACK)
      getServer.addRecipe(nametag)
      val flesh: ItemStack = new ItemStack(Material.LEATHER, 1)
      val leather: FurnaceRecipe = new FurnaceRecipe(flesh, Material.ROTTEN_FLESH)
      getServer.addRecipe(leather)
    }
    if (this.settings.getBoolean("Feather")) {
      pm.registerEvents(fc, this)
    }
    if (this.settings.getBoolean("Flower")) {
      getCommand("flower").setExecutor(new Flower)
    }
    if (this.settings.getBoolean("HorseTeleport")) {
      pm.registerEvents(ht, this)
    }
    if (this.settings.getBoolean("PVP")) {
      pm.registerEvents(pvp, this)
    }
    if (this.settings.getBoolean("Timber")) {
      pm.registerEvents(tim, this)
    }

    getCommand("msgOps").setExecutor(wi)

    val restart = Restart(this)
  }

  override def onDisable() {
    wi.closeClient()
    getLogger.info("Kadmin disabled")
  }

  def saveSettings: Boolean = {
    if (!settingsFile.exists) {
      this.settingsFile.getParentFile.mkdirs
    }
    try {
      this.settings.save(this.settingsFile)
      return true
    }
    catch {
      case e: IOException => e.printStackTrace()
    }
    false
  }
}