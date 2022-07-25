import java.io.*;
import java.util.ArrayList;

public class IO {
    public static ArrayList<int[]> loadData (String filepath){
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
        ArrayList<int[]> data = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null)
            {
                everyLine = line;
                String[] split = everyLine.split(",");
                data.add(new int[]{Integer.parseInt(split[0].trim()), Integer.parseInt(split[1].trim())});
            }
            System.out.println("relationships: " + data.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static ArrayList<int[]> exampleData (){
        ArrayList<int []> example = new ArrayList<int[]>(11);
//        example.add(new int[]{1,1});
        example.add(new int[]{2,1});
        example.add(new int[]{3,1});
        example.add(new int[]{4,1});
        example.add(new int[]{5,1});
        example.add(new int[]{6,1});
        example.add(new int[]{7,1});
        example.add(new int[]{8,1});
        example.add(new int[]{9,8});
        example.add(new int[]{10,8});
        example.add(new int[]{11,8});
        return example;
    }
}
