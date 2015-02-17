package main

import java.io.InputStream
import java.net.{HttpURLConnection, URL}
import java.util.Base64

import scala.io.Source._

import main.MediaManager._

object Downloader extends Base {
    
    val seedingMessage: String = "Seeding 100.0 %"

    val ut: Map[String, String] = Map[String, String](
        "user" -> "root",
        "pass" -> ex(config.get("password")),
        "win_host" -> "127.0.0.1",
        "other_host" -> ex(config.get("ip"))
    )
    
    def getURL(params: String): String = "http://" + ex(ut.get("win_host")) + ":8080/gui/?" + params +
        "&list=1&cid=0&getmsg=1&t=" + System.currentTimeMillis
    
    def getStatus: String = download(getURL("1=1"), content = true)
    
    def sendAction(hash: String, action: String) = download(getURL("action=" + action + "&hash=" + hash), content = false)
    
    def download(url: String, content: Boolean): String = {
        val con: HttpURLConnection  = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
        con.setRequestMethod("GET")
        con.setRequestProperty("Authorization", "Basic " + new String(Base64.getEncoder.encode(
            (ex(ut.get("user")) + ":" + ex(ut.get("pass"))).getBytes))
        )

        if (!content) {
            con.getResponseCode.toString
        } else {
           fromInputStream(con.getContent.asInstanceOf[InputStream]).getLines().mkString
        }
    }

    def clearSeed(hash: String): Unit = {
        sendAction(hash, "stop")
        sendAction(hash, "remove")
    }

}