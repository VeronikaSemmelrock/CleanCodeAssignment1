# CleanCodeAssignment1
This project is a Web-Crawler written in Java, which outputs a compact overview of the given website and linked websites in a given language by only listing the headings and the links in a file called output.md.

The project is a Maven-project, so building, running and testing works as usual. 
The WebCrawler can be started by executing the main-Method in the class Main. The system is then started and the user can follow the instructions in the command line to input all arguments needed for the crawler to start. The arguments are input in the following syntax {URL};{depth};{targetLanguage}. An example would be the input "https://campus.aau.at;1;german". The input consists of three arguments - a URL to the website to crawl, a depth at which the website and all links should be crawled and a target language for the possible translation of headings. 
When the input was entered correctly, the WebCrawler is started automatically. Once it is done, the resulting output.md-file can be found in the root-directory of the project. 

Automatic testing can be started via opening the Maven-Tab, navigating to Lifecycle and clicking on "test". 
