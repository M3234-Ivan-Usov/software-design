package ru.akirakozov.sd.refactoring;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.*;
import java.net.http.*;

/**
 * @author iusov
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest {
    private static final Thread testServer = new Thread(() -> {
        try {
            Main.main(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeClass
    public static void initServer() throws InterruptedException {
        testServer.start();
        Thread.sleep(1000);  // Waiting for server to start
    }

    private HttpResponse<String> connectTo(String postfix)
            throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder(
                new URL("http://localhost:" + Main.PORT + postfix).toURI()
        ).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        return response;
    }

    @Test
    public void test01AddProductConnection()
            throws IOException, URISyntaxException, InterruptedException {
        connectTo("/add-product?name=iphone6&price=300");
        connectTo("/add-product?name=iphone7&price=500");
    }

    @Test
    public void test02GetProductsConnection()
            throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = connectTo("/get-products");
        Document doc = Jsoup.parse(response.body());
        assertEquals("iphone6 300 iphone7 500", doc.body().text());
    }

    private void makeQuery(String command, String expectedResult)
            throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = connectTo("/query?command=" + command);
        Document doc = Jsoup.parse(response.body());
        assertEquals(expectedResult, doc.body().text());
    }

    @Test
    public void test03QueryConnection() throws IOException, URISyntaxException, InterruptedException {
        makeQuery("sum", "Summary price: 800");
        makeQuery("min", "Product with min price: iphone6 300");
        makeQuery("max", "Product with max price: iphone7 500");
        makeQuery("count", "Number of products: 2");
    }

    @AfterClass
    public static void shutdownServer() {
        Main.execSql(Main.DB_NAME, "DELETE FROM PRODUCT");
        testServer.interrupt();
    }
}