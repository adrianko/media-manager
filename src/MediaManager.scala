import org.json.simple._
import org.json.simple.parser._

import scala.io.Source._

import java.io.File

/**
 * MediaManager class
 * TODO Check can connect to service
 * TODO Process files to be kept
 * TODO SD files move to sync
 * TODO HD files straight to library
 */
object MediaManager extends Base {

    /**
     * if Windows
     */
    val os = System.getProperty("os.name").contains("Windows")

    /**
     * Path to class
     */
    val basePath: String = (getClass.getResource(".").getPath + "../../../").drop(if (os) 1 else 0)

    /**
     * configuration settings
     */
    val config: Map[String, String] = Map(fromFile(basePath + "conf/config").getLines().map(_.replace("\n", "").split("="))
        .map(line => line(0).trim -> line(1).trim).toList: _*)

    /**
     * ut settings / credentials
     */
    val ut: Map[String, String] = Map[String, String](
        "user" -> "root",
        "pass" -> ex(config.get("password")),
        "win_host" -> "localhost",
        "other_host" -> ex(config.get("ip"))
    )

    /**
     * completion folder
     */
    val sourceDir: String = ex(config.get("video_dir"))

    /**
     * list of files to keep
     */
    val keepList: String = basePath + ex(config.get("keep_list"))

    /**
     * extract map options
     * @param x Some/None
     * @return
     */
    def ex(x: Option[String]) = x match {
        case Some(s) => s
        case None => ""
    }

    /**
     * Use external application to rename file according to standards
     * @param file the file
     */
    def rename(file: File) = ()

    /**
     * Check whether is a file and if has particular extension
     * @param f File
     * @return
     */
    def isVideoFile(f: File): Boolean = (f.getName.contains(".mp4") || f.getName.contains(".mkv")) && f.isFile

    /**
     * Process list of files and pull out those to keep
     * @param files - the file list
     * @param keepList - the list that we want
     * @return
     */
    def keepFile(files: List[File], keepList: Map[String, Int]): Set[File] = {
        var processing: collection.mutable.Set[File] = collection.mutable.Set[File]()
        
        files.foreach { f: File =>
            
            // match files in keep list to files found in directory
            keepList.keys.foreach { t =>
                
                if (f.getName.contains(t) && isVideoFile(f)) {
                    processing += f
                } else if (f.isDirectory) {
                    processing ++= keepFile(f.listFiles.toList, keepList)
                }
                // otherwise ignore completely
            }
        }
        
        processing.toSet
    }

    def main(args: Array[String]) {
        val torrents: JSONArray = new JSONParser().parse(Downloader.getStatus).asInstanceOf[JSONObject].get("torrents")
            .asInstanceOf[JSONArray]

        //JSON array doesn't support foreach. Maybe use an iterator?
        for (i: Int <- 0 to (torrents.size() - 1)) {
            val t = torrents.get(i).asInstanceOf[JSONArray]

            if (t.get(21).toString.equals(Downloader.seedingMessage)) {
                Downloader.clearSeed(t.get(0).toString)
            }
        }

        if (!os) {
            System.exit(0)
        }

        val processing: Set[File] = keepFile(new File(sourceDir).listFiles.toList, 
            Map(fromFile(keepList).getLines().map(_.replace("\n", "").split(",")).map(line => line(0).trim -> line(1)
                .trim.toInt).toList: _*)
        )
        
        processing.foreach { f => println(f.getAbsoluteFile) }
    }

}