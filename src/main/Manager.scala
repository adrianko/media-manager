package main

import java.io.File

object Manager {
    
    val exclusionExtensions: List[String] = List(".txt", ".nfo")
    
    val videoFileExtensions: List[String] = List(".mkv", ".mp4")
    
    val keepExtensions: List[String] = List(".srt")

    def rename(file: File): Unit = () //invoke filebot
    
    def move(src: File, dest: File): Unit = ()

    def isVideoFile(f: File): Boolean = videoFileExtensions.contains(f.getName.takeRight(4)) && f.isFile

    def processFolder(files: List[File], keepList: Map[String, Int]): Set[File] = {
        val processing: collection.mutable.Set[File] = collection.mutable.Set[File]()

        files.foreach { f: File =>

            // match files in keep list to files found in directory
            keepList.keys.foreach { t =>

                if (f.getName.contains(t) && isVideoFile(f) || keepExtensions.contains(f.getName.takeRight(4))) {
                    processing += f
                } else if (f.isDirectory) {
                    if (f.getName.toLowerCase.contains("sample")) {
                        f.delete()
                    } else {
                        processing ++= processFolder(f.listFiles.toList, keepList)
                    }
                } else if (exclusionExtensions.contains(f.getName.takeRight(4))) {
                    f.delete()
                }
                // otherwise ignore completely
            }
        }

        processing.toSet
    }
    
}