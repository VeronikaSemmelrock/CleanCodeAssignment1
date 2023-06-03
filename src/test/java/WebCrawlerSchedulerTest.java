import kotlin.collections.AbstractMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import webcrawler.WebCrawlerScheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerSchedulerTest {

    private WebCrawlerScheduler webCrawlerScheduler;

    @BeforeEach
    void init() {
        WebCrawlerScheduler.initializeThreadPoolWithThreadCount(5);
        webCrawlerScheduler = new WebCrawlerScheduler();
    }

    @Test
    void simpleTasksTest() {
        webCrawlerScheduler.submit(() -> "Result1", "Url1");
        webCrawlerScheduler.submit(() -> "Result2", "Url2");

        String result = webCrawlerScheduler.getResult();
        assertEquals("Result1Result2", result);
    }

    @Test
    void complexTasksTest() {
        List<Map.Entry<Integer, Integer>> tuples = intTuples();

        for (Map.Entry<Integer, Integer> tuple : tuples) {
            webCrawlerScheduler.submit(() -> String.valueOf(tuple.getKey() + tuple.getValue()), "Url1");
        }

        String result = webCrawlerScheduler.getResult();
        assertEquals("510275", result);
    }

    private List<Map.Entry<Integer, Integer>> intTuples() {
        return List.of(Map.entry(1, 4), Map.entry(2, 8), Map.entry(1, 1), Map.entry(30, 45));
    }

    @Test
    void timeoutWhenWaitingForChildTest() {
        WebCrawlerScheduler.timeoutForChildThreadsInMinutes = 1;

        webCrawlerScheduler.submit(() -> {
            Thread.sleep(120_000);
            return "result";
        }, "url");

        String result = webCrawlerScheduler.getResult();

        assertEquals("url\nTimeoutException when waiting on child crawling url", result);
    }

    @Test
    void executionExceptionInChildTest() {
        webCrawlerScheduler.submit(() -> {
            throw new Exception();
        }, "url");

        String result = webCrawlerScheduler.getResult();
        assertEquals("url\nExecutionException when waiting on child crawling url", result);
    }

    @Test
    void invalidShutdownTest() {
        WebCrawlerScheduler.timeoutForShutdownInMinutes = 1;

        webCrawlerScheduler.submit(() -> {
            Thread.sleep(120_000);
            return "result";
        }, "url");

        assertFalse(webCrawlerScheduler.shutdown());
    }

    @Test
    void validShutdownTest() {
        assertTrue(webCrawlerScheduler.shutdown());
    }
}