package it.teamunivr.powerstrips;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    public HashMap<String, ArrayList<String>> getPowerStrips() throws ParseException {
        HashMap<String, ArrayList<String>> toReturn = new HashMap<>();
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(configFile.toString()));

            JSONArray powerStripsArray = (JSONArray) jsonObject.get("powerStrips");
            Iterator<JSONObject> PSIterator = powerStripsArray.iterator();

            while (PSIterator.hasNext()) {
                JSONObject tmp = PSIterator.next();
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

    public static void main(String[] args) {
        Config cfg = Config.getInstance();
        HashMap<String, ArrayList<String>> map;

        try {
            map = cfg.getPowerStrips();
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