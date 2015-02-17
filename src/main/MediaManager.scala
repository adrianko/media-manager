package main

import java.io.File

import org.json.simple._
import org.json.simple.parser._

import scala.io.Source._

/**
 * MediaManager class
 * TODO Check can connect to service
 * TODO Process files to be kept
 * TODO SD files move to sync
 * TODO HD files straight to library
 */
object MediaManager extends Base {
    
    val basePath: String = (getClass.getResource(".").getPath + "../../../").drop(1)
    
    val config: Map[String, String] = Map(fromFile(basePath + "conf/config").getLines().map(_.replace("\n", "").split("="))
            .map(line => line(0).trim -> line(1).trim).toList: _*)
    
    val sourceDir: String = ex(config.get("video_dir"))
    
    val keepList: String = basePath + ex(config.get("keep_list"))

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

        val processing: Set[File] = Manager.processFolder(new File(sourceDir).listFiles.toList,
            Map(fromFile(keepList).getLines().map(_.replace("\n", "").split(",")).map(line => line(0).trim -> line(1)
                    .trim.toInt).toList: _*)
        )

        processing.foreach { f => println(f.getAbsoluteFile) }
    }

}