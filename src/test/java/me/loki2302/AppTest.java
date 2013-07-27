package me.loki2302;

import static org.junit.Assert.*;

import org.eclipse.jetty.server.Server;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

public class AppTest {
    @Test
    public void dummyTest() throws Exception {
        Server server = App.start();
        try {
            Document document = Jsoup.connect("http://localhost:8080/").get();
            
            Element pageHeader = document.select(".page-header h1").first();
            String pageTitle = pageHeader.ownText();
            assertEquals("hello", pageTitle);
            
            Element currentTimeMessage = document.select("p").first();
            String currentTimeText = currentTimeMessage.ownText();
            assertTrue(currentTimeText.toLowerCase().contains("current time is"));
        } finally {        
            server.stop();
            server.join();
        }
    }
}
