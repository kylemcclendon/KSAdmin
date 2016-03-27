package runecraft

import java.io._
import java.util.logging.Logger

import net.kylemc.kadmin.Kadmin

/**
  * Created by Kyle on 3/27/2016.
  */
class RunecraftTeles {
  private[kadmin] var airBlocks: util.HashSet[Nothing] = null
  private[kadmin] var wayPoints: util.HashMap[Runecraft_Teles.Signature, Nothing] = null
  private[kadmin] var teles: util.HashMap[Nothing, Runecraft_Teles.Signature] = null
  private[kadmin] val log: Logger = Bukkit.getLogger

  def setup {
    airBlocks = Sets.newHashSet(Material.AIR, Material.DIRT, Material.GRASS, Material.SAND, Material.STONE, Material.MYCEL, Material.DOUBLE_PLANT, Material.LONG_GRASS)
    log.info("Reading Waypoints and Teleports files...")
    readFromFiles
    log.info("Loaded " + wayPoints.size + " waypoints.")
    log.info("Loaded " + teles.size + " teles.")
  }

  @SuppressWarnings(Array("deprecation"))
  @EventHandler(ignoreCancelled = true) def rightClickTele(event: Nothing) {
    val player: Nothing = event.getPlayer
    if ((event.getAction eq Action.RIGHT_CLICK_BLOCK) && (isTool(player.getItemInHand))) {
      val middleBlock: Nothing = event.getClickedBlock
      if (middleBlock == null) {
        return
      }
      if (isCompass(middleBlock)) {
        makeCompass(middleBlock)
        return
      }
      if (isWayPointShaped(middleBlock)) {
        val signature: Runecraft_Teles.Signature = Signature.readFromWayPoint(middleBlock)
        if (!signature.isValidSignature) {
          player.sendMessage(ChatColor.RED + "Invalid signature.")
          return
        }
        val loc: Nothing = wayPoints.get(signature)
        if (loc == null) {
          player.sendMessage(ChatColor.YELLOW + "Waypoint accepted.")
          if (middleBlock.getLocation == null) {
            System.out.println("bad block, find fix")
          }
          wayPoints.put(signature, middleBlock.getLocation)
        }
        else if (middleBlock.getLocation.equals(loc)) {
          player.sendMessage(ChatColor.RED + "Waypoint already established.")
        }
        else if ((isWayPointShaped(loc.getBlock)) && (Signature.readFromWayPoint(loc.getBlock) == signature)) {
          player.sendMessage(ChatColor.RED + "Another Waypoint already uses this signature.")
        }
        else {
          player.sendMessage(ChatColor.GREEN + "Waypoint accepted.")
          wayPoints.put(signature, middleBlock.getLocation)
        }
      }
      else if (isTeleShaped(middleBlock)) {
        val signature: Runecraft_Teles.Signature = Signature.readFromTele(middleBlock)
        val loc: Nothing = wayPoints.get(signature)
        val WPsig: Runecraft_Teles.Signature = teles.get(middleBlock.getLocation)
        val WPloc: Nothing = wayPoints.get(WPsig)
        if (!signature.isValidSignature) {
          if (WPloc != null) {
            val airloc: Nothing = findAir(WPloc)
            if ((isWayPointShaped(WPloc.getBlock)) && (Signature.readFromWayPoint(WPloc.getBlock) == WPsig) && (airloc != null)) {
              player.teleport(airloc.add(0.5D, 0.0D, 0.5D))
              player.sendMessage(ChatColor.YELLOW + "Teleporter used.")
            }
            else {
              player.sendMessage(ChatColor.RED + "Your way has been barred from the other side.")
            }
          }
          else {
            player.sendMessage(ChatColor.RED + "This tele doesn't go anywhere!")
          }
        }
        else if (loc != null) {
          teles.put(middleBlock.getLocation, signature)
          middleBlock.getWorld.getBlockAt(new Nothing(middleBlock.getWorld, middleBlock.getX + 2, middleBlock.getY, middleBlock.getZ)).setType(Material.AIR)
          middleBlock.getWorld.getBlockAt(new Nothing(middleBlock.getWorld, middleBlock.getX + 2, middleBlock.getY, middleBlock.getZ)).setData(0.toByte)
          middleBlock.getWorld.getBlockAt(new Nothing(middleBlock.getWorld, middleBlock.getX - 2, middleBlock.getY, middleBlock.getZ)).setType(Material.AIR)
          middleBlock.getWorld.getBlockAt(new Nothing(middleBlock.getWorld, middleBlock.getX - 2, middleBlock.getY, middleBlock.getZ)).setData(0.toByte)
          middleBlock.getWorld.getBlockAt(new Nothing(middleBlock.getWorld, middleBlock.getX, middleBlock.getY, middleBlock.getZ + 2)).setType(Material.AIR)
          middleBlock.getWorld.getBlockAt(new Nothing(middleBlock.getWorld, middleBlock.getX, middleBlock.getY, middleBlock.getZ + 2)).setData(0.toByte)
          middleBlock.getWorld.getBlockAt(new Nothing(middleBlock.getWorld, middleBlock.getX, middleBlock.getY, middleBlock.getZ - 2)).setType(Material.AIR)
          middleBlock.getWorld.getBlockAt(new Nothing(middleBlock.getWorld, middleBlock.getX, middleBlock.getY, middleBlock.getZ - 2)).setData(0.toByte)
          player.sendMessage(ChatColor.GREEN + "Teleporter accepted.")
        }
        else {
          player.sendMessage(ChatColor.RED + "Signature not recognized.")
        }
      }
    }
  }

  private def findAir(wPloc: Nothing): Nothing = {
    val find: Nothing = new Nothing(wPloc.getWorld, wPloc.getX, wPloc.getY + 2.0D, wPloc.getZ)
    var y: Int = find.getY.asInstanceOf[Int]
    while (y < 256) {
      {
        if ((find.getBlock.getType.equals(Material.AIR)) && (find.getBlock.getRelative(BlockFace.DOWN).getType.equals(Material.AIR))) {
          return find.getBlock.getRelative(BlockFace.DOWN).getLocation
        }
        y += 2
        find.add(0.0D, 2.0D, 0.0D)
      }
    }
    return null
  }

  private def isTool(item: Nothing): Boolean = {
    if (item == null) {
      return true
    }
    val mat: Nothing = item.getType
    if (mat.equals(Material.AIR)) {
      return true
    }
    if ((mat.equals(Material.WOOD_AXE)) || (mat.equals(Material.WOOD_HOE)) || (mat.equals(Material.WOOD_PICKAXE)) || (mat.equals(Material.WOOD_SPADE)) || (mat.equals(Material.WOOD_SWORD))) {
      return true
    }
    if ((mat.equals(Material.STONE_AXE)) || (mat.equals(Material.STONE_HOE)) || (mat.equals(Material.STONE_PICKAXE)) || (mat.equals(Material.STONE_SPADE)) || (mat.equals(Material.STONE_SWORD))) {
      return true
    }
    if ((mat.equals(Material.GOLD_AXE)) || (mat.equals(Material.GOLD_HOE)) || (mat.equals(Material.GOLD_PICKAXE)) || (mat.equals(Material.GOLD_SPADE)) || (mat.equals(Material.GOLD_SWORD))) {
      return true
    }
    if ((mat.equals(Material.IRON_AXE)) || (mat.equals(Material.IRON_HOE)) || (mat.equals(Material.IRON_PICKAXE)) || (mat.equals(Material.IRON_SPADE)) || (mat.equals(Material.IRON_SWORD))) {
      return true
    }
    if ((mat.equals(Material.DIAMOND_AXE)) || (mat.equals(Material.DIAMOND_HOE)) || (mat.equals(Material.DIAMOND_PICKAXE)) || (mat.equals(Material.DIAMOND_SPADE)) || (mat.equals(Material.DIAMOND_SWORD))) {
      return true
    }
    return false
  }

  private def isTeleShaped(midBlock: Nothing): Boolean = {
    val loc: Nothing = midBlock.getLocation
    val x: Double = loc.getX
    val y: Double = loc.getY
    val z: Double = loc.getZ
    val world: Nothing = loc.getWorld
    var temp: Nothing = new Nothing(world, x + 1.0D, y, z + 1.0D)
    var block: Nothing = temp.getBlock
    val frameType: Nothing = block.getType
    if ((frameType.equals(midBlock.getType)) || (airBlocks.contains(frameType))) {
      return false
    }
    var i: Int = -2
    while (i <= 2) {
      {
        temp = new Nothing(world, x + i, y, z + 1.0D)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Nothing(world, x + i, y, z - 1.0D)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
      }
      ({
        i += 1; i - 1
      })
    }
    var i: Int = -2
    while (i <= 2) {
      {
        temp = new Nothing(world, x + 1.0D, y, z + i)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Nothing(world, x - 1.0D, y, z + i)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
      }
      i += 2
    }
    if (!airBlocks.contains(new Nothing(world, x + 2.0D, y, z + 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Nothing(world, x + 2.0D, y, z - 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Nothing(world, x - 2.0D, y, z + 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Nothing(world, x - 2.0D, y, z - 2.0D).getBlock.getType)) {
      return false
    }
    return true
  }

  private def isWayPointShaped(midBlock: Nothing): Boolean = {
    val loc: Nothing = midBlock.getLocation
    val x: Double = loc.getX
    val y: Double = loc.getY
    val z: Double = loc.getZ
    val world: Nothing = loc.getWorld
    val frameType: Nothing = midBlock.getType
    if (airBlocks.contains(frameType)) {
      return false
    }
    var i: Int = -1
    while (i <= 1) {
      {
        var temp: Nothing = new Nothing(world, x + i, y, z + 2.0D)
        var block: Nothing = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Nothing(world, x + i, y, z - 2.0D)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Nothing(world, x + 2.0D, y, z + i)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Nothing(world, x - 2.0D, y, z + i)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
      }
      ({
        i += 1; i - 1
      })
    }
    if (!frameType.equals(new Nothing(world, x + 1.0D, y, z + 1.0D).getBlock.getType)) {
      return false
    }
    if (!frameType.equals(new Nothing(world, x + 1.0D, y, z - 1.0D).getBlock.getType)) {
      return false
    }
    if (!frameType.equals(new Nothing(world, x - 1.0D, y, z + 1.0D).getBlock.getType)) {
      return false
    }
    if (!frameType.equals(new Nothing(world, x - 1.0D, y, z - 1.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Nothing(world, x + 2.0D, y, z + 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Nothing(world, x + 2.0D, y, z - 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Nothing(world, x - 2.0D, y, z + 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Nothing(world, x - 2.0D, y, z - 2.0D).getBlock.getType)) {
      return false
    }
    if (frameType.equals(new Nothing(world, x + 1.0D, y, z).getBlock.getType)) {
      return false
    }
    if (frameType.equals(new Nothing(world, x + 1.0D, y, z).getBlock.getType)) {
      return false
    }
    if (frameType.equals(new Nothing(world, x, y, z + 1.0D).getBlock.getType)) {
      return false
    }
    if (frameType.equals(new Nothing(world, x, y, z - 1.0D).getBlock.getType)) {
      return false
    }
    return true
  }

  private object Signature {
    @SuppressWarnings(Array("deprecation")) private def readFromWayPoint(midBlock: Nothing): Runecraft_Teles.Signature = {
      val toRet: Runecraft_Teles.Signature = new Runecraft_Teles.Signature
      var temp: Nothing = midBlock.getRelative(BlockFace.NORTH)
      toRet.north = temp.getType
      toRet.northD = temp.getData
      temp = midBlock.getRelative(BlockFace.SOUTH)
      toRet.south = temp.getType
      toRet.southD = temp.getData
      temp = midBlock.getRelative(BlockFace.EAST)
      toRet.east = temp.getType
      toRet.eastD = temp.getData
      temp = midBlock.getRelative(BlockFace.WEST)
      toRet.west = temp.getType
      toRet.westD = temp.getData
      return toRet
    }

    @SuppressWarnings(Array("deprecation")) private def readFromTele(midBlock: Nothing): Runecraft_Teles.Signature = {
      val toRet: Runecraft_Teles.Signature = new Runecraft_Teles.Signature
      var temp: Nothing = midBlock.getRelative(BlockFace.NORTH, 2)
      toRet.north = temp.getType
      toRet.northD = temp.getData
      temp = midBlock.getRelative(BlockFace.SOUTH, 2)
      toRet.south = temp.getType
      toRet.southD = temp.getData
      temp = midBlock.getRelative(BlockFace.EAST, 2)
      toRet.east = temp.getType
      toRet.eastD = temp.getData
      temp = midBlock.getRelative(BlockFace.WEST, 2)
      toRet.west = temp.getType
      toRet.westD = temp.getData
      return toRet
    }
  }

  private class Signature {
    private[kadmin] var north: Nothing = null
    private[kadmin] var south: Nothing = null
    private[kadmin] var east: Nothing = null
    private[kadmin] var west: Nothing = null
    private[kadmin] var northD: Byte = 0
    private[kadmin] var southD: Byte = 0
    private[kadmin] var eastD: Byte = 0
    private[kadmin] var westD: Byte = 0

    private def isValidSignature: Boolean = {
      if ((Runecraft_Teles.airBlocks.contains(this.north)) && (Runecraft_Teles.airBlocks.contains(this.south)) && (Runecraft_Teles.airBlocks.contains(this.east)) && (Runecraft_Teles.airBlocks.contains(this.west))) {
        return false
      }
      return true
    }

    @SuppressWarnings(Array("deprecation")) override def toString: String = {
      val s: StringBuilder = new StringBuilder
      s.append(this.north.getId + "|")
      s.append(this.south.getId + "|")
      s.append(this.east.getId + "|")
      s.append(this.west.getId + "|")
      s.append(this.northD + "|")
      s.append(this.southD + "|")
      s.append(this.eastD + "|")
      s.append(this.westD)
      return s.toString
    }

    override def equals(o: Any): Boolean = {
      val s: Runecraft_Teles.Signature = o.asInstanceOf[Runecraft_Teles.Signature]
      if ((!this.north.equals(s.north)) || (!this.south.equals(s.south)) || (!this.east.equals(s.east)) || (!this.west.equals(s.west))) {
        return false
      }
      if ((this.northD != s.northD) || (this.southD != s.southD) || (this.eastD != s.eastD) || (this.westD != s.westD)) {
        return false
      }
      return true
    }

    @SuppressWarnings(Array("deprecation")) override def hashCode: Int = {
      val numBlocks: Int = 255
      var code: Int = this.north.getId
      code *= numBlocks
      code += this.south.getId
      code *= numBlocks
      code += this.east.getId
      code *= numBlocks
      code += this.west.getId
      code += this.northD
      code += this.southD
      code += this.eastD
      code += this.westD
      return code
    }
  }

  def writeToFiles {
    log.info("Writing to Waypoint and Teleports files...")
    writeWayPointFile("kim_waypoints.txt")
    writeTeleFile("kim_teles.txt")
    log.info("Waypoint and Tele files written")
  }

  private def writeWayPointFile(filename: String) {
    val sb: StringBuilder = new StringBuilder
    import scala.collection.JavaConversions._
    for (entry <- wayPoints.entrySet) {
      sb.append(entry.getKey.toString)
      sb.append("|")
      sb.append(locFormat(entry.getValue))
      sb.append('\n')
    }
    sb.setLength(sb.length - 1)
    var writer: PrintWriter = null
    try {
      writer = new PrintWriter(Kadmin.dFolder + "/" + filename, "UTF-8")
      writer.println(sb.toString)
      writer.close
    }
    catch {
      case e: Any => {
        Bukkit.getLogger.severe("Could not find waypoint file")
      }
    }
  }

  private def writeTeleFile(filename: String) {
    val sb: StringBuilder = new StringBuilder
    import scala.collection.JavaConversions._
    for (entry <- teles.entrySet) {
      sb.append(entry.getValue.toString)
      sb.append("|")
      sb.append(locFormat(entry.getKey))
      sb.append('\n')
    }
    sb.setLength(sb.length - 1)
    var writer: PrintWriter = null
    try {
      writer = new PrintWriter(Kadmin.dFolder + "/" + filename, "UTF-8")
      writer.println(sb.toString)
      writer.close
    }
    catch {
      case e: Any => {
        Bukkit.getLogger.severe("Could not find tele file")
      }
    }
  }

  private def locFormat(loc: Nothing): String = {
    val s: StringBuilder = new StringBuilder
    s.append(loc.getWorld.getName + "|")
    s.append(loc.getX + "|")
    s.append(loc.getY + "|")
    s.append(loc.getZ)
    return s.toString
  }

  private def readFromFiles {
    wayPoints = new util.HashMap[Runecraft_Teles.Signature, Nothing]
    teles = new util.HashMap[Nothing, Runecraft_Teles.Signature]
    try {
      readFromWayPointFile("kim_waypoints.txt")
    }
    catch {
      case e: FileNotFoundException => {
        Bukkit.getLogger.severe("Could not load waypoint file!")
      }
      case e: IOException => {
        Bukkit.getLogger.severe("Problem loading waypoints")
      }
    }
    try {
      readFromTeleFile("kim_teles.txt")
    }
    catch {
      case e: FileNotFoundException => {
        Bukkit.getLogger.severe("Could not load tele file!")
      }
      case e: IOException => {
        Bukkit.getLogger.severe("Problem loading teles")
      }
    }
  }

  @throws[IOException]
  private def readFromWayPointFile(filename: String) {
    try {
      val br: BufferedReader = new BufferedReader(new FileReader(Kadmin.dFolder + "/" + filename))
      var line: String = null
      while ((line = br.readLine) != null) {
        {
          addToHash(line, true)
        }
      }
      br.close
    }
    catch {
      case e: FileNotFoundException => {
        Bukkit.getLogger.severe("Unable to load waypoint file!")
      }
    }
  }

  @throws[IOException]
  private def readFromTeleFile(filename: String) {
    try {
      val br: BufferedReader = new BufferedReader(new FileReader(Kadmin.dFolder + "/" + filename))
      var line: String = null
      while ((line = br.readLine) != null) {
        {
          addToHash(line, false)
        }
      }
      br.close
    }
    catch {
      case e: FileNotFoundException => {
        Bukkit.getLogger.severe("Unable to load tele file!")
      }
    }
  }

  @SuppressWarnings(Array("deprecation")) private def addToHash(line: String, isWayPoint: Boolean) {
    val pieces: Array[String] = line.split("\\|")
    val s: Runecraft_Teles.Signature = new Runecraft_Teles.Signature
    s.north = Material.getMaterial(pieces(0).toInt)
    s.south = Material.getMaterial(pieces(1).toInt)
    s.east = Material.getMaterial(pieces(2).toInt)
    s.west = Material.getMaterial(pieces(3).toInt)
    s.northD = Byte.parseByte(pieces(4))
    s.southD = Byte.parseByte(pieces(5))
    s.eastD = Byte.parseByte(pieces(6))
    s.westD = Byte.parseByte(pieces(7))
    val l: Nothing = new Nothing(Bukkit.getServer.getWorld(pieces(8)), pieces(9).toDouble, pieces(10).toDouble, pieces(11).toDouble)
    if (isWayPoint) {
      wayPoints.put(s, l)
    }
    else {
      teles.put(l, s)
    }
  }

  def isCompass(midBlock: Nothing): Boolean = {
    if ((midBlock.getType.equals(Material.COBBLESTONE)) && (midBlock.getRelative(BlockFace.NORTH_EAST).getType.equals(Material.COBBLESTONE)) && (midBlock.getRelative(BlockFace.SOUTH_EAST).getType.equals(Material.COBBLESTONE)) && (midBlock.getRelative(BlockFace.NORTH_WEST).getType.equals(Material.COBBLESTONE)) && (midBlock.getRelative(BlockFace.SOUTH_WEST).getType.equals(Material.COBBLESTONE)) && (midBlock.getRelative(BlockFace.NORTH).getType.equals(Material.AIR)) && (midBlock.getRelative(BlockFace.SOUTH).getType.equals(Material.AIR)) && (midBlock.getRelative(BlockFace.EAST).getType.equals(Material.AIR)) && (midBlock.getRelative(BlockFace.WEST).getType.equals(Material.AIR))) {
      return true
    }
    return false
  }

  def makeCompass(midBlock: Nothing) {
    midBlock.getRelative(BlockFace.NORTH_EAST).setType(Material.AIR)
    midBlock.getRelative(BlockFace.NORTH).setType(Material.COBBLESTONE)
    midBlock.getRelative(BlockFace.NORTH_WEST).setType(Material.AIR)
    midBlock.getRelative(BlockFace.EAST).setType(Material.COBBLESTONE)
    midBlock.setType(Material.AIR)
    midBlock.getRelative(BlockFace.WEST).setType(Material.COBBLESTONE)
  }
}
