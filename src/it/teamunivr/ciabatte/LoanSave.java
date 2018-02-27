package it.teamunivr.ciabatte;

import it.teamunivr.ciabatte.model.Prestito;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class LoanSave {

    private static File dataFile;

    // initializes the data file looking in the /data-samples/Loans.json
    public static void init() {
        dataFile = new File("/Users/noemurr/git/CiabatteUnivr/data-samples/Loans.json");

    }

    private static JSONObject buildJSONObject(Prestito p) {
        JSONObject obj = new JSONObject();
        obj.put("name", p.nomeProperty());
        obj.put("surname", p.cognomeProperty());
        obj.put("item-id", p.IDCiabattaProperty());
        return obj;
    }

    public static void addEntry(Prestito p) {
        try {
            // write the json object to the dataFile
            FileWriter fileWriter = new FileWriter(dataFile);
            fileWriter.write(buildJSONObject(p).toString());
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeEntry(Prestito p) {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) parser.parse(new FileReader(dataFile));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.get(i).equals(buildJSONObject(p)))
                    jsonArray.remove(i);
            }

            // writes the whole array in the .json file
            FileWriter fileWriter = new FileWriter(dataFile);

            fileWriter.write(jsonArray.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
