package game;

import java.sql.*;


public class Database {

    private static Database db = null;

    public static Database getInstance() {
        if (db == null) {
            db = new Database();
        }
        return db;
    }

    private final String DB_NAME = "jdbc:sqlite:database.db";
    private final String TABLE_GAME = "Mastermind";

    public Database() {
        createDatabase();
        createGameTable();
    }

    private void createDatabase() {
        try {
            DriverManager.getConnection(DB_NAME);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createGameTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_GAME + "("
                + "    username text NOT NULL UNIQUE,\n"
                + " guess_score INTEGER NOT NULL);";

        try (Connection conn = DriverManager.getConnection(DB_NAME);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_NAME);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // create game entry
    public void createGameEntry(String username) {
        String sql = "INSERT INTO " + TABLE_GAME +
                "(username,guess_score) VALUES(?,0)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
    }

    public void incrementGuessScore(String username) {
        incrementScore(username, "guess_score", 1);
    }

    private void incrementScore(String username, String game_name, int val) {
        // try to update the type score for the username, if not successful
        // it means that user is not in db, so add em with a 1 score for type
        createGameEntry(username);
        String sql = "UPDATE " + TABLE_GAME +
                " SET " + game_name + " = " + game_name + " + " + val + " " +
                " WHERE username = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public long getGuessScore(String username) {
        return getScore(username, "guess_score");
    }

    private long getScore(String username, String game_name) {
        createGameEntry(username);
        String sql = "SELECT " + game_name + " "
                + "FROM " + TABLE_GAME + " WHERE username = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the value
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            // loop through the result set
            if (rs.next()) {
                return rs.getInt(game_name);
            }
            return -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -2;
        }
    }

}

