package main

import java.io.File
import java.sql.{DriverManager, Connection}

object DB {

    var conn: Connection = null

    def getConnection: Connection = {
        if (conn == null) {
            try {
                Class.forName("org.sqlite.JDBC")
                conn = DriverManager.getConnection("jdbc:sqlite:" + new File(getClass.getResource(".").getFile).getAbsolutePath + "/../../../../db/shows.db")
            } catch {
                case e: Any => e.printStackTrace()
            }
        }

        conn
    }

    def loadSettings(): Unit = {

    }
}