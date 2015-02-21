package main

import java.io.File

object Manager extends Base {

    val fileDirSettings: Map[String, List[String]] = Map[String, List[String]](
        "deleteExt" -> List(".txt", ".nfo"),
        "videoFile" -> List(".mp4", ".mkv"),
        "keepExt" -> List(".srt"),
        "excludeDir" -> List(".sync"),
        "deleteDir" -> List("sample")
    )

    val fileList = new File(ex(MediaManager.settings.get("video_dir")))

    def rename(file: File): Unit = () //invoke filebot
    
    def move(src: File, dest: File): Unit = ()

    def isVideoFile(f: File): Boolean = exList(fileDirSettings.get("videoFile")).contains(f.getName.takeRight(4)) && f.isFile
    
    def retrieveFiles(): Set[File] = retrieveFiles(fileList.listFiles.toList, DB.getKeepList)

    def retrieveFiles(files: List[File], keepList: Map[String, Int]): Set[File] = {
        val processing: collection.mutable.Set[File] = collection.mutable.Set[File]()

        files.foreach { f: File =>

            // match files in keep list to files found in directory
            keepList.keys.foreach { t =>

                if (f.isFile) {
                    if (f.getName.contains(t.replace(" ", ".")) && (isVideoFile(f) || exList(fileDirSettings.get("keepExt"))
                        .contains(f.getName.takeRight(4)))) {
                        processing += f
                    } else if (exList(fileDirSettings.get("deleteExt")).contains(f.getName.takeRight(4))) {
                        f.delete()
                    }
                } else if (f.isDirectory) {
                    if (exList(fileDirSettings.get("deleteDir")).contains(f.getName.toLowerCase)) {
                        f.delete()
                    } else if(!exList(fileDirSettings.get("excludeDir")).contains(f.getName)) {
                        processing ++= retrieveFiles(f.listFiles.toList, keepList)
                    }
                }
                // otherwise ignore completely
            }
        }

        processing.toSet
    }

    def cleanupFolder(): Unit = fileList.listFiles.filter(f => f.isDirectory && f.list.length == 0).foreach(f => f.delete)
    
}