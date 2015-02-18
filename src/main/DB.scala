package main

import java.io.File
import java.sql.{SQLException, Statement, DriverManager, Connection}

object DB {

    var conn: Connection = null
    var stmt: Statement = null

    def getConnection: Connection = {
        if (conn == null) {
            try {
                Class.forName("org.sqlite.JDBC")
                conn = DriverManager.getConnection("jdbc:sqlite:" + new File(getClass.getResource(".").getFile).getAbsolutePath + "/../../../../db/shows.db")
            } catch {
                case e: SQLException => e.printStackTrace()
            }
        }

        conn
    }

    def getStatement: Statement = {
        if (stmt == null) {
            stmt = getConnection.createStatement()
        }

        stmt
    }

    def loadSettings: Map[String, String] = {
        Map[String, String]()
    }

}