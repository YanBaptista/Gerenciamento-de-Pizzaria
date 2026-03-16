package pizzaria.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // APONTANDO PARA O BANCO CORRETO!
    private static final String URL = "jdbc:mysql://localhost:3306/db_pizzaria";

    // Verifique se o usuário e senha do seu MySQL são esses mesmo
    private static final String USER = "root";
    private static final String PASSWORD = "Root"; // Ou a senha que você usa

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
