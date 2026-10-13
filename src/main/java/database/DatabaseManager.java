package database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;

public class DatabaseManager {

    private static final Path DB_FOLDER = Path.of(
            System.getenv("LOCALAPPDATA") != null
                    ? System.getenv("LOCALAPPDATA")
                    : System.getProperty("user.home"),
            "StatisticsApp"
    );

    private static final Path DB_PATH =
            DB_FOLDER.resolve("pokemon_stats.db");

    public static Connection getConnection() throws SQLException {
        try {
            Files.createDirectories(DB_FOLDER);
        } catch (IOException e) {
            throw new SQLException("Could not create database folder", e);
        }

        return DriverManager.getConnection(
                "jdbc:sqlite:" + DB_PATH.toAbsolutePath()
        );
    }

    public static void initializeDatabase() {

        String createTable = """
                CREATE TABLE IF NOT EXISTS entries (
                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                    date             TEXT,
                    day              TEXT,
                    time             TEXT,
                    pokemonName      TEXT,
                    cp               INTEGER,
                    caught           BOOLEAN,
                    shiny            BOOLEAN,
                    weather          TEXT,
                    park             TEXT,
                    location         TEXT,
                    tag              TEXT,
                    event            TEXT,
                    incense          BOOLEAN,
                    incenseDuration  INTEGER
                );
                """;

        try (Connection conn = getConnection();
             var stmt = conn.createStatement()) {

            stmt.execute(createTable);

            stmt.execute("""
                    UPDATE entries
                    SET day = upper(substr(day,1,1)) || lower(substr(day,2))
                    WHERE day IS NOT NULL
                      AND day <> ''
                      AND day = upper(day)
                    """);

            System.out.println("Database ready!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}