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
    
    val basePath: String = (getClass.getResource(".").getPath + "../../../").drop(1)
    
    val config: Map[String, String] = Map(fromFile(basePath + "conf/config").getLines().map(_.replace("\n", "").split("="))
            .map(line => line(0).trim -> line(1).trim).toList: _*)

    val ut: Map[String, String] = Map[String, String](
        "user" -> "root",
        "pass" -> ex(config.get("password")),
        "win_host" -> "127.0.0.1",
        "other_host" -> ex(config.get("ip"))
    )
    
    val sourceDir: String = ex(config.get("video_dir"))
    
    val keepList: String = basePath + ex(config.get("keep_list"))
    
    def rename(file: File) = ()
    
    def isVideoFile(f: File): Boolean = (f.getName.takeRight(4).equals(".mp4") || f.getName.takeRight(4).equals(".mkv")) && f.isFile
    
    def processFolder(files: List[File], keepList: Map[String, Int]): Set[File] = {
        var processing: collection.mutable.Set[File] = collection.mutable.Set[File]()

        files.foreach { f: File =>

            // match files in keep list to files found in directory
            keepList.keys.foreach { t =>

                if (f.getName.contains(t) && isVideoFile(f)) {
                    processing += f
                } else if (f.isDirectory) {
                    if (f.getName.contains("sample")) {
                        f.delete()
                    } else {
                        processing ++= processFolder(f.listFiles.toList, keepList)
                    }
                } else if (f.getName.takeRight(4).equals(".nfo") || f.getName.takeRight(4).equals(".txt")) {
                    f.delete()
                } else if (f.getName.takeRight(4).equals(".srt")) {
                    processing += f
                }
                // otherwise ignore completely
            }
        }

        processing.toSet
    }

    def main(args: Array[String]) {
        if (!System.getProperty("os.name").contains("Windows")) {
            System.out.println("This application is not designed to run on any operating system other than Windows. Sorry.")
            System.exit(0)
        }
        
        val torrents: JSONArray = new JSONParser().parse(Downloader.getStatus).asInstanceOf[JSONObject].get("torrents")
                .asInstanceOf[JSONArray]

        //JSON array doesn't support foreach. Maybe use an iterator?
        for (i: Int <- 0 to (torrents.size() - 1)) {
            val t = torrents.get(i).asInstanceOf[JSONArray]

            if (t.get(21).toString.equals(Downloader.seedingMessage)) {
                Downloader.clearSeed(t.get(0).toString)
            }
        }

        val processing: Set[File] = processFolder(new File(sourceDir).listFiles.toList,
            Map(fromFile(keepList).getLines().map(_.replace("\n", "").split(",")).map(line => line(0).trim -> line(1)
                    .trim.toInt).toList: _*)
        )

        processing.foreach { f => println(f.getAbsoluteFile)}
    }

}