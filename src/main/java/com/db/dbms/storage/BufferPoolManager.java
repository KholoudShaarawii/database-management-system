package com.db.dbms.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BufferPoolManager { //Load whole database into memory, modify in memory, write whole database back to disk
    public final DiskManager diskManager = new DiskManager();
    public Map<String, Map<String, String>> tables = new HashMap<>();
    public Map<String, List<Map<String, Object>>> rows = new HashMap<>();
    public Map<String, Object> page = new HashMap<>();
    public boolean isDirty = false;

    public BufferPoolManager() throws IOException, ClassNotFoundException {
        loadOrInitializePage();
    }

    public void loadOrInitializePage() throws IOException, ClassNotFoundException {
        Path DiskFile = Path.of("E:\\DataBaseManagementSystem\\DataBase\\DiskFile");
        //loaded in RAM
        // case1
        if (Files.exists(DiskFile) && Files.size(DiskFile) > 0) { //EOFEXCEPTION
            System.out.println("found and loaded into RAM ");
            this.page = (Map<String, Object>) diskManager.read();

            //this.tables.containsKey("users")-> false???
            //Ensure that the fields I work with in RAM are exactly the same as the ones stored on disk.
            // this.tables must use the tables object stored inside page
            this.tables = (Map<String, Map<String, String>>) this.page.get("tables");
            this.rows = (Map<String, List<Map<String, Object>>>) this.page.get("rows");

        } else {
            //case2
            diskManager.createFile();
            page.put("tables", tables);
            page.put("rows", rows);
            diskManager.write(page);

            System.out.println("File was created and the page object was loaded into RAM");
        }
    }

    public void flushToDisk() throws IOException {
        if (isDirty) {
            diskManager.write(page);
            isDirty = false;
            System.out.println("Page marked dirty and written to disk ");
        }
    }


}