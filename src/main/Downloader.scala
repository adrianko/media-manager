package main

import java.io.InputStream
import java.net.{HttpURLConnection, URL}
import java.util.Base64

import scala.io.Source.fromInputStream
import org.json.simple.{JSONArray, JSONObject}
import org.json.simple.parser.JSONParser

object Downloader extends Base {
    
    val seedingMessage: List[String] = List("Seeding 100.0 %", "[F] Seeding 100.0 %")
    
    def complete(msg: String): Boolean = seedingMessage.contains(msg)

    def getURL(params: String): String =
        "http://" + ex(MediaManager.settings.get("dl_host")) + ":" + ex(MediaManager.settings.get("dl_port")) + "/gui/?" +
            params + "&list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
    
    def getStatus: String = download(getURL("1=1"))
    
    def sendAction(hash: String, action: String): String = download(getURL("action=" + action + "&hash=" + hash))
    
    def download(url: String): String = {
        val con: HttpURLConnection  = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
        con.setRequestMethod("GET")
        con.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder.encode(
            (ex(MediaManager.settings.get("dl_user")) + ":" + ex(MediaManager.settings.get("dl_pass"))).getBytes))
        )
        
        fromInputStream(con.getContent.asInstanceOf[InputStream]).getLines().mkString
    }

    def clear(hash: String): Unit = {
        sendAction(hash, "stop")
        sendAction(hash, "remove")
    }
    
    def getQueue: JSONArray = new JSONParser().parse(getStatus).asInstanceOf[JSONObject].get("torrents").asInstanceOf[JSONArray]
    
    def clearFinished(): Unit = {
        val queue: JSONArray = Downloader.getQueue
        
        for (i: Int <- 0 to (queue.size() - 1)) {
            val t = queue.get(i).asInstanceOf[JSONArray]

            if (Downloader.complete(t.get(21).toString)) {
                Downloader.clear(t.get(0).toString)
            }
        }
    }

}