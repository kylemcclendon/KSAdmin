import java.io.{File, IOException}

import bonemeal.Bonemeal
import categorized_warps.CategorizedWarps
import net.kylemc.kadmin.{Runecraft_Teles, _}
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin

class KSAdmin extends JavaPlugin {
  var plugin: KSAdmin = _
  var settingsFile: File = _
  var settings: YamlConfiguration = _
  var dFolder: Option[File] = None

  private val bm: Bonemeal = new Bonemeal()
  private val cw: CategorizedWarps = new CategorizedWarps(this)
  private val fc: FeatherClip = new FeatherClip
  private val gi: Giant = new Giant(this)
  private val ht: HorseTeleport = new HorseTeleport
  private val pv: PVP = new PVP
  private val restart: Restart = new Restart(this)
  private val tim: Timber = new Timber
  private val run: Runecraft_Teles = new Runecraft_Teles
  var explorationThread: Thread = _
  var threadVar = true

  override def onEnable = {
    if(getServer.hasWhitelist){
      new Maintenance(this)
    }

    val pm = getServer.getPluginManager
    dFolder = Option(getDataFolder)

    if(dFolder.isEmpty){
      dFolder.get.mkdirs()
    }

    settingsFile = new File(dFolder.get, "config.yml")
    if(settingsFile.exists()){
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
      settings.set("Rainbow", true)
      settings.set("Restart", true)
      settings.set("Runecraft", true)
      settings.set("Timber", true)
      saveSettings
    }

    this.settings = YamlConfiguration.loadConfiguration(this.settingsFile)
    if (this.settings.getBoolean("Bonemeal")) {
      pm.registerEvents(this.bm, this)
    }
    if (this.settings.getBoolean("CategorizedWarps")) {
      pm.registerEvents(this.cw, this)
      getCommand("cwarps").setExecutor(this.cw)
    }
    if (this.settings.getBoolean("Crafting")) {
      val book: Nothing = new Nothing(new Nothing(Material.BOOK, 1))
      book.shape(Array[String](" A ", " A ", " A "))
      book.setIngredient('A', Material.PAPER)
      getServer.addRecipe(book)
      val saddle: Nothing = new Nothing(new Nothing(Material.SADDLE, 1))
      saddle.shape(Array[String]("A A", "AAA", " B "))
      saddle.setIngredient('A', Material.LEATHER)
      saddle.setIngredient('B', Material.IRON_INGOT)
      getServer.addRecipe(saddle)
      val nametag: Nothing = new Nothing(new Nothing(Material.NAME_TAG, 1))
      nametag.addIngredient(Material.STRING)
      nametag.addIngredient(Material.WOOD)
      nametag.addIngredient(Material.INK_SACK)
      getServer.addRecipe(nametag)
      val flesh: Nothing = new Nothing(Material.LEATHER, 1)
      val leather: Nothing = new Nothing(flesh, Material.ROTTEN_FLESH)
      getServer.addRecipe(leather)
    }
    if (this.settings.getBoolean("Exploration")) {
      t = new Thread(new Exploration)
      t.start
    }
    if (this.settings.getBoolean("Feather")) {
      pm.registerEvents(this.fc, this)
    }
    if (this.settings.getBoolean("Flower")) {
      getCommand("flower").setExecutor(new Flower)
    }
    if (this.settings.getBoolean("Giant")) {
      pm.registerEvents(this.gi, this)
      this.gi.initItems
    }
    if (this.settings.getBoolean("HorseTeleport")) {
      pm.registerEvents(this.ht, this)
    }
    if (this.settings.getBoolean("PVP")) {
      pm.registerEvents(this.pv, this)
    }
    //    if (this.settings.getBoolean("Rainbow")) {
    //      pm.registerEvents(this.rb, this);
    //      getCommand("hoe_undo").setExecutor(this.rb);
    //      getCommand("hoe_help").setExecutor(this.rb);
    //      getCommand("hoe").setExecutor(this.rb);
    //      getCommand("rename").setExecutor(this.rb);
    //    }
    if (this.settings.getBoolean("Restart")) {
      getCommand("restart").setExecutor(this.restart)
      getCommand("ls").setExecutor(this.restart)
      getCommand("quartz").setExecutor(this.restart)
      this.restart.initRestarts
    }
    if (this.settings.getBoolean("Runecraft")) {
      pm.registerEvents(this.run, this)
      this.run.setup
    }
    if (this.settings.getBoolean("Timber")) {
      pm.registerEvents(this.tim, this)
    }
  }

  def onDisable {
    threadVar = false
    try {
      System.out.println("Waiting for Exploration thread to end...")
      t.join
    }
    catch {
      case e: InterruptedException => {
        e.printStackTrace
      }
    }
    if (this.settings.getBoolean("Runecraft")) {
      Runecraft_Teles.writeToFiles
    }
    getLogger.info("Kadmin disabled")
  }

  def saveSettings: Boolean = {
    if (!this.settingsFile.exists) {
      this.settingsFile.getParentFile.mkdirs
    }
    try {
      this.settings.save(this.settingsFile)
      return true
    }
    catch {
      case e: IOException => e.printStackTrace
    }
    false
  }
}
