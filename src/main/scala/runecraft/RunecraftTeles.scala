package runecraft

import java.io._
import java.util.logging.Logger

import org.bukkit.{Bukkit, ChatColor, Location, Material}
import org.bukkit.block.{Block, BlockFace}
import org.bukkit.event.{EventHandler, Listener}
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class RunecraftTeles(dFolder: File) extends Listener {
  var wayPoints: Map[Signature, Location] = Map()
  var teles: Map[Location, Signature] = Map()
  val log: Logger = Bukkit.getLogger
  val airBlocks = Set(Material.AIR, Material.DIRT, Material.GRASS, Material.SAND, Material.STONE, Material.MYCEL, Material.DOUBLE_PLANT, Material.LONG_GRASS)

  log.info("Reading Waypoints and Teleports files...")
  readFromFiles()
  log.info("Loaded " + wayPoints.size + " waypoints.")
  log.info("Loaded " + teles.size + " teles.")

  @SuppressWarnings(Array("deprecation"))
  @EventHandler(ignoreCancelled = true) def rightClickTele(event: PlayerInteractEvent) {
    val player = event.getPlayer
    if ((event.getAction eq Action.RIGHT_CLICK_BLOCK) && isTool(player.getItemInHand)) {
      val middleBlock = event.getClickedBlock
      println(middleBlock)
      if (middleBlock == null) {
        return
      }
      if (isCompass(middleBlock)) {
        makeCompass(middleBlock)
        return
      }
      if (isWayPointShaped(middleBlock)) {
        println(middleBlock)
        val signature = Signature.readFromWayPoint(middleBlock)
        if (!signature.isValidSignature) {
          println("bad sig")
          player.sendMessage(ChatColor.RED + "Invalid signature.")
          return
        }
        println("fine")
        val loc = wayPoints.get(signature)
        if (loc.isEmpty) {
          player.sendMessage(ChatColor.YELLOW + "Waypoint accepted.")
          if (middleBlock.getLocation == null) {
            System.out.println("bad block, find fix")
          }
          wayPoints = wayPoints + (signature -> middleBlock.getLocation)
        }
        else if (middleBlock.getLocation.equals(loc.get)) {
          player.sendMessage(ChatColor.RED + "Waypoint already established.")
        }
        else if (isWayPointShaped(loc.get.getBlock) && Signature.readFromWayPoint(loc.get.getBlock) == signature) {
          player.sendMessage(ChatColor.RED + "Another Waypoint already uses this signature.")
        }
        else {
          player.sendMessage(ChatColor.GREEN + "Waypoint accepted.")
          wayPoints = wayPoints + (signature -> middleBlock.getLocation)
        }
      }
      else if (isTeleShaped(middleBlock)) {
        val signature = Signature.readFromTele(middleBlock)
        val loc = wayPoints.get(signature)
        if (!signature.isValidSignature) {
          val WPsig = teles.get(middleBlock.getLocation)
          println(WPsig)
          val WPloc = wayPoints.get(WPsig.get)
          println(WPloc)
          if (WPloc != null) {
            val airloc = findAir(WPloc.get)
            if (isWayPointShaped(WPloc.get.getBlock) && Signature.readFromWayPoint(WPloc.get.getBlock) == WPsig.get && (airloc != null)) {
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
          teles = teles + (middleBlock.getLocation -> signature)
          middleBlock.getWorld.getBlockAt(new Location(middleBlock.getWorld, middleBlock.getX + 2, middleBlock.getY, middleBlock.getZ)).setType(Material.AIR)
          middleBlock.getWorld.getBlockAt(new Location(middleBlock.getWorld, middleBlock.getX + 2, middleBlock.getY, middleBlock.getZ)).setData(0.toByte)
          middleBlock.getWorld.getBlockAt(new Location(middleBlock.getWorld, middleBlock.getX - 2, middleBlock.getY, middleBlock.getZ)).setType(Material.AIR)
          middleBlock.getWorld.getBlockAt(new Location(middleBlock.getWorld, middleBlock.getX - 2, middleBlock.getY, middleBlock.getZ)).setData(0.toByte)
          middleBlock.getWorld.getBlockAt(new Location(middleBlock.getWorld, middleBlock.getX, middleBlock.getY, middleBlock.getZ + 2)).setType(Material.AIR)
          middleBlock.getWorld.getBlockAt(new Location(middleBlock.getWorld, middleBlock.getX, middleBlock.getY, middleBlock.getZ + 2)).setData(0.toByte)
          middleBlock.getWorld.getBlockAt(new Location(middleBlock.getWorld, middleBlock.getX, middleBlock.getY, middleBlock.getZ - 2)).setType(Material.AIR)
          middleBlock.getWorld.getBlockAt(new Location(middleBlock.getWorld, middleBlock.getX, middleBlock.getY, middleBlock.getZ - 2)).setData(0.toByte)
          player.sendMessage(ChatColor.GREEN + "Teleporter accepted.")
        }
        else {
          player.sendMessage(ChatColor.RED + "Signature not recognized.")
        }
      }
    }
  }

  private def findAir(wPloc: Location): Location = {
    val find = new Location(wPloc.getWorld, wPloc.getX, wPloc.getY + 2.0D, wPloc.getZ)
    var y = find.getY.asInstanceOf[Int]
    while (y < 256) {
      {
        if (find.getBlock.getType.equals(Material.AIR) && find.getBlock.getRelative(BlockFace.DOWN).getType.equals(Material.AIR)) {
          return find.getBlock.getRelative(BlockFace.DOWN).getLocation
        }
        y += 2
        find.add(0.0D, 2.0D, 0.0D)
      }
    }
    null
  }

  private def isTool(item: ItemStack): Boolean = {
    if (item == null) {
      return true
    }
    val mat = item.getType
    if (mat.equals(Material.AIR)) {
      return true
    }
    if (mat.equals(Material.WOOD_AXE) || mat.equals(Material.WOOD_HOE) || mat.equals(Material.WOOD_PICKAXE) || mat.equals(Material.WOOD_SPADE) || mat.equals(Material.WOOD_SWORD)) {
      return true
    }
    if (mat.equals(Material.STONE_AXE) || mat.equals(Material.STONE_HOE) || mat.equals(Material.STONE_PICKAXE) || mat.equals(Material.STONE_SPADE) || mat.equals(Material.STONE_SWORD)) {
      return true
    }
    if (mat.equals(Material.GOLD_AXE) || mat.equals(Material.GOLD_HOE) || mat.equals(Material.GOLD_PICKAXE) || mat.equals(Material.GOLD_SPADE) || mat.equals(Material.GOLD_SWORD)) {
      return true
    }
    if (mat.equals(Material.IRON_AXE) || mat.equals(Material.IRON_HOE) || mat.equals(Material.IRON_PICKAXE) || mat.equals(Material.IRON_SPADE) || mat.equals(Material.IRON_SWORD)) {
      return true
    }
    if (mat.equals(Material.DIAMOND_AXE) || mat.equals(Material.DIAMOND_HOE) || mat.equals(Material.DIAMOND_PICKAXE) || mat.equals(Material.DIAMOND_SPADE) || mat.equals(Material.DIAMOND_SWORD)) {
      return true
    }
    false
  }

  private def isTeleShaped(midBlock: Block): Boolean = {
    val loc = midBlock.getLocation
    val x = loc.getX
    val y = loc.getY
    val z = loc.getZ
    val world = loc.getWorld
    var temp = new Location(world, x + 1.0D, y, z + 1.0D)
    var block = temp.getBlock
    val frameType = block.getType
    if (frameType.equals(midBlock.getType) || airBlocks.contains(frameType)) {
      return false
    }
    (-2 to 2).foreach {
      i =>
        temp = new Location(world, x + i, y, z + 1.0D)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Location(world, x + i, y, z - 1.0D)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
    }
    (-2 to 2 by 2).foreach {
      i =>
        temp = new Location(world, x + 1.0D, y, z + i)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Location(world, x - 1.0D, y, z + i)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
    }
    if (!airBlocks.contains(new Location(world, x + 2.0D, y, z + 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Location(world, x + 2.0D, y, z - 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Location(world, x - 2.0D, y, z + 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Location(world, x - 2.0D, y, z - 2.0D).getBlock.getType)) {
      return false
    }
    true
  }

  private def isWayPointShaped(midBlock: Block): Boolean = {
    val loc = midBlock.getLocation
    val x = loc.getX
    val y = loc.getY
    val z = loc.getZ
    val world = loc.getWorld
    val frameType = midBlock.getType
    if (airBlocks.contains(frameType)) {
      return false
    }
    (-1 to 1).foreach {
      i =>
        var temp = new Location(world, x + i, y, z + 2.0D)
        var block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Location(world, x + i, y, z - 2.0D)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Location(world, x + 2.0D, y, z + i)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
        temp = new Location(world, x - 2.0D, y, z + i)
        block = temp.getBlock
        if (!block.getType.equals(frameType)) {
          return false
        }
    }
    if (!frameType.equals(new Location(world, x + 1.0D, y, z + 1.0D).getBlock.getType)) {
      return false
    }
    if (!frameType.equals(new Location(world, x + 1.0D, y, z - 1.0D).getBlock.getType)) {
      return false
    }
    if (!frameType.equals(new Location(world, x - 1.0D, y, z + 1.0D).getBlock.getType)) {
      return false
    }
    if (!frameType.equals(new Location(world, x - 1.0D, y, z - 1.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Location(world, x + 2.0D, y, z + 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Location(world, x + 2.0D, y, z - 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Location(world, x - 2.0D, y, z + 2.0D).getBlock.getType)) {
      return false
    }
    if (!airBlocks.contains(new Location(world, x - 2.0D, y, z - 2.0D).getBlock.getType)) {
      return false
    }
    if (frameType.equals(new Location(world, x + 1.0D, y, z).getBlock.getType)) {
      return false
    }
    if (frameType.equals(new Location(world, x + 1.0D, y, z).getBlock.getType)) {
      return false
    }
    if (frameType.equals(new Location(world, x, y, z + 1.0D).getBlock.getType)) {
      return false
    }
    if (frameType.equals(new Location(world, x, y, z - 1.0D).getBlock.getType)) {
      return false
    }
    true
  }

  private object Signature {
    @SuppressWarnings(Array("deprecation"))
    def readFromWayPoint(midBlock: Block): Signature = {
      val toRet = new Signature(airBlocks)
      var temp = midBlock.getRelative(BlockFace.NORTH)
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
      toRet
    }

    @SuppressWarnings(Array("deprecation"))
    def readFromTele(midBlock: Block): Signature = {
      val toRet = new Signature(airBlocks)
      var temp = midBlock.getRelative(BlockFace.NORTH, 2)
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
      toRet
    }
  }

  def writeToFiles() {
    log.info("Writing to Waypoint and Teleports files...")
    writeWayPointFile("kim_waypoints.txt")
    writeTeleFile("kim_teles.txt")
    log.info("Waypoint and Tele files written")
  }

  private def writeWayPointFile(filename: String) {
    val sb = new StringBuilder
    for((k,v) <- wayPoints){
      sb.append(k.toString)
      sb.append("|")
      sb.append(locFormat(v))
      sb.append('\r')
    }
    sb.setLength(sb.length - 1)
    var writer: PrintWriter = null
    try {
      writer = new PrintWriter(dFolder + "/" + filename, "UTF-8")
      writer.println(sb.toString)
      writer.close()
    }
    catch {
      case e: Any => Bukkit.getLogger.severe("Could not find waypoint file")
    }
  }

  private def writeTeleFile(filename: String) {
    val sb = new StringBuilder
    var count = 0
    for((k,v) <- teles){
      sb.append(v.toString)
      sb.append("|")
      sb.append(locFormat(k))
      if(count < teles.keys.size)
        sb.append('\n')
      count += 1
    }
    sb.setLength(sb.length - 1)
    var writer: PrintWriter = null
    try {
      writer = new PrintWriter(dFolder + "/" + filename, "UTF-8")
      writer.println(sb.toString)
      writer.close()
    }
    catch {
      case e: Any => Bukkit.getLogger.severe("Could not find tele file")
    }
  }

  private def locFormat(loc: Location): String = {
    val s = new StringBuilder
    s.append(loc.getWorld.getName + "|")
    s.append(loc.getX + "|")
    s.append(loc.getY + "|")
    s.append(loc.getZ)
    s.toString
  }

  private def readFromFiles() {
    wayPoints = Map()
    teles = Map()
    try {
      readFromWayPointFile("kim_waypoints.txt")
    }
    catch {
      case e: FileNotFoundException => Bukkit.getLogger.severe("Could not load waypoint file!")
      case e: IOException => Bukkit.getLogger.severe("Problem loading waypoints")
    }
    try {
      readFromTeleFile("kim_teles.txt")
    }
    catch {
      case e: FileNotFoundException => Bukkit.getLogger.severe("Could not load tele file!")
      case e: IOException => Bukkit.getLogger.severe("Problem loading teles")
    }
  }

  @throws[IOException]
  private def readFromWayPointFile(filename: String) {
    try {
      val br: BufferedReader = new BufferedReader(new FileReader(dFolder + "/" + filename))
      var line: String = null
      while ({ line = br.readLine() ; line != null } ) {
        addToHash(line, true)
      }
      br.close()
    }
    catch {
      case e: FileNotFoundException => Bukkit.getLogger.severe("Unable to load waypoint file!")
    }
  }

  @throws[IOException]
  private def readFromTeleFile(filename: String) {
    try {
      val br: BufferedReader = new BufferedReader(new FileReader(dFolder + "/" + filename))
      var line: String = null
      while ((line = br.readLine) != null) {
        addToHash(line, false)
      }
      br.close()
    }
    catch {
      case e: FileNotFoundException => Bukkit.getLogger.severe("Unable to load tele file!")
    }
  }

  @SuppressWarnings(Array("deprecation")) private def addToHash(line: String, isWayPoint: Boolean) {
    val pieces: Array[String] = line.split("\\|")
    val s = new Signature(airBlocks)
    println(pieces(0))
    println(pieces(1))
    println(pieces(2))
    println(pieces(3))
    println(pieces(4))
    println(pieces(5))
    println(pieces(6))
    println(pieces(7))
    s.north = Material.getMaterial(pieces(0).toInt)
    s.south = Material.getMaterial(pieces(1).toInt)
    s.east = Material.getMaterial(pieces(2).toInt)
    s.west = Material.getMaterial(pieces(3).toInt)
    s.northD = pieces(4).toByte
    s.southD = pieces(5).toByte
    s.eastD = pieces(6).toByte
    s.westD = pieces(7).toByte
    val l: Location = new Location(Bukkit.getServer.getWorld(pieces(8)), pieces(9).toDouble, pieces(10).toDouble, pieces(11).toDouble)
    if (isWayPoint) {
      wayPoints = wayPoints + (s -> l)
    }
    else {
      teles = teles + (l -> s)
    }
  }

  def isCompass(midBlock: Block): Boolean = {
    if (midBlock.getType.equals(Material.COBBLESTONE) && midBlock.getRelative(BlockFace.NORTH_EAST).getType.equals(Material.COBBLESTONE) && midBlock.getRelative(BlockFace.SOUTH_EAST).getType.equals(Material.COBBLESTONE) && midBlock.getRelative(BlockFace.NORTH_WEST).getType.equals(Material.COBBLESTONE) && midBlock.getRelative(BlockFace.SOUTH_WEST).getType.equals(Material.COBBLESTONE) && midBlock.getRelative(BlockFace.NORTH).getType.equals(Material.AIR) && midBlock.getRelative(BlockFace.SOUTH).getType.equals(Material.AIR) && midBlock.getRelative(BlockFace.EAST).getType.equals(Material.AIR) && midBlock.getRelative(BlockFace.WEST).getType.equals(Material.AIR)) {
      return true
    }
    false
  }

  def makeCompass(midBlock: Block) {
    midBlock.getRelative(BlockFace.NORTH_EAST).setType(Material.AIR)
    midBlock.getRelative(BlockFace.NORTH).setType(Material.COBBLESTONE)
    midBlock.getRelative(BlockFace.NORTH_WEST).setType(Material.AIR)
    midBlock.getRelative(BlockFace.EAST).setType(Material.COBBLESTONE)
    midBlock.setType(Material.AIR)
    midBlock.getRelative(BlockFace.WEST).setType(Material.COBBLESTONE)
  }
}
