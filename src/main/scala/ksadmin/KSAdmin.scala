package ksadmin

import java.io.{File, IOException}

import maintenance.Maintenance
import org.bukkit.inventory.FurnaceRecipe
import bonemeal.Bonemeal
import giant.Giant
import categorized_warps.CategorizedWarps
import feather_clip.FeatherClip
import flower.Flower
import horse_teleport.HorseTeleport
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.{ItemStack, ShapedRecipe, ShapelessRecipe}
import org.bukkit.plugin.java.JavaPlugin
import pvp.PVP
import restart.net.kylemc.kadmin.Restart
import runecraft.RunecraftTeles
import timber.Timber

class KSAdmin extends JavaPlugin {
  var settingsFile: File = _
  var settings: YamlConfiguration = _
  var dFolder: Option[File] = None

  private val bm = new Bonemeal()
  private val cw = CategorizedWarps(this)
  private val fc = new FeatherClip
  private val gi = Giant(this)
  private val ht = new HorseTeleport
  private val pvp = new PVP
  private val tim = new Timber
  private var rune: RunecraftTeles = null

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
      settings.set("Crafting", true)
      settings.set("Exploration", true)
      settings.set("Feather", true)
      settings.set("Flower", true)
      settings.set("Giant", true)
      settings.set("HorseTeleport", true)
      settings.set("Hyrule", true)
      settings.set("PVP", true)
      settings.set("Runecraft", true)
      settings.set("Timber", true)
      saveSettings
    }

    this.settings = YamlConfiguration.loadConfiguration(this.settingsFile)
    if (this.settings.getBoolean("Bonemeal")) {
      pm.registerEvents(bm, this)
    }
    if (this.settings.getBoolean("CategorizedWarps")) {
      pm.registerEvents(cw, this)
      getCommand("cwarps").setExecutor(cw)
      getCommand("shrug").setExecutor(cw)
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
    if (this.settings.getBoolean("Giant")) {
      pm.registerEvents(gi, this)
    }
    if (this.settings.getBoolean("HorseTeleport")) {
      pm.registerEvents(ht, this)
    }
    if (this.settings.getBoolean("PVP")) {
      pm.registerEvents(pvp, this)
    }
    if (this.settings.getBoolean("Runecraft")) {
      rune = new RunecraftTeles(dFolder.get)
      pm.registerEvents(rune, this)
    }
    if (this.settings.getBoolean("Timber")) {
      pm.registerEvents(tim, this)
    }

    val restart = new Restart(this)
  }

  override def onDisable() {
    if (this.settings.getBoolean("Runecraft") && rune != null) {
      rune.writeToFiles()
    }
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