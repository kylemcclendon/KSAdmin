package runecraft

import org.bukkit.Material

class Signature(airBlocks: Set[Material]) {
  var north: Material = null
  var south: Material = null
  var east: Material = null
  var west: Material = null
  var northD: Byte = 0
  var southD: Byte = 0
  var eastD: Byte = 0
  var westD: Byte = 0

  def isValidSignature: Boolean = {
    if (airBlocks.contains(north) && airBlocks.contains(south) && airBlocks.contains(east) && airBlocks.contains(west)) {
      return false
    }
    true
  }

  @SuppressWarnings(Array("deprecation")) override def toString: String = {
    val s = new StringBuilder
    s.append(north.getId + "|")
    s.append(this.south.getId + "|")
    s.append(this.east.getId + "|")
    s.append(this.west.getId + "|")
    s.append(this.northD + "|")
    s.append(this.southD + "|")
    s.append(this.eastD + "|")
    s.append(this.westD)
    s.toString
  }

  override def equals(o: Any): Boolean = {
    val s: Signature = o.asInstanceOf[Signature]
    if ((!north.equals(s.north)) || (!south.equals(s.south)) || (!east.equals(s.east)) || (!west.equals(s.west))) {
      return false
    }
    if ((northD != s.northD) || (southD != s.southD) || (eastD != s.eastD) || (westD != s.westD)) {
      return false
    }
    true
  }

  @SuppressWarnings(Array("deprecation")) override def hashCode: Int = {
    val numBlocks: Int = 255
    var code = this.north.getId
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
    code
  }
}
