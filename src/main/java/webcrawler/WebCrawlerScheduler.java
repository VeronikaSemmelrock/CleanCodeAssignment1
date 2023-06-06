package webcrawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WebCrawlerScheduler {

    public static int timeoutForChildThreadsInMinutes = 10;
    public static int timeoutForShutdownInMinutes = 10;

    private static ExecutorService executorService;

    private final List<Future<String>> futures = new ArrayList<>();
    private final StringBuilder result = new StringBuilder();

    private final Map<Future<String>, String> futuresWithUrls = new HashMap<>();

    //Initialization of a too small thread pool (like size 1) can cause deadlocks
    //Parent threads would try to start child threads but no more threads would be available in the pool so the timeoutForChildThreadsInMinutes would be reached as no results are created.
    public static void initializeThreadPoolWithThreadCount(int threadCount) {
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    public void submit(Callable<String> task, String url) {
        Future<String> future = executorService.submit(task);
        futures.add(future);
        futuresWithUrls.put(future, url);
    }

    public String getResult() {
        for (Future<String> future : futures) {
            try {
                result.append(future.get(timeoutForChildThreadsInMinutes, TimeUnit.MINUTES));
            } catch (Exception e) {
                String url = futuresWithUrls.get(future);
                result.append(url).append("\n").append(getErrorAsReportMessage(e));
            }
        }
        return result.toString();
    }

    private String getErrorAsReportMessage(Exception e) {
        if (e instanceof TimeoutException) {
            return "TimeoutException when waiting on child crawling url";
        } else if (e instanceof ExecutionException) {
            return "ExecutionException when waiting on child crawling url";
        } else if (e instanceof InterruptedException) {
            return "InterruptedException when waiting on child crawling url";
        }
        return "Exception when waiting on child crawling url";
    }

    public boolean shutdown() {
        executorService.shutdown();
        try {
            return executorService.awaitTermination(timeoutForShutdownInMinutes, TimeUnit.MINUTES);
        } catch (InterruptedException interruptedException) {
            System.out.println("InterruptedException when waiting for thread pool termination");
            interruptedException.printStackTrace();
        }
        return false;
    }
}
