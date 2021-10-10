package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

import javax.servlet.http.HttpServlet;
import java.sql.*;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author akirakozov, iusov
 */
public class Main {
    public static final int PORT = 8081;
    public static String DB_NAME;

    private static Server configureServer() {
        Server server = new Server(PORT);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        addServlets(context, Map.ofEntries(
                Map.entry("/add-product", new AddProductServlet()),
                Map.entry("/get-products", new GetProductsServlet()),
                Map.entry("/query", new QueryServlet())
        ));

        return server;
    }

    private static void addServlets(ServletContextHandler context, Map<String, HttpServlet> endpoints) {
        endpoints.forEach((path, servlet) -> context.addServlet(new ServletHolder(servlet), path));
    }

    public static void execSql(String dbName, String operator) {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbName)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(operator);
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Failed to execute SQL operator inside " + dbName);
            e.printStackTrace();
        }
    }

    public static void execQuery(String dbName, String operator, SqlWrapper<ResultSet, SQLException> resultHandler) {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbName)) {
            Statement stmt = c.createStatement();
            ResultSet queryResult = stmt.executeQuery(operator);
            resultHandler.call(queryResult);
            queryResult.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Failed to execute SQL query inside " + dbName);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        DB_NAME = (args != null && args.length > 0)? args[0]: "test.db";
        execSql(DB_NAME,
                "CREATE TABLE IF NOT EXISTS PRODUCT" +
                        "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        " NAME           TEXT    NOT NULL, " +
                        " PRICE          INT     NOT NULL)"
        );

        Server server = configureServer();
        server.start();
        server.join();
        DB_NAME = null;
    }
}
