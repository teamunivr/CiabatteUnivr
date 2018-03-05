package it.teamunivr.powerstrips;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Config {
    private Path configFile;
    private LoanSave loanSave;
    private static Config ourInstance;
    private static String error;

    static {
        try {
            ourInstance = new Config();
            error = null;
        } catch (IOException e) {
            error = e.getMessage();
            ourInstance = null;
        }
    }

    public static Config getInstance() throws RuntimeException {
        if (ourInstance == null)
            throw new RuntimeException(
                    String.format("cannot initialize the directory containing the config files. Error: %s", error)
            );

        return ourInstance;
    }

    private Config() throws IOException {
        Path configDir;
        String OS = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (OS.contains("win"))
            configDir = java.nio.file.Paths.get(home, "appdata", "univr", "PowerStrips");
        else if (OS.contains("mac"))
            configDir = java.nio.file.Paths.get(home, "Library", "Application Support", "univr", "PowerStrips");
        else
            configDir = java.nio.file.Paths.get(home, ".univr", "PowerStrips");

        if (!java.nio.file.Files.exists(configDir)) {
            try {
                java.nio.file.Files.createDirectories(configDir);
            } catch (IOException e) {
                throw new IOException("the config directory does not exists and cannot be created");
            }
        }

        if (!java.nio.file.Files.exists(java.nio.file.Paths.get(configDir.toString(), "config.json"))) {

            InputStream inputStream = Config.class.getResourceAsStream("resources/DefaultConfig.json");

            File config = new File(java.nio.file.Paths.get(configDir.toString(), "config.json").toString());

            OutputStream outputStream = new FileOutputStream(config);
            copyStream(inputStream, outputStream);
            outputStream.close();
        }

        configFile = java.nio.file.Paths.get(configDir.toString(), "config.json");

        Path saveFile = java.nio.file.Paths.get(configDir.toString(), "loans.json");

        if (!java.nio.file.Files.exists(saveFile)) {
            try {
                java.nio.file.Files.createFile(saveFile);
                FileWriter fileWriter = new FileWriter(saveFile.toString());
                fileWriter.write("{\n\t\"loans\":[\n\t]\n}");
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                throw new IOException("the loans.json file does not exists and cannot be created");
            }
        }

        loanSave = new LoanSave(saveFile);
    }

    @SuppressWarnings("unchecked")
    public Map<String, ArrayList<String>> getLoanableItems() throws ParseException {
        Map<String, ArrayList<String>> toReturn = new TreeMap<>();
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(configFile.toString()));

            JSONArray powerStripsArray = (JSONArray) jsonObject.get("items");

            for (JSONObject tmp : (Iterable<JSONObject>) powerStripsArray) {
                String type = (String) tmp.get("type");
                JSONArray IDs = (JSONArray) tmp.get("ids");
                Iterator<String> IDsIterator = IDs.iterator();
                ArrayList<String> tmpIDsArray = new ArrayList<>();

                while (IDsIterator.hasNext()) {
                    tmpIDsArray.add(IDsIterator.next());
                }

                toReturn.put(type, tmpIDsArray);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    public LoanSave getLoanSave() {
        return loanSave;
    }

    @SuppressWarnings("unchecked")
    public void setLoanableItemes(Map<String, ArrayList<String>> map) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject itemsObject = (JSONObject) parser.parse("{\n\t\"items\":[\n\t]\n}");
        JSONArray itemsArray = (JSONArray) itemsObject.get("items");

        for (Map.Entry<String, ArrayList<String>> e : map.entrySet()) {
            JSONObject tmpObject = new JSONObject();
            JSONArray IDs = new JSONArray();

            IDs.addAll(e.getValue());

            tmpObject.put("type", e.getKey());
            tmpObject.put("ids", IDs);
            itemsArray.add(tmpObject);
        }

        try {
            FileWriter fileWriter = new FileWriter(configFile.toString());
            fileWriter.write(itemsObject.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Config cfg = Config.getInstance();
        Map<String, ArrayList<String>> map;

        try {
            map = cfg.getLoanableItems();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (String id : entry.getValue()) {
                System.out.println("\t" + id);
            }
        }

    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}