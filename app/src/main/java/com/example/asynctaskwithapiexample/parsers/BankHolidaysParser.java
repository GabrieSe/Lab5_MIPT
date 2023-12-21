package com.example.asynctaskwithapiexample.parsers;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class BankHolidaysParser {
    public static String getBankHolidaysData(InputStream stream) throws IOException {
        String result = new String();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder jsonResult = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonResult.append(line);
            }

            try {
                JSONObject jsonData = new JSONObject(jsonResult.toString());
                JSONObject divisionNode = jsonData.getJSONObject("division");
                JSONArray events = divisionNode.getJSONArray("events");

                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    String title = event.getString("title");
                    String date = event.getString("date");
                    String notes = event.optString("notes", "");
                    boolean bunting = event.getBoolean("bunting");

                    result += (String.format("Title: %s,\nDate: %s,\nNotes: %s,\nBunting: %s\n",
                            title, date, notes, bunting));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}