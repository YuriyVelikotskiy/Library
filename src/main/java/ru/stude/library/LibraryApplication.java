package ru.stude.library;

import java.sql.*;

public class LibraryApplication {
    static final String DB_URL = "jdbc:postgresql://localhost/dbforlibrary";
    static final String USER = "test";
    static final String PASS = "test";

    public static void main(String[] args) throws SQLException {

        System.out.println("Testing connection to PostgreSQL JDBC");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }

        Statement stmt1 = connection.createStatement();
        Statement stmt2 = connection.createStatement();

        ResultSet rs1 = stmt1.executeQuery(
                    "SELECT DISTINCT reader.name_reader, count(title)\n" +
                        "FROM lib.book \n" +
                        "INNER JOIN lib.reader ON book.reader_id = reader.reader_id\n" +
                        "WHERE reader.name_reader <> 'lib'\n" +
                        "GROUP BY reader.name_reader\n" +
                        "ORDER BY count(title) DESC;"
        );

        ResultSet rs2 = stmt2.executeQuery(
                    "SELECT genre.name_genre\n" +
                        "FROM lib.book \n" +
                        "INNER JOIN lib.genre ON book.genre_id = genre.genre_id\n" +
                        "WHERE book.reader_id <> '11'\n" +
                        "GROUP BY genre.name_genre\n" +
                        "HAVING COUNT (title) = ( \n" +
                            "SELECT MAX(numberOfBooks)\n" +
                            "FROM (\n" +
                                "SELECT book.genre_id, COUNT(title) AS numberOfBooks\n" +
                                "FROM lib.book \n" +
                                "WHERE book.reader_id <> '11'\n" +
                                "GROUP BY book.genre_id\n" +
                        "   ) AS selection\n" +
                        ");"
        );
            System.out.println("Books on hand:");
        while (rs1.next())
            System.out.println(rs1.getString(1) + " " + rs1.getString(2));

            System.out.println("");
            System.out.println("The most popular genres:");
        while (rs2.next())
            System.out.println(rs2.getString(1));

        try {
            connection.close();
            System.out.println("You have successfully disconnected");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

