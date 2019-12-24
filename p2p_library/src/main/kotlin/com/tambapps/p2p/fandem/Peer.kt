package com.tambapps.p2p.fandem

import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException

/**
 * Representation of peer
 */

data class Peer private constructor(val ip: InetAddress, val port: Int) {

  private val ipString: String
    get() {
      return ip.hostAddress.replace("/", "")
    }

  override fun toString(): String {
    return "$ipString:$port"
  }

  fun toHexString(): String {
    val ipHex = ipString.split(".").joinToString(prefix = "", postfix = "", separator = "") { s -> toHexString(s) }
    return if (port != DEFAULT_PORT) {
      ipHex + toHexString(port - DEFAULT_PORT)
    } else {
      ipHex
    }
  }

  private fun toHexString(s: String): String {
    return toHexString(s.toInt())
  }

  private fun toHexString(i: Int): String {
    val  n = i.toString(16)
    return if (n.length == 1) "0$n" else n
  }

  companion object {
    const val DEFAULT_PORT = 8081

    @JvmStatic
    fun of(address: InetAddress, port: Int): Peer {
      return Peer(address, port)
    }

    @JvmStatic
    fun of(socket: Socket): Peer {
      return Peer(socket.inetAddress, socket.port)
    }

    @JvmStatic
    fun fromHexString(hexString: String): Peer {
      val address = IntArray(4)
      for (i in address.indices) {
        address[i] = hexString.substring(i * 2, i * 2 + 2).toInt(16)
      }
      val port: Int =
          if(hexString.length == 10)
            DEFAULT_PORT + hexString.substring(8, 10).toInt(16)
          else
            DEFAULT_PORT
      return of(InetAddress.getByName(address.joinToString(prefix = "", separator = ".", postfix = "") { it.toString() }), port)
    }

    @JvmStatic
    @Throws(UnknownHostException::class)
    fun parse(peer: String): Peer {
      val index = peer.indexOf(":")
      require(index >= 0) { "peer is malformed" }
      return Peer(InetAddress.getByName(peer.substring(0, index)), peer.substring(index + 1).toInt())
    }

    @JvmStatic
    @Throws(UnknownHostException::class)
    fun of(address: String?, port: Int): Peer {
      return of(InetAddress.getByName(address), port)
    }

    @JvmStatic
    fun isCorrectPeerKey(peerKey: String?): Boolean {
      if (peerKey == null) {
        return false
      }
      val peerKey = peerKey.toUpperCase()
      if (peerKey.length != 8 && peerKey.length != 10) {
        return false
      }
      for (element in peerKey) {
        if (!(element in 'A'..'F' || element in '0'..'9')) {
          return false
        }
      }
      return true
    }
  }

}