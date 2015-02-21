package main

import java.io.File

object Manager extends Base {
    
    val deleteExtensions: List[String] = List(".txt", ".nfo")
    
    val videoFileExtensions: List[String] = List(".mkv", ".mp4")
    
    val keepExtensions: List[String] = List(".srt")

    val excludeFolders: List[String] = List(".sync")

    val deleteFolders: List[String] = List("sample")
    
    val fileList = new File(ex(MediaManager.settings.get("video_dir"))).listFiles.toList

    def rename(file: File): Unit = () //invoke filebot
    
    def move(src: File, dest: File): Unit = ()

    def isVideoFile(f: File): Boolean = videoFileExtensions.contains(f.getName.takeRight(4)) && f.isFile
    
    def processFolder(): Set[File] = processFolder(fileList, DB.getKeepList)

    def processFolder(files: List[File], keepList: Map[String, Int]): Set[File] = {
        val processing: collection.mutable.Set[File] = collection.mutable.Set[File]()

        files.foreach { f: File =>

            // match files in keep list to files found in directory
            keepList.keys.foreach { t =>

                if (f.isFile) {
                    if (f.getName.contains(t.replace(" ", ".")) && (isVideoFile(f) || keepExtensions.contains(f.getName.takeRight(4)))) {
                        processing += f
                    } else if (deleteExtensions.contains(f.getName.takeRight(4))) {
                        f.delete()
                    }
                } else if (f.isDirectory && !excludeFolders.contains(f.getName)) {
                    if (deleteFolders.contains(f.getName.toLowerCase)) {
                        f.delete()
                    } else {
                        processing ++= processFolder(f.listFiles.toList, keepList)
                    }
                }
                // otherwise ignore completely
            }
        }

        processing.toSet
    }
    
}