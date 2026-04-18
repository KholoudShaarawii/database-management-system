import java.io.IOException;
import java.io.Serializable;

public class ServerMain {
    public static void main(String[] args) throws IOException, ClassNotFoundException {


        ServerSide handler = new ServerSide();
        handler. run();

    }
}
