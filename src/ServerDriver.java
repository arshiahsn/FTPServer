
/**
 * Driver for WebServer class
 * 
 * 
 * CPSC 441
 * Assignment 3
 * 
 * @author 	Majid Ghaderi
 * @version 2021
 * 
 */

import java.io.*;
import java.util.*;
import java.util.logging.*;


public class ServerDriver {

	private static final Logger logger = Logger.getLogger("WebServer"); // global logger
	private static final int TERM_WAIT_TIME = 10000; // wait until termination (milli-seconds)
    
	public static void main(String[] args) {
        // parse command line args
        HashMap<String, String> params = parseCommandLine(args);
        
        // set the parameters
        int serverPort = Integer.parseInt( params.getOrDefault("-p", "2025") ); // server port number
        Level logLevel = Level.parse( params.getOrDefault("-v", "all").toUpperCase() ); // log levels: all, info, off

        // standard output
        setLogLevel(logLevel);

        System.out.println("log level is set to " + logLevel);
        System.out.println("starting the server on port " + serverPort);
        WebServer server = new WebServer(serverPort);
        
        // start the server
        server.start();
        System.out.println("server started. type \"quit\" to stop");
        System.out.println(".....................................");

        // wait for quit command
        waitForQuit(server);

        try {
            // shutdown the server
            System.out.println();
            System.out.println("server is shutting down...");
            server.shutdown();
            server.join(TERM_WAIT_TIME);
            System.out.println("server stopped");
        } catch (InterruptedException e) {
            // Ok, ignore
            System.out.println("server did not respond to shutdown");
        }

		System.exit(0);
	}


    // wait until user types "quit" or server terminates
    private static void waitForQuit(Thread server) {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (server.isAlive()) {
                // to avoid blocking on input
                if (console.ready()) 
                    if (console.readLine().equals("quit")) break;
            }
        } catch (IOException e) {
            // Ok, ignore
        }
    }    

	// parse command line arguments
	private static HashMap<String, String> parseCommandLine(String[] args) {
		HashMap<String, String> params = new HashMap<String, String>();

		int i = 0;
		while ((i + 1) < args.length) {
			params.put(args[i], args[i+1]);
			i += 2;
		}
		
		return params;
	}
	
	
	// set the global log level and format
	private static void setLogLevel(Level level) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
		
		ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(level);
        logger.addHandler(handler);		
		logger.setLevel(level);
        logger.setUseParentHandlers(false);
	}
	
}
