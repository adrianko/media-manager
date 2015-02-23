package main

import java.io.{FileOutputStream, FileInputStream, File}

object MediaLibrary extends Base {

    val mediaLibraryFolder: File = new File(ex(MediaManager.settings.get("lib_folder")))

    lazy val processingList = new File(ex(MediaManager.settings.get("processed_dir"))).listFiles.toList

    def retrieveFiles(): Unit = {
        val showFolders: Map[String, File] = Map(mediaLibraryFolder.listFiles.filter(_.isDirectory)
            .map(d => d.getName -> d).toList: _*)

        showFolders.foreach{ case (t: String, f: File) => println(t + " " + f.getAbsolutePath) }
        processingList.foreach(println)
        processingList.foreach{ f =>
            showFolders.foreach { case (d: String, dir: File) =>
                if (f.getName.toLowerCase.contains(d.toLowerCase)) {
                    copy(f, new File(dir.getAbsolutePath + "\\" + f.getName))
                }
            }
        }
    }

    def copy(src: File, dest: File): Unit = {
        val srcFIS = new FileInputStream(src)
        val destFIS = new FileOutputStream(dest)
        destFIS.getChannel.transferFrom(srcFIS.getChannel, 0, Long.MaxValue)
        destFIS.close()
        srcFIS.close()
    }

    def refresh(): Unit = Downloader.download("http://" + ex(MediaManager.settings.get("lib_host")) + ":" +
        ex(MediaManager.settings.get("lib_port")) + ex(MediaManager.settings.get("lib_path")))

}