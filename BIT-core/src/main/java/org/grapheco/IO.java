package org.grapheco;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IO {
    public static ArrayList<long[]> loadData (String filepath){
        File csv = new File(filepath);
//        csv.setReadable(true);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        String everyLine = "";
        ArrayList<long[]> data = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null)
            {
                everyLine = line;
                String[] split = everyLine.split(",");
                data.add(new long[]{Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim())});
            }
            System.out.println("relationships: " + data.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static ArrayList<long[]> exampleData (){
        ArrayList<long []> example = new ArrayList<long[]>(11);
//        example.add(new int[]{1,1});
        example.add(new long[]{2,1});
        example.add(new long[]{3,1});
        example.add(new long[]{4,1});
        example.add(new long[]{5,1});
        example.add(new long[]{6,1});
        example.add(new long[]{7,1});
        example.add(new long[]{8,1});
        example.add(new long[]{9,8});
        example.add(new long[]{10,8});
        example.add(new long[]{11,8});
        return example;
    }

    public static void toCSV(String path, String[] header, String[][] data) {
        ICsvListWriter listWriter = null;
        try {
            listWriter = new CsvListWriter(new FileWriter(path),
                    CsvPreference.STANDARD_PREFERENCE);
            listWriter.writeHeader(header);
            for (String[] row : data) {
                listWriter.write(row);
            }
            listWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<String[]> read(File file) {
        try {
            CsvListReader reader = new CsvListReader(new FileReader(file),
                    CsvPreference.STANDARD_PREFERENCE);
            reader.getHeader(true);
            List<String[]> result = new ArrayList<>();
            List<String> line;
            while( (line = reader.read()) != null ) {
                result.add(line.toArray(new String[0]));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLog(String filename,String content){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(content);
            writer.newLine();
            System.out.println("Content appended to file successfully.");
        } catch (IOException e) {
            System.err.println("Error appending content to file: " + e.getMessage());
        }
    }

}
