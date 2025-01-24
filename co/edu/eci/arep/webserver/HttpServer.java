package co.edu.eci.arep.webserver;

import java.net.*;
import java.io.*;

public class HttpServer {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
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
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
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
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>\r\n" + //
                        "<html>\r\n" + //
                        "    <head>\r\n" + //
                        "        <title>Form Example</title>\r\n" + //
                        "        <meta charset=\"UTF-8\">\r\n" + //
                        "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                        "    </head>\r\n" + //
                        "    <body>\r\n" + //
                        "        <h1>Form with GET</h1>\r\n" + //
                        "        <form action=\"/hello\">\r\n" + //
                        "            <label for=\"name\">Name:</label><br>\r\n" + //
                        "            <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\r\n" + //
                        "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\r\n" + //
                        "        </form> \r\n" + //
                        "        <div id=\"getrespmsg\"></div>\r\n" + //
                        "\r\n" + //
                        "        <script>\r\n" + //
                        "            function loadGetMsg() {\r\n" + //
                        "                let nameVar = document.getElementById(\"name\").value;\r\n" + //
                        "                const xhttp = new XMLHttpRequest();\r\n" + //
                        "                xhttp.onload = function() {\r\n" + //
                        "                    document.getElementById(\"getrespmsg\").innerHTML =\r\n" + //
                        "                    this.responseText;\r\n" + //
                        "                }\r\n" + //
                        "                xhttp.open(\"GET\", \"/app/hello?name=\"+nameVar);\r\n" + //
                        "                xhttp.send();\r\n" + //
                        "            }\r\n" + //
                        "        </script>\r\n" + //
                        "\r\n" + //
                        "        <h1>Form with POST</h1>\r\n" + //
                        "        <form action=\"/hellopost\">\r\n" + //
                        "            <label for=\"postname\">Name:</label><br>\r\n" + //
                        "            <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>\r\n" + //
                        "            <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\r\n" + //
                        "        </form>\r\n" + //
                        "        \r\n" + //
                        "        <div id=\"postrespmsg\"></div>\r\n" + //
                        "        \r\n" + //
                        "        <script>\r\n" + //
                        "            function loadPostMsg(name){\r\n" + //
                        "                let url = \"/app/hellopost?name=\" + name.value;\r\n" + //
                        "\r\n" + //
                        "                fetch (url, {method: 'POST'})\r\n" + //
                        "                    .then(x => x.text())\r\n" + //
                        "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\r\n" + //
                        "            }\r\n" + //
                        "        </script>\r\n" + //
                        "    </body>\r\n" + //
                        "</html>\r\n" + //
                        "";
                out.println(outputLine);
            }
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }
    private static String helloRestService(String path, String query){
        query = query.substring(5);
        String response = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + "{\"name\": "+ "\"" + query + "\""+ "}";
        return(response);
    }
}
