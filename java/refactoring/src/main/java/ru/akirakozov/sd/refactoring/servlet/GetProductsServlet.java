package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.Main;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author akirakozov, iusov
 */
public class GetProductsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sql = "SELECT * FROM PRODUCT";
        PrintWriter writer = response.getWriter();
        Main.execQuery(Main.DB_NAME, sql, resultSet -> {
            writer.println("<html><body>");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int price = resultSet.getInt("price");
                writer.println(name + "\t" + price + "</br>");
            }
            writer.println("</body></html>");
        });
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
