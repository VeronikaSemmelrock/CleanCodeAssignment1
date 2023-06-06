# CleanCodeAssignment1
This project is a Web-Crawler written in Java, which outputs a compact overview of the given website and linked websites in a given language by only listing the headings and the links in a file called output.md.

The project is a Maven-project, so building, running and testing works as usual. 

Before the WebCrawler can be started, one's Rapid-API Key must be set in the TextTranslator2TranslatorService-class in the variable KEY. 

The WebCrawler can be started by executing the main-Method in the class Main. The system is then started and the user can follow the instructions in the command line to input all arguments needed for the crawler to start. The arguments are input in the following syntax {depth};{targetLanguage};{URL};{URL};{URL};...{URL}. An example would be the input "1;german;https://campus.aau.at;https://www.google.com;https://www.orf.at". 
When the input was entered correctly, the WebCrawler is started automatically. Once it is done, the resulting output.md-file can be found in the root-directory of the project. 

Please note that this project crawls the websites in a concurrent manner with a parent-thread at the top starting child threads in a tree structure. Each child can start more child-threads and only returns once all their children have returned. The WebCrawlerScheduler-class holds a variable timeoutForChildThreadsInMinutes, which can be set depending on how long the system should be running and waiting on children. The size of the used thread pool can also be changed and set via the initializeThreadPoolWithThreadCount()-method called in Main - please note because of the structure of how child threads are created a too small pool size can cause deadlock-like behaviour which will result in reached timeouts. 

Automatic testing can be started via opening the Maven-Tab, navigating to Lifecycle and clicking on "test". Please note that the tests can take a while to complete. 
