import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class DiskManager {


    public File createFile() {

        Path rootFolder = Path.of("E:\\DataBaseManagementSystem"); //rootFolder

        Path DiskFile = Path.of("E:\\DataBaseManagementSystem\\DataBase\\DiskFile");//file

        try {
            Path DataBaseFolder = Path.of("E:\\DataBaseManagementSystem\\DataBase");//folder

            Path folderDirectory = Files.createDirectories(DataBaseFolder);
            if (Files.notExists(DiskFile)) {
                System.out.println("Disk file will be created");
                Files.createFile(DiskFile);
            }
        } catch (IOException | SecurityException e) {
            System.out.println(e.getMessage());
        }
        return DiskFile.toFile();
    }


    //Client data -> Row/Object -> Serialization -> bytes -> disk
    public void write(Object data) throws IOException {
        File file = this.createFile();

        FileOutputStream fileOutputStream = new FileOutputStream(file); //writing the file
        //Core Method: writeObject(Object obj) writes an object to the stream
        //FileOutputStream already is an OutputStream

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream); //converting the object into bytes
        objectOutputStream.writeObject(data); //Serialization
        objectOutputStream.flush(); //make sure data is written
        objectOutputStream.close();//finish using the stream and release the system resources it was holding
    }

    //reading => disk -> bytes -> Deserialization -> Row/Object -> returned result
    public Object read() throws IOException, ClassNotFoundException {
        File file = this.createFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object readObject = objectInputStream.readObject();
        objectInputStream.close();

        return readObject;
    }

}
