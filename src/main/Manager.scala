package main

import java.io.{FileInputStream, FileOutputStream, File}

import sys.process._

object Manager extends Base {

    private val fileDirSettings: Map[String, List[String]] = Map[String, List[String]](
        "deleteExt" -> List("txt", "nfo"),
        "videoFileExt" -> List("mp4", "mkv"),
        "keepExt" -> List("srt"),
        "excludeDir" -> List(".sync"),
        "deleteDir" -> List("sample")
    )

    val fileList: File = new File(ex(MediaManager.settings.get("video_dir")))

    lazy val processingList: List[File] = new File(ex(MediaManager.settings.get("processed_dir"))).listFiles.toList

    def moveFiles(): Unit = retrieveFiles().foreach{ f => Manager.move(f.getAbsoluteFile) }

    def cleanupFolder(): Unit = fileList.listFiles.toList.filter(f => f.isDirectory && f.list.isEmpty).foreach(f =>
        f.delete)

    def processFolder(): Unit = processingList.foreach(rename)

    private def rename(file: File): Unit = Seq("filebot", "-rename", file.getAbsoluteFile.toString, "--format", 
        "\"{n} - {s00e00} - {t}\"", "-non-strict", "--db", "TVRage", "--output", "\"" + ex(MediaManager.settings
        .get("processed_dir")) + "\"").!
    
    private def copy(src: File): Unit = {
        val srcFIS = new FileInputStream(src)
        val destFIS = new FileOutputStream(new File(ex(MediaManager.settings.get("processed_dir")) + "/" + src.getName))
        destFIS.getChannel.transferFrom(srcFIS.getChannel, 0, Long.MaxValue)
        destFIS.close()
        srcFIS.close()
    }

    private def move(src: File): Unit = {
        copy(src)
        src.delete
    }

    private def fileExt(f: File): String = f.getName.toLowerCase.substring(f.getName.lastIndexOf(".") + 1)

    private def isVideoFile(f: File): Boolean = exList(fileDirSettings.get("videoFileExt")).contains(fileExt(f)) && f.isFile
    
    private def retrieveFiles(): Set[File] = retrieveFiles(fileList.listFiles.toList, DB.getKeepList)

    private def retrieveFiles(files: List[File], keepList: Map[String, Int]): Set[File] = {
        val processing: collection.mutable.Set[File] = collection.mutable.Set[File]()

        files.foreach { f: File =>

            // match files in keep list to files found in directory
            keepList.keys.foreach { t: String =>
                val fileName: String = f.getName.toLowerCase

                if (f.isFile) {
                    if (fileName.contains(t.toLowerCase.replace(" ", ".")) && (isVideoFile(f) ||
                        exList(fileDirSettings.get("keepExt")).contains(fileExt(f)))) {
                        if ((exInt(keepList.get(t)) == 1 && fileName.toLowerCase.contains("1080p")) ||
                            (exInt(keepList.get(t)) == 0 && fileName.toLowerCase.contains("hdtv")) ||
                            (exInt(keepList.get(t)) == 2 && fileName.toLowerCase.contains("720p"))) {
                            processing += f
                        }
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
    
}