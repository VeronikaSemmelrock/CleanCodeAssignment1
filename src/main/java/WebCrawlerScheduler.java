import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WebCrawlerScheduler {

    private static final int timeoutForChildThreadsInMinutes = 10;
    private static final int timeoutForShutdownInMinutes = 10;

    private static ExecutorService executorService;

    private final List<Future<String>> futures = new ArrayList<>();
    private final StringBuilder result = new StringBuilder();

    private final Map<Future<String>, String> futuresWithUrls = new HashMap<>();

    public static void initializeThreadPoolWithThreadCount(int threadCount) {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(threadCount);
        }
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

    public void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(timeoutForShutdownInMinutes, TimeUnit.MINUTES);
        } catch (InterruptedException interruptedException) {
            System.out.println("Exception when waiting for thread pool termination");
            interruptedException.printStackTrace();
        }
    }
}
