package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.Main;
import ru.akirakozov.sd.refactoring.SqlWrapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.sql.*;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author akirakozov, iusov
 */
public class QueryServlet extends HttpServlet {
    private void queryMax(HttpServletResponse response) throws IOException {
        String sql = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
        PrintWriter writer = response.getWriter();
        Main.execQuery(Main.DB_NAME, sql, resultSet -> {
            writer.println("<html><body>");
            writer.println("<h1>Product with max price: </h1>");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int price = resultSet.getInt("price");
                writer.println(name + "\t" + price + "</br>");
            }
            writer.println("</body></html>");
        });
    }

    private void queryMin(HttpServletResponse response) throws IOException {
        String sql = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
        PrintWriter writer = response.getWriter();
        Main.execQuery(Main.DB_NAME, sql, resultSet -> {
            writer.println("<html><body>");
            writer.println("<h1>Product with min price: </h1>");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int price = resultSet.getInt("price");
                writer.println(name + "\t" + price + "</br>");
            }
            writer.println("</body></html>");
        });
    }

    private void querySum(HttpServletResponse response) throws IOException {
        String sql = "SELECT SUM(price) FROM PRODUCT";
        PrintWriter writer = response.getWriter();
        Main.execQuery(Main.DB_NAME, sql, resultSet -> {
            writer.println("<html><body>");
            writer.println("Summary price: ");
            if (resultSet.next()) {
                writer.println(resultSet.getInt(1));
            }
            writer.println("</body></html>");
        });
    }

    private void queryCount(HttpServletResponse response) throws IOException {
        String sql = "SELECT COUNT(*) FROM PRODUCT";
        PrintWriter writer = response.getWriter();
        Main.execQuery(Main.DB_NAME, sql, resultSet -> {
            writer.println("<html><body>");
            writer.println("Number of products: ");
            if (resultSet.next()) {
                writer.println(resultSet.getInt(1));
            }
            writer.println("</body></html>");
        });
    }

    private final Map<String, SqlWrapper<HttpServletResponse, IOException>> commands = Map.ofEntries(
            Map.entry("max", this::queryMax),
            Map.entry("min", this::queryMin),
            Map.entry("sum", this::querySum),
            Map.entry("count", this::queryCount)
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        if (!commands.containsKey(command)) {
            response.getWriter().println("Unknown command: " + command);
        }
        else {
            commands.get(command).call(response);
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
