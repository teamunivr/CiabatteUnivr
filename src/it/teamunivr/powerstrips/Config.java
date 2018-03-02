package it.teamunivr.powerstrips;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Config {
    private Path configDirectory;
    private LoanSave loanSave;
    private static Config ourInstance;
    private static String error;

    static {
        try {
            ourInstance = new Config();
            error = null;
        }catch (IOException e){
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
        String OS = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (OS.contains("win"))
            configDirectory = java.nio.file.Paths.get(home, "appdata", "univr", "PowerStrips");
        else if(OS.contains("mac"))
            configDirectory = java.nio.file.Paths.get(home, "Library", "Application Support", "univr", "PowerStrips");
        else
            configDirectory = java.nio.file.Paths.get(home, ".univr", "PowerStrips");

        // for testing:
        //configDirectory = java.nio.file.Paths.get(home, "git", "CiabatteUnivr", "data-samples");

        if (!java.nio.file.Files.exists(configDirectory)) {
            try {
                java.nio.file.Files.createDirectory(configDirectory);
                Path defaultConfigFile = java.nio.file.Paths.get(
                        Config.class.getResource("resources/DefaultConfig.json").getPath()
                );

                java.nio.file.Files.copy(
                        defaultConfigFile, java.nio.file.Paths.get(configDirectory.toString(), "config.json")
                );
            } catch (IOException e) {
                throw new IOException("the config directory does not exists and cannot be created");
            }
        }

        if (!java.nio.file.Files.exists(java.nio.file.Paths.get(configDirectory.toString(), "config.json")))
            throw new IOException("the config file does not exists");

        java.nio.file.Path loansFile = java.nio.file.Paths.get(configDirectory.toString(), "loans.json");

        if (!java.nio.file.Files.exists(loansFile)) {
            try {
                java.nio.file.Files.createFile(loansFile);
                FileWriter fileWriter = new FileWriter(loansFile.toString());
                fileWriter.write("{\n\t\"loans\":[\n\t]\n}");
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                throw new IOException("the loans.json file does not exists and cannot be created");
            }
        }

        loanSave = new LoanSave(loansFile);
    }

    public HashMap<String, ArrayList<String>> getPowerStrips() throws ParseException {
        HashMap<String, ArrayList<String>> toReturn = new HashMap<>();
        JSONParser parser = new JSONParser();
        Path configFile = java.nio.file.Paths.get(configDirectory.toString(), "config.json");

        try{
            JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(configFile.toString()));

            JSONArray powerStripsArray = (JSONArray) jsonObject.get("powerStrips");
            Iterator<JSONObject> PSIterator = powerStripsArray.iterator();

            while (PSIterator.hasNext()){
                JSONObject tmp = PSIterator.next();
                String type = (String) tmp.get("type");
                JSONArray IDs = (JSONArray) tmp.get("ids");
                Iterator<String> IDsIterator = IDs.iterator();
                ArrayList<String> tmpIDsArray = new ArrayList<>();

                while(IDsIterator.hasNext()){
                    tmpIDsArray.add(IDsIterator.next());
                }

                toReturn.put(type, tmpIDsArray);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return toReturn;
    }

    public LoanSave getLoanSave(){
        return loanSave;
    }

    public static void main(String[] args){
        Config cfg = Config.getInstance();
        HashMap<String, ArrayList<String>> map = null;

        try{
            map = cfg.getPowerStrips();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":");
            for (String id: entry.getValue()) {
                System.out.println("\t" + id);
            }
        }

    }
}