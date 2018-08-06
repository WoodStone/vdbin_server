package services

import javax.inject.Singleton

trait Id {
  def nextId(): String
}

//TODO placeholder generator
@Singleton
class IdGenerator extends Id {
  var i: Long = System.nanoTime()

  override def nextId(): String = {
    i ^= (i << 21)
    i ^= (i >>> 35)
    i ^= (i << 24)
    return i.toHexString
  }

}
