package main

import java.io.File

import org.json.simple._
import org.json.simple.parser._

import scala.io.Source._

/**
 * MediaManager class
 * TODO Use SQLite DB instead of text config1 file
 * TODO Check can connect to service
 * TODO Process files to be kept
 * TODO SD files move to sync
 * TODO HD files straight to library
 */
object MediaManager extends Base {
    
    val basePath: String = new File(getClass.getResource(".").getFile).getAbsolutePath + "/../../../../"

    val settings: Map[String, String] = DB.loadSettings

    val sourceDir: String = ex(settings.get("video_dir"))

    def main(args: Array[String]) {
        checkOS()
        MediaManager()
    }
    
    def MediaManager(): Unit = {
        val queue: JSONArray = new JSONParser().parse(Downloader.getStatus).asInstanceOf[JSONObject].get("torrents")
                .asInstanceOf[JSONArray]

        //JSON array doesn't support foreach. Maybe use an iterator?
        for (i: Int <- 0 to (queue.size() - 1)) {
            val t = queue.get(i).asInstanceOf[JSONArray]

            if (t.get(21).toString.equals(Downloader.seedingMessage)) {
                Downloader.clearSeed(t.get(0).toString)
            }
        }

        Manager.processFolder(new File(sourceDir).listFiles.toList, DB.getKeepList).foreach { f => println(f.getAbsoluteFile) }
    }

}