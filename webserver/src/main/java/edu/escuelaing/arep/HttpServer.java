package edu.escuelaing.arep;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;

public class HttpServer {

    public static void main(String[] args) throws IOException, URISyntaxException {
        int port = 35000;
        startServer(port);
    }

    private static void startServer(int port) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }

        boolean running = true;

        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            String inputLine, outputLine;

            boolean isFirstLine = true;
            String file = "";

            while ((inputLine = in.readLine()) != null) {
                if (isFirstLine) {
                    file = inputLine.split(" ")[1];
                    isFirstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            URI resourceuri = new URI(file);
            System.out.println("URI: " + resourceuri);

            if (resourceuri.getPath().startsWith("/app/hello")) {
                outputLine = helloRestService(resourceuri.getPath(), resourceuri.getQuery());
                out.println(outputLine);
            } else {
                String filePath = "webserver\\src\\main\\java\\edu\\escuelaing\\arep\\www" + resourceuri.getPath();
                if (filePath.endsWith("/")) {
                    filePath += "index.html";
                }
                serveFile(out, filePath);    
            }
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    static String helloRestService(String path, String query) {
        query = query.substring(5);
        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                + "{\"name\": " + "\"" + query + "\"" + "}";
        return (response);
    }

    static void serveFile(PrintWriter out, String filePath) {
        try {
            String contentType = getContentType(filePath);
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println();
            out.write(new String(fileContent));
        } catch (IOException e) {
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/html");
            out.println();
            out.println("<h1>404 File Not Found</h1>");
        }
    }

    static String getContentType(String filePath) {
        if (filePath.endsWith(".html")) return "text/html";
        if (filePath.endsWith(".css")) return "text/css";
        if (filePath.endsWith(".js")) return "application/javascript";
        if (filePath.endsWith(".png")) return "image/png";
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) return "image/jpeg";
        if (filePath.endsWith(".gif")) return "image/gif";
        return "text/plain";
    }
}
