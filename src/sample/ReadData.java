package sample;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Ксения Горская on 21.01.2017.
 */
public class ReadData {

    public ArrayList<String[]> reader(String fileName) throws Exception {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        ArrayList<String[]> matrix = new ArrayList<String[]>();
        boolean headline = true;//headline contains names of columns
        boolean parametersFile = false;
        if (fileName.contains("Parameters")) parametersFile = true;
        if (fileName.contains("Warehouse")){
            Warehouse warehouse = new Warehouse();
            warehouse.readWarehouse(fileName);
            return matrix;
        }

        try {
            br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null) {
                String[] row = line.split(cvsSplitBy);
                if (parametersFile){
                    matrix.add(row);
                    parametersFile = false;
                }
                if (!headline){
                    matrix.add(row);
                }
                headline = false;
            }

            /*for (int i=0; i<matrix.size(); i++){
                for (int j=0; j<matrix.get(i).length; j++) {
                    System.out.print(matrix.get(i)[j] + " ");
                }
                System.out.println();
            }*/

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done");
        return matrix;
    }
}
