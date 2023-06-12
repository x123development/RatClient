package net.x123dev.ratclient

import net.x123dev.ratclient.modules.*

var ismaster:Boolean = false

fun main(args: Array<String>){
    if(args.size==1&&args[0].equals("dungeonmaster69"))
        ismaster=true;
    if(ismaster)
        InputThread().start()
    ServerListener().start()
}

class InputThread : Thread(){

    override fun run() {
        while(true){
            val input = readLine()
            if(input?.indexOf(' ')==-1){
                when(input){
                    "stop"->System.exit(0)
                    else -> println("Command not recognized")
                }
            }else{
                when(input?.substring(0,input.indexOf(' '))){
                    else -> println("Command not recognized")
                }
            }

        }
    }
}

fun handleIncoming(rec:String){
    if(rec.indexOf(';')==-1)
        println("received unknown command from server")
    else when(rec.substring(0,rec.indexOf(';'))){
        "ping" ->{}
        "browse"->{
            if(rec.indexOf('[')!=-1&&rec.indexOf(']')!=-1){
                println("received valid browse instruction from server: $rec")
                browseWebsite(""+rec.substring(rec.indexOf('[')+1,rec.indexOf(']')))
            }
        }
        "image"->{
            if(rec.indexOf('[')!=-1&&rec.indexOf(']')!=-1) {
                println("received valid image instruction from server: $rec")
                var image = ""+rec.substring(rec.indexOf('[')+1,rec.indexOf(']'))
                if(image.startsWith("URL="))
                    fullScreenImageFromURL(image.substring(4))
                else
                    fullScreenImage(image)
            }
        }
        "msg"->{
            if(rec.indexOf('[')!=-1&&rec.indexOf(']')!=-1){
                println("received valid msg instruction from server: $rec")
                displayInfoDialog(""+rec.substring(rec.indexOf('[')+1,rec.indexOf(']')))
            }
        }
        else -> println("received unknown command from server")
    }
}
