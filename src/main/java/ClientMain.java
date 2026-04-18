import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException, ClassNotFoundException {


        new ClientSide("localhost", 9090);
    }
}