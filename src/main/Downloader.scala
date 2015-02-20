package main

import java.io.InputStream
import java.net.{HttpURLConnection, URL}
import java.util.Base64

import scala.io.Source.fromInputStream

object Downloader extends Base {
    
    val seedingMessage: List[String] = List("Seeding 100.0 %", "[F] Seeding 100.0 %")

    def getURL(params: String): String =
        "http://" + ex(MediaManager.settings.get("dl_host")) + ":" + ex(MediaManager.settings.get("dl_port")) + "/gui/?" +
            params + "&list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
    
    def getStatus: String = download(getURL("1=1"))
    
    def sendAction(hash: String, action: String) = download(getURL("action=" + action + "&hash=" + hash))
    
    def download(url: String): String = {
        val con: HttpURLConnection  = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
        con.setRequestMethod("GET")
        con.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder.encode(
            (ex(MediaManager.settings.get("dl_user")) + ":" + ex(MediaManager.settings.get("dl_pass"))).getBytes))
        )
        
        fromInputStream(con.getContent.asInstanceOf[InputStream]).getLines().mkString
    }

    def clearSeed(hash: String): Unit = {
        sendAction(hash, "stop")
        sendAction(hash, "remove")
    }

}