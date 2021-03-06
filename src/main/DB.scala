package main

import java.sql.{Connection, Statement, DriverManager, SQLException, ResultSet}

object DB extends Base {

    val dbPath: String = path + "db/config.db"
    var conn: Connection = null
    var stmt: Statement = null

    def getConnection: Connection = {
        if (conn == null) {
            try {
                Class.forName("org.sqlite.JDBC")
                conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)
            } catch {
                case e: SQLException => e.printStackTrace()
            }
        }

        conn
    }

    def getStatement: Statement = {
        if (stmt == null) stmt = getConnection.createStatement

        stmt
    }

    def loadSettings: Map[String, String] = {
        try {
            new ResultSetIterator(getStatement.executeQuery("SELECT * FROM settings")).map(x => (
                    x.getString("property"), x.getString("value"))).toMap
        } catch {
            case e: SQLException =>
                e.printStackTrace()
                Map[String, String]()
        }
    }

    def getKeepList: Map[String, Int] = {
        try {
            new ResultSetIterator(getStatement.executeQuery("SELECT * FROM keep")).map(x => (x.getString("title"), 
                    x.getInt("quality"))).toMap
        } catch {
            case e: SQLException =>
                e.printStackTrace() 
                Map[String, Int]()
        }
    }

    class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
        def hasNext = rs.next()
        def next() = rs
    }

}