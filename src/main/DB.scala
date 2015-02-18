package main

import java.io.File
import java.sql._

object DB {

    var conn: Connection = null
    var stmt: Statement = null

    def getConnection: Connection = {
        if (conn == null) {
            try {
                Class.forName("org.sqlite.JDBC")
                conn = DriverManager.getConnection("jdbc:sqlite:" + new File(getClass.getResource(".").getFile).getAbsolutePath + "/../../../../db/config.db")
            } catch {
                case e: SQLException => e.printStackTrace()
            }
        }

        conn
    }

    def getStatement: Statement = {
        if (stmt == null) {
            stmt = getConnection.createStatement
        }

        stmt
    }

    def loadSettings: Map[String, String] = {
        try {
            class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
                def hasNext = rs.next()
                def next() = rs
            }

            new ResultSetIterator(getStatement.executeQuery("SELECT * FROM settings"))
                .map(x => (x.getString("property"), x.getString("value"))).toMap
        } catch {
            case e: SQLException =>
                e.printStackTrace()
                Map[String, String]()
        }
    }

    def getKeepList: Map[String, Int] = {
        Map[String, Int]()
    }

}