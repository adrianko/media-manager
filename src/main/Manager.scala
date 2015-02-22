package main

import java.io.{FileInputStream, FileOutputStream, File}

import sys.process._

object Manager extends Base {

    val fileDirSettings: Map[String, List[String]] = Map[String, List[String]](
        "deleteExt" -> List("txt", "nfo"),
        "videoFileExt" -> List("mp4", "mkv"),
        "keepExt" -> List("srt"),
        "excludeDir" -> List(".sync"),
        "deleteDir" -> List("sample")
    )

    val fileList = new File(ex(MediaManager.settings.get("video_dir")))

    lazy val processingList = new File(ex(MediaManager.settings.get("processed_dir"))).listFiles.toList

    def rename(file: File): Unit =
        Seq("filebot", "-rename", file.getAbsoluteFile.toString, "--format", "\"{n} - {s00e00} - {t}\"", "-non-strict",
            "--db", "TVRage", "--output", "\"" + ex(MediaManager.settings.get("processed_dir")) + "\"").!
    
    def copy(src: File): Unit = {
        val srcFIS = new FileInputStream(src)
        val destFIS = new FileOutputStream(new File(ex(MediaManager.settings.get("processed_dir")) + "/" + src.getName))
        destFIS.getChannel.transferFrom(srcFIS.getChannel, 0, Long.MaxValue)
        destFIS.close()
        srcFIS.close()
    }

    def move(src: File): Unit = {
        copy(src)
        src.delete
    }

    def fileExt(f: File): String = f.getName.toLowerCase.substring(f.getName.lastIndexOf(".") + 1)

    def isVideoFile(f: File): Boolean = exList(fileDirSettings.get("videoFileExt")).contains(fileExt(f)) && f.isFile
    
    def retrieveFiles(): Set[File] = retrieveFiles(fileList.listFiles.toList, DB.getKeepList)

    def retrieveFiles(files: List[File], keepList: Map[String, Int]): Set[File] = {
        val processing: collection.mutable.Set[File] = collection.mutable.Set[File]()

        files.foreach { f: File =>

            // match files in keep list to files found in directory
            keepList.keys.foreach { t =>
                val fileName: String = f.getName.toLowerCase

                if (f.isFile) {
                    if (fileName.contains(t.toLowerCase.replace(" ", ".")) && (isVideoFile(f) ||
                        exList(fileDirSettings.get("keepExt")).contains(fileExt(f)))) {
                        processing += f
                    } else if (exList(fileDirSettings.get("deleteExt")).contains(fileExt(f))) {
                        f.delete
                    }
                } else if (f.isDirectory) {
                    if (exList(fileDirSettings.get("deleteDir")).contains(fileName)) {
                        f.delete
                    } else if(!exList(fileDirSettings.get("excludeDir")).contains(fileName)) {
                        processing ++= retrieveFiles(f.listFiles.toList, keepList)
                    }
                }
                // otherwise ignore completely
            }
        }

        processing.toSet
    }

    def cleanupFolder(): Unit = fileList.listFiles.toList.filter(f => f.isDirectory && f.list.length == 0).foreach(f => f.delete)

    def processFolder(): Unit = processingList.foreach(rename)
    
}