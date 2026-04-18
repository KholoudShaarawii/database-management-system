import java.io.*;
import java.net.Socket;

/*Client
→ connect(server)
→ send db name
→ read handshake response
→ send query
→ read response*/
public class ClientSide {

    public ClientSide(String address, int port) {
        try {
            Socket clientSocket = new Socket(address, port);// connect to server
            System.out.println("Connected");

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));// read user input
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // read from server
            PrintWriter serverOutput = new PrintWriter(clientSocket.getOutputStream(), true);// send to server

            // 1) handshake
            System.out.print("Enter database name: ");
            String dbName = userInput.readLine();
            serverOutput.println("db:>" + dbName);
            System.out.println("Server: " + serverInput.readLine());
            System.out.println("Server: " + serverInput.readLine());

            String userRequest = "";
            while (true) {
                System.out.print("Enter request: ");
                userRequest = userInput.readLine();

                if (userRequest == null || userRequest.equalsIgnoreCase("Exit")) {
                    System.out.println("Client stopped sending requests");
                    break;
                }
                serverOutput.println("query:>" + userRequest);
                System.out.println("Server: " + serverInput.readLine());
                System.out.println("Server: " + serverInput.readLine());
            }

            userInput.close();
            serverInput.close();
            serverOutput.close();
            clientSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}