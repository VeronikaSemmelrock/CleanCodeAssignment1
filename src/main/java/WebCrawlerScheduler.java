import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WebCrawlerScheduler {

    private static final int timeoutForChildThreadsInMinutes = 10;
    private static final int timeoutShutdownInMinutes = 10;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(300);

    private final List<Future<String>> futures = new ArrayList<>();
    private final StringBuilder result = new StringBuilder();

    private final Map<Future<String>, String> futureMap = new HashMap<>();

    public void submit(Callable<String> task, String url) {
        Future<String> future = executorService.submit(task);
        futures.add(future);
        futureMap.put(future, url);
    }

    public String getScheduledResult() {
        for (Future<String> future : futures) {
            String url = futureMap.get(future);
            try {
                String resultStr = future.get(timeoutForChildThreadsInMinutes, TimeUnit.MINUTES);
                result.append(resultStr);
            } catch (TimeoutException timeoutException) {
                result.append("TimeoutException when waiting on child crawling url " + url);
                System.out.println("Waiting on child thread reached timeout. Result skipped.");
            } catch (ExecutionException executionException) {
                result.append("ExecutionException when waiting on child crawling url " + url);
                executionException.printStackTrace();
            } catch (InterruptedException interruptedException) {
                result.append("InterruptedException when waiting on child crawling url " + url);
                interruptedException.printStackTrace();
            }
        }
        return result.toString();
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(timeoutShutdownInMinutes, TimeUnit.MINUTES);
        } catch (InterruptedException interruptedException) {
            System.out.println("Exception when waiting for thread pool termination");
            interruptedException.printStackTrace();
        }
    }
}
