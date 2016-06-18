package giant

import java.util.Random

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.collection.JavaConverters.seqAsJavaListConverter
import ksadmin.KSAdmin
import org.bukkit.Bukkit
import org.bukkit.block.Biome
import org.bukkit.{Location, Material, Sound}
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.{Entity, EntityType, LivingEntity, Player, Zombie}
import org.bukkit.inventory.ItemStack
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftGiant
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.entity.{CreatureSpawnEvent, EntityDamageByEntityEvent, EntityDamageEvent, EntityDeathEvent}
import org.bukkit.plugin.Plugin
import org.bukkit.potion.{PotionEffect, PotionEffectType}
import org.bukkit.util.Vector
import org.bukkit.event.server.PluginDisableEvent

case class Giant(instance: KSAdmin) extends Listener{
  var dropsTable: List[ItemStack] = Nil
  private var listSize = 0
  var plugin: Plugin = instance
  initItems()

  protected def initItems() {
    val dsword = new ItemStack(Material.DIAMOND_SWORD, 1)
    var im = dsword.getItemMeta
    im.setDisplayName("Giant Slaying Blade")
    dsword.setItemMeta(im)
    dsword.setItemMeta(im)
    dsword.addEnchantment(Enchantment.DAMAGE_ALL, 4)
    dsword.addEnchantment(Enchantment.DURABILITY, 3)
    val darmor = new ItemStack(Material.DIAMOND_CHESTPLATE, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Giant's Armor")
    darmor.setItemMeta(im)
    darmor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
    darmor.addEnchantment(Enchantment.DURABILITY, 3)
    val dboots = new ItemStack(Material.DIAMOND_BOOTS, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Spiked Boots")
    val lore = List(org.bukkit.ChatColor.DARK_PURPLE + "Boots infused with gripping iron spikes")
    im.setLore(lore.asJava)
    dboots.setItemMeta(im)
    darmor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
    dboots.addEnchantment(Enchantment.DURABILITY, 3)
    val diamond = new ItemStack(Material.DIAMOND, 4)
    val crystals = new ItemStack(Material.PRISMARINE_CRYSTALS, 6)
    val bow = new ItemStack(Material.BOW, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Adamantium Bow")
    bow.setItemMeta(im)
    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 4)
    bow.addEnchantment(Enchantment.DURABILITY, 3)
    val shards = new ItemStack(Material.PRISMARINE_SHARD, 10)
    val netherstar = new ItemStack(Material.NETHER_STAR, 1)
    dropsTable = List(dsword,shards,diamond,dboots,darmor,crystals,diamond,bow,shards,diamond,netherstar)
    listSize = dropsTable.size
  }

  @EventHandler private def playerHurtGiant(event: EntityDamageByEntityEvent) {
    val l = event.getEntity.getLocation
    event.getEntity.getWorld.playEffect(l, org.bukkit.Effect.STEP_SOUND, 152)
    if (event.getEntity.getType.equals(EntityType.GIANT) && event.getDamager.isInstanceOf[Player]) {
      val g = event.getEntity.asInstanceOf[CraftGiant]
      g.setTarget(event.getDamager.asInstanceOf[LivingEntity])
      val random = new Random(System.currentTimeMillis)
      val next = random.nextInt(15)
      val sound = random.nextInt(3)
      if (sound == 2) {
        Bukkit.getWorld(event.getEntity.getWorld.getUID).playSound(l, Sound.ENTITY_ZOMBIE_HURT, 6.0F, 0.1F)
      }
      if (!event.getEntity.isDead) {
        giantAttackSelector(event.getEntity.asInstanceOf[CraftGiant], event.getDamager)

        if (next == 3) {
          event.getEntity.getWorld.spawnEntity(l, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
          event.getEntity.getWorld.spawnEntity(l, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
          event.getEntity.getWorld.spawnEntity(l, EntityType.ZOMBIE).asInstanceOf[Zombie].setBaby(true)
        }
      }
    }
  }

  @EventHandler(priority = org.bukkit.event.EventPriority.HIGH, ignoreCancelled = true)
  private def spawnGiant(event: CreatureSpawnEvent) {
    if (event.getEntityType.equals(EntityType.GIANT)) {
      Bukkit.getWorld(event.getEntity.getWorld.getUID).playSound(event.getEntity.getLocation, Sound.ENTITY_ENDERDRAGON_GROWL, 6.0F, 0.7F)
    }
    if (event.getEntityType.equals(EntityType.ZOMBIE) && event.getSpawnReason.equals(CreatureSpawnEvent.SpawnReason.NATURAL) && (event.getEntity.getLocation.getBlock.getLightFromSky == 15)) {
      val r = new Random(System.currentTimeMillis)
      var next = 0
      if (event.getLocation.getBlock.getBiome.equals(Biome.MUTATED_EXTREME_HILLS) || event.getLocation.getBlock.getBiome.equals(Biome.MUTATED_TAIGA_COLD) || event.getLocation.getBlock.getBiome.equals(Biome.MUTATED_EXTREME_HILLS_WITH_TREES)) {
        next = r.nextInt(10)
      }
      else if (event.getLocation.getBlock.getBiome.equals(Biome.EXTREME_HILLS) || event.getLocation.getBlock.getBiome.equals(Biome.EXTREME_HILLS_WITH_TREES) || event.getLocation.getBlock.getBiome.equals(Biome.TAIGA_COLD_HILLS) || event.getLocation.getBlock.getBiome.equals(Biome.TAIGA_HILLS))
        next = r.nextInt(25)
      else {
        next = r.nextInt(50)
      }
      if (next == 5) {
        val l = event.getLocation
        val g = event.getEntity.getWorld.spawnEntity(l, EntityType.GIANT).asInstanceOf[CraftGiant]
        g.getHandle.getAttributeInstance(net.minecraft.server.v1_10_R1.GenericAttributes.maxHealth).setValue(200.0D)
        g.setHealth(200.0D)
        Bukkit.getWorld(event.getEntity.getWorld.getUID).playSound(l, Sound.ENTITY_ENDERDRAGON_GROWL, 6.0F, 1.0F)
        event.getEntity.remove()
      }
    }
  }

  @EventHandler private def giantDeath(event: EntityDeathEvent) {
    if (event.getEntityType.equals(EntityType.GIANT) && event.getEntity.getKiller.isInstanceOf[Player]) {
      val r = new Random(System.currentTimeMillis)
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).playSound(event.getEntity.getLocation, Sound.ENTITY_BLAZE_DEATH, 6.0F, 0.5F)
      var index = r.nextInt(listSize)
      if (index == listSize - 1) {
        index = r.nextInt(listSize)
      }
      if (index == listSize - 1) {
        index = r.nextInt(listSize)
      }
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).dropItem(event.getEntity.getLocation, dropsTable(index))
      val potion = new ItemStack(Material.POTION, 1, 8229.toShort)
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).dropItem(event.getEntity.getLocation, potion)
      event.setDroppedExp(200)
    }
  }

  private def giantAttackSelector(giant: CraftGiant, damager: Entity) {
    val rand = new Random(System.currentTimeMillis)
    val attack = rand.nextInt(5)
    attack match {
      case 0 =>
      case 1 =>
        giantLeap(giant.getLocation, giant)
      case 2 =>
        giantRoar(giant.getLocation)
      case 3 =>
        giantStomp(giant.getLocation, giant)
      case 4 =>
        giantToss(giant, damager)
    }
  }

  private def giantLeap(l: Location, giant: CraftGiant) {
    giant.setVelocity(new Vector(0, 0, 0))
    l.getWorld.playSound(l, Sound.ENTITY_ENDERDRAGON_FLAP, 6.0F, 0.1F)
    giant.getHandle.move(0.0D, 20.0D, 0.0D)
  }

  private def giantStomp(l: Location, giant: CraftGiant) {
    val players = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers.asScala
    var close: List[Player] = Nil
    for (p <- players) {
      if (getDistance(l, p.getLocation) <= 10.0D) {
        close = close.::(p)
      }
    }
    l.getWorld.playSound(l, Sound.ENTITY_ENDERDRAGON_GROWL, 6.0F, 1.5F)
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
      def run() {
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ + 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ - 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ + 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ - 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ + 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ - 5.0D, 0.0F, false, false)
        for (p <- close) {
          p.damage(20.0D, giant)
          p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1))
          p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1))
          p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1))
          pushAway(p, 3.0D, giant)
        }
      }
    }, 20L)
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
      def run() {
        l.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ + 10.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ - 10.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ + 10.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ + 10.0D, 0.0F, false, false)
      }
    }, 25L)
  }

  private def giantRoar(l: Location) {
    val players = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers.asScala
    for (p <- players) if (getDistance(l, p.getLocation) <= 20.0D) {
      l.getWorld.playSound(l, Sound.ENTITY_ENDERDRAGON_GROWL, 6.0F, 0.7F)
      p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1))
      p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 2))
      p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 2))
    }
  }

  private def giantToss(giant: CraftGiant, damager: Entity) {
    damager match {
      case player: Player =>
        if(getDistance(player.getLocation, giant.getLocation) < 5.0D){
          if ((player.getInventory.getBoots != null) && (player.getInventory.getBoots.getType eq Material.DIAMOND_BOOTS) && (player.getInventory.getBoots.getItemMeta != null) && (player.getInventory.getBoots.getItemMeta.getDisplayName != null) && player.getInventory.getBoots.getItemMeta.getDisplayName.equals("Spiked Boots"))
            player.setVelocity(new Vector(0, 1, 0))
          else
            player.setVelocity(new Vector(0.0D, 1.5D, 0.0D))
        }
    }
  }

  @EventHandler
  @throws[InterruptedException]
  private def giantFall(event: EntityDamageEvent) {
    if (event.getEntityType.equals(EntityType.ARMOR_STAND) && event.getCause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
      event.setCancelled(true)
    }
    if (event.getEntityType.equals(EntityType.GIANT)) {
      if (event.getCause.equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause.equals(EntityDamageEvent.DamageCause.FIRE_TICK) || event.getCause.equals(EntityDamageEvent.DamageCause.POISON)) {
        event.setCancelled(true)
      }
      if (event.getCause.equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
        event.setDamage(event.getDamage / 4.0D)
      }
      if (event.getCause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
        event.setDamage(event.getDamage / 2.0D)
      }
      if (event.getCause.equals(EntityDamageEvent.DamageCause.DROWNING)) {
        event.setCancelled(true)
      }
      if (event.getCause.equals(EntityDamageEvent.DamageCause.FALL)) {
        val l = event.getEntity.getLocation
        val players = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers.asScala
        var close: List[Player] = Nil
        for (p <- players) {
          if (getDistance(l, p.getLocation) <= 20.0D) {
            close = close.::(p)
          }
        }
        event.getEntity.getWorld.createExplosion(l.getX, l.getY, l.getZ, 0.0F, false, false)
        Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
          def run() {
            event.getEntity.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX, l.getY, l.getZ + 5.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX, l.getY, l.getZ - 5.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ + 5.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ - 5.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ + 5.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ - 5.0D, 0.0F, false, false)
          }
        }, 3L)
        Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
          def run() {
            event.getEntity.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX, l.getY, l.getZ + 10.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX, l.getY, l.getZ - 10.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ + 10.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ + 10.0D, 0.0F, false, false)
          }
        }, 6L)
        Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
          def run() {
            event.getEntity.getWorld.createExplosion(l.getX + 15.0D, l.getY, l.getZ, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 15.0D, l.getY, l.getZ, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX, l.getY, l.getZ + 15.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX, l.getY, l.getZ - 15.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ - 15.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX + 15.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX + 15.0D, l.getY, l.getZ + 10.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX + 10.0D, l.getY, l.getZ + 15.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ - 15.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 15.0D, l.getY, l.getZ - 10.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 10.0D, l.getY, l.getZ + 15.0D, 0.0F, false, false)
            event.getEntity.getWorld.createExplosion(l.getX - 15.0D, l.getY, l.getZ + 10.0D, 0.0F, false, false)
          }
        }, 9L)
        for (p <- close) {
          p.damage(35.0D, event.getEntity)
          p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1))
          p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1))
          p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 1))
          pushAway(p, 7.0D, event.getEntity)
        }
        event.setCancelled(true)
      }
    }
  }

  private def getDistance(ent1: Location, ent2: Location) = {
    val x2 = Math.pow(ent2.getX - ent1.getX, 2.0D)
    val y2 = Math.pow(ent2.getY - ent1.getY, 2.0D)
    val z2 = Math.pow(ent2.getZ - ent1.getZ, 2.0D)
    Math.sqrt(x2 + y2 + z2)
  }

  private def pushAway(player: Player, speed: Double, entity: Entity) {
    if ((player.getInventory.getBoots != null) && (player.getInventory.getBoots.getType eq Material.DIAMOND_BOOTS) && (player.getInventory.getBoots.getItemMeta != null) && (player.getInventory.getBoots.getItemMeta.getDisplayName != null) && player.getInventory.getBoots.getItemMeta.getDisplayName.equals("Spiked Boots") && (player.getInventory.getBoots.getItemMeta.getLore != null) && player.getInventory.getBoots.getItemMeta.getLore.contains(org.bukkit.ChatColor.DARK_PURPLE + "Boots infused with gripping iron spikes")) {
      return
    }
    val unitVector = player.getLocation.toVector.subtract(entity.getLocation.toVector).normalize
    player.setVelocity(unitVector.multiply(speed))
  }

  @EventHandler def disableFirst(event: PluginDisableEvent) {
    val plug = Bukkit.getPluginManager.getPlugin("Multiverse-Core")
    if (event.getPlugin.equals(plug)) {
      Bukkit.getPluginManager.disablePlugin(this.plugin)
    }
  }
}
