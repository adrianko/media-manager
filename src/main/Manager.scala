package main

import java.io.File

object Manager {

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
    
}