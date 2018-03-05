package it.teamunivr.powerstrips;

import it.teamunivr.powerstrips.model.Loan;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class LoanSave {

    private File dataFile;

    public LoanSave(java.nio.file.Path path) {
        this.dataFile = new File(path.toString());
    }

    private JSONObject buildJSONObject(Loan p) {
        JSONObject obj = new JSONObject();
        obj.put("name", p.getName());
        obj.put("surname", p.getLastName());
        obj.put("item-id", p.getPowerStripID());
        return obj;
    }

    public static void resetLoanSaveFile(String saveFile) throws IOException {
        FileWriter fileWriter = new FileWriter(saveFile);
        fileWriter.write("{\n\t\"loans\":[\n\t]\n}");
        fileWriter.flush();
        fileWriter.close();
    }

    public void addEntry(Loan l) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject loansObject = (JSONObject) parser.parse(new FileReader(dataFile));
            JSONArray loansArray = (JSONArray) loansObject.get("loans");

            if (loansArray == null) {
                System.out.println("Error reading save file, Resetting save file.");
                resetLoanSaveFile(dataFile.toString());
                loansObject = (JSONObject) parser.parse(new FileReader(dataFile));
            }

            loansArray.add(buildJSONObject(l));

            FileWriter fileWriter = new FileWriter(dataFile);
            fileWriter.write(loansObject.toString());
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void removeEntry(Loan p) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        JSONArray loansArray = null;
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(dataFile));

            if (null == jsonObject.get("loans")) {
                System.out.println("Error reading save file, Resetting save file.");
                resetLoanSaveFile(dataFile.toString());
                jsonObject = (JSONObject) parser.parse(new FileReader(dataFile));
            }

            loansArray = (JSONArray) jsonObject.get("loans");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            for (int i = 0; i < loansArray.size(); i++) {
                if (loansArray.get(i).equals(buildJSONObject(p)))
                    loansArray.remove(i);
            }

            // writes the whole array in the .json file
            FileWriter fileWriter = new FileWriter(dataFile);

            fileWriter.write(jsonObject.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Loan> getLoans() {
        ArrayList<Loan> ret = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        JSONArray loansArray;

        try {
            jsonObject = (JSONObject) parser.parse(new FileReader(dataFile));

            if (null == jsonObject.get("loans")) {
                System.out.println("Error reading save file, Resetting save file.");
                resetLoanSaveFile(dataFile.toString());
                jsonObject = (JSONObject) parser.parse(new FileReader(dataFile));
            }

            loansArray = (JSONArray) jsonObject.get("loans");

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }

        for (Object loanObject : loansArray) {
            JSONObject loanJSONObject = (JSONObject) loanObject;

            ret.add(
                    new Loan((String) loanJSONObject.get("name"), (String) loanJSONObject.get("surname"), (String) loanJSONObject.get("item-id"))
            );
        }

        return ret;
    }
}
