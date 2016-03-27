package giant

import java.util.Random

import net.kylemc.kadmin.Kadmin

/**
  * Created by Kyle on 3/27/2016.
  */
class Giant {
  private val dropTable: util.ArrayList[Nothing] = new util.ArrayList[Nothing]
  private var listSize: Int = 0
  private[kadmin] var plugin: Nothing = null

  def this(instance: Kadmin) {
    this()
    this.plugin = instance
  }

  protected def initItems {
    val dsword: Nothing = new Nothing(Material.DIAMOND_SWORD, 1)
    var im: Nothing = dsword.getItemMeta
    im.setDisplayName("Giant Slaying Blade")
    dsword.setItemMeta(im)
    dsword.setItemMeta(im)
    dsword.addEnchantment(Enchantment.DAMAGE_ALL, 4)
    dsword.addEnchantment(Enchantment.DURABILITY, 3)
    val darmor: Nothing = new Nothing(Material.DIAMOND_CHESTPLATE, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Giant's Armor")
    darmor.setItemMeta(im)
    darmor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
    darmor.addEnchantment(Enchantment.DURABILITY, 3)
    val dboots: Nothing = new Nothing(Material.DIAMOND_BOOTS, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Spiked Boots")
    val lore: util.List[String] = java.util.Arrays.asList(Array[String](org.bukkit.ChatColor.DARK_PURPLE + "Boots infused with gripping iron spikes"))
    im.setLore(lore)
    dboots.setItemMeta(im)
    darmor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
    dboots.addEnchantment(Enchantment.DURABILITY, 3)
    val diamond: Nothing = new Nothing(Material.DIAMOND, 4)
    val crystals: Nothing = new Nothing(Material.PRISMARINE_CRYSTALS, 6)
    val bow: Nothing = new Nothing(Material.BOW, 1)
    im = darmor.getItemMeta
    im.setDisplayName("Adamantium Bow")
    bow.setItemMeta(im)
    bow.addEnchantment(Enchantment.ARROW_DAMAGE, 4)
    bow.addEnchantment(Enchantment.DURABILITY, 3)
    val shards: Nothing = new Nothing(Material.PRISMARINE_SHARD, 10)
    val netherstar: Nothing = new Nothing(Material.NETHER_STAR, 1)
    dropTable.add(dsword)
    dropTable.add(shards)
    dropTable.add(diamond)
    dropTable.add(dboots)
    dropTable.add(darmor)
    dropTable.add(crystals)
    dropTable.add(diamond)
    dropTable.add(bow)
    dropTable.add(shards)
    dropTable.add(diamond)
    dropTable.add(netherstar)
    listSize = dropTable.size
  }

  @EventHandler private def playerHurtGiant(event: Nothing) {
    val l: Nothing = event.getEntity.getLocation
    event.getEntity.getWorld.playEffect(l, org.bukkit.Effect.STEP_SOUND, 152)
    if (event.getEntity.getType.equals(EntityType.GIANT) && event.getDamager.isInstanceOf[Nothing]) {
      val random: Random = new Random(System.currentTimeMillis)
      val next: Int = random.nextInt(15)
      val sound: Int = random.nextInt(3)
      if (sound == 2) {
        Bukkit.getWorld(event.getEntity.getWorld.getUID).playSound(l, Sound.ZOMBIE_HURT, 6.0F, 0.1F)
      }
      if (event.getEntity.isDead) {
        return
      }
      giantAttackSelector(event.getEntity.asInstanceOf[Nothing], event.getDamager)
      if (next == 3) {
        var i: Int = 0
        while (i < 3) {
          {
            (event.getEntity.getWorld.spawnEntity(l, EntityType.ZOMBIE).asInstanceOf[Nothing]).setBaby(true)
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
  }

  @EventHandler(priority = org.bukkit.event.EventPriority.HIGH, ignoreCancelled = true) private def spawnGiant(event: Nothing) {
    if (event.getEntityType.equals(EntityType.GIANT)) {
      Bukkit.getWorld(event.getEntity.getWorld.getUID).playSound(event.getEntity.getLocation, Sound.ENDERDRAGON_GROWL, 6.0F, 0.7F)
      return
    }
    if ((event.getEntityType.equals(EntityType.ZOMBIE)) && (event.getSpawnReason.equals(CreatureSpawnEvent.SpawnReason.NATURAL)) && (event.getEntity.getLocation.getBlock.getLightFromSky eq 15)) {
      val r: Random = new Random(System.currentTimeMillis)
      var next: Int = 0
      if ((event.getLocation.getBlock.getBiome.equals(Biome.EXTREME_HILLS_PLUS_MOUNTAINS)) || (event.getLocation.getBlock.getBiome.equals(Biome.EXTREME_HILLS_MOUNTAINS))) {
        next = r.nextInt(10)
      }
      else {
        next = r.nextInt(50)
      }
      if (next == 5) {
        val l: Nothing = event.getLocation
        val g: Nothing = event.getEntity.getWorld.spawnEntity(l, EntityType.GIANT).asInstanceOf[Nothing]
        g.getHandle.getAttributeInstance(net.minecraft.server.v1_8_R3.GenericAttributes.maxHealth).setValue(200.0D)
        g.setHealth(200.0D)
        Bukkit.getWorld(event.getEntity.getWorld.getUID).playSound(l, Sound.ENDERDRAGON_GROWL, 6.0F, 1.0F)
        event.getEntity.remove
      }
    }
  }

  @EventHandler private def giantDeath(event: Nothing) {
    if ((event.getEntityType.equals(EntityType.GIANT)) && ((event.getEntity.getKiller.isInstanceOf[Nothing]))) {
      val r: Random = new Random(System.currentTimeMillis)
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).playSound(event.getEntity.getLocation, Sound.BLAZE_DEATH, 6.0F, 0.5F)
      var index: Int = r.nextInt(listSize)
      if (index == listSize - 1) {
        index = r.nextInt(listSize)
      }
      if (index == listSize - 1) {
        index = r.nextInt(listSize)
      }
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).dropItem(event.getEntity.getLocation, dropTable.get(index))
      val potion: Nothing = new Nothing(Material.POTION, 1, 8229.toShort)
      Bukkit.getServer.getWorld(event.getEntity.getWorld.getUID).dropItem(event.getEntity.getLocation, potion)
      event.setDroppedExp(200)
    }
  }

  private def giantAttackSelector(giant: Nothing, damager: Nothing) {
    val rand: Random = new Random(System.currentTimeMillis)
    val attack: Int = rand.nextInt(5)
    attack match {
      case 0 =>
        break //todo: break is not supported
      case 1 =>
        giantLeap(giant.getLocation, giant)
        break //todo: break is not supported
      case 2 =>
        giantRoar(giant.getLocation)
        break //todo: break is not supported
      case 3 =>
        giantStomp(giant.getLocation, giant)
        break //todo: break is not supported
      case 4 =>
        giantToss(giant, damager)
    }
  }

  private def giantLeap(l: Nothing, giant: Nothing) {
    giant.setVelocity(new Nothing(0, 0, 0))
    l.getWorld.playSound(l, Sound.ENDERDRAGON_WINGS, 6.0F, 0.1F)
    giant.getHandle.move(0.0D, 20.0D, 0.0D)
  }

  private def giantStomp(l: Nothing, giant: Nothing) {
    val players: util.List[Nothing] = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers
    val close: util.List[Nothing] = new util.ArrayList[Nothing]
    import scala.collection.JavaConversions._
    for (p <- players) {
      if (getDistance(l, p.getLocation) <= 10.0D) {
        close.add(p)
      }
    }
    l.getWorld.playSound(l, Sound.ENDERDRAGON_GROWL, 6.0F, 1.5F)
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
      def run {
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ + 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX, l.getY, l.getZ - 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ + 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ - 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX - 5.0D, l.getY, l.getZ + 5.0D, 0.0F, false, false)
        l.getWorld.createExplosion(l.getX + 5.0D, l.getY, l.getZ - 5.0D, 0.0F, false, false)
        import scala.collection.JavaConversions._
        for (p <- close) {
          p.damage(20.0D, giant)
          p.addPotionEffect(new Nothing(PotionEffectType.SLOW, 200, 1))
          p.addPotionEffect(new Nothing(PotionEffectType.WEAKNESS, 200, 1))
          p.addPotionEffect(new Nothing(PotionEffectType.CONFUSION, 200, 1))
          pushAway(p, 3.0D, giant)
        }
      }
    }, 20L)
    Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
      def run {
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

  private def giantRoar(l: Nothing) {
    val players: util.List[Nothing] = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers
    import scala.collection.JavaConversions._
    for (p <- players) if (getDistance(l, p.getLocation) <= 20.0D) {
      l.getWorld.playSound(l, Sound.ENDERDRAGON_GROWL, 6.0F, 0.7F)
      p.addPotionEffect(new Nothing(PotionEffectType.SLOW, 200, 1))
      p.addPotionEffect(new Nothing(PotionEffectType.WEAKNESS, 200, 2))
      p.addPotionEffect(new Nothing(PotionEffectType.CONFUSION, 200, 2))
    }
  }

  private def giantToss(giant: Nothing, damager: Nothing) {
    if ((damager.isInstanceOf[Nothing])) {
      val player: Nothing = damager.asInstanceOf[Nothing]
      val dist: Boolean = getDistance(player.getLocation, giant.getLocation) < 5.0D
      if ((player.getInventory.getBoots != null) && (player.getInventory.getBoots.getType eq Material.DIAMOND_BOOTS) && (player.getInventory.getBoots.getItemMeta != null) && (player.getInventory.getBoots.getItemMeta.getDisplayName != null) && (player.getInventory.getBoots.getItemMeta.getDisplayName.equals("Spiked Boots"))) {
        if (dist) {
          player.setVelocity(new Nothing(0, 1, 0))
        }
        return
      }
      if (dist) {
        player.setVelocity(new Nothing(0.0D, 1.5D, 0.0D))
      }
    }
  }

  @EventHandler
  @throws[InterruptedException]
  private def giantFall(event: Nothing) {
    if ((event.getEntityType.equals(EntityType.ARMOR_STAND)) && (event.getCause.equals(EntityDamageEvent.DamageCause.PROJECTILE))) {
      event.setCancelled(true)
    }
    if (event.getEntityType.equals(EntityType.GIANT)) {
      if ((event.getCause.equals(EntityDamageEvent.DamageCause.FIRE)) || (event.getCause.equals(EntityDamageEvent.DamageCause.FIRE_TICK)) || (event.getCause.equals(EntityDamageEvent.DamageCause.POISON))) {
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
        val l: Nothing = event.getEntity.getLocation
        val players: util.List[Nothing] = Bukkit.getServer.getWorld(l.getWorld.getUID).getPlayers
        val close: util.List[Nothing] = new util.ArrayList[Nothing]
        import scala.collection.JavaConversions._
        for (p <- players) {
          if (getDistance(l, p.getLocation) <= 20.0D) {
            close.add(p)
          }
        }
        event.getEntity.getWorld.createExplosion(l.getX, l.getY, l.getZ, 0.0F, false, false)
        Bukkit.getServer.getScheduler.scheduleSyncDelayedTask(this.plugin, new Runnable() {
          def run {
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
          def run {
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
          def run {
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
        import scala.collection.JavaConversions._
        for (p <- close) {
          p.damage(35.0D, event.getEntity)
          p.addPotionEffect(new Nothing(PotionEffectType.SLOW, 200, 1))
          p.addPotionEffect(new Nothing(PotionEffectType.WEAKNESS, 200, 1))
          p.addPotionEffect(new Nothing(PotionEffectType.CONFUSION, 200, 1))
          pushAway(p, 7.0D, event.getEntity)
        }
        event.setCancelled(true)
      }
    }
  }

  private def getDistance(ent1: Nothing, ent2: Nothing): Double = {
    val x2: Double = Math.pow(ent2.getX - ent1.getX, 2.0D)
    val y2: Double = Math.pow(ent2.getY - ent1.getY, 2.0D)
    val z2: Double = Math.pow(ent2.getZ - ent1.getZ, 2.0D)
    return Math.sqrt(x2 + y2 + z2)
  }

  private def pushAway(player: Nothing, speed: Double, entity: Nothing) {
    if ((player.getInventory.getBoots != null) && (player.getInventory.getBoots.getType eq Material.DIAMOND_BOOTS) && (player.getInventory.getBoots.getItemMeta != null) && (player.getInventory.getBoots.getItemMeta.getDisplayName != null) && (player.getInventory.getBoots.getItemMeta.getDisplayName.equals("Spiked Boots")) && (player.getInventory.getBoots.getItemMeta.getLore != null) && (player.getInventory.getBoots.getItemMeta.getLore.contains(org.bukkit.ChatColor.DARK_PURPLE + "Boots infused with gripping iron spikes"))) {
      return
    }
    val unitVector: Nothing = player.getLocation.toVector.subtract(entity.getLocation.toVector).normalize
    player.setVelocity(unitVector.multiply(speed))
  }

  @EventHandler def disableFirst(event: Nothing) {
    val plug: Nothing = Bukkit.getPluginManager.getPlugin("Multiverse-Core")
    if (event.getPlugin.equals(plug)) {
      Bukkit.getPluginManager.disablePlugin(this.plugin)
    }
  }
}
