package Readers;

import Models.Point;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringJoiner;
import java.util.Vector;

/**
 * Created by Ксения Горская on 23.01.2017.
 */
public class Warehouse {
    private static Warehouse instance;
    private Integer directionOfMovement;
    private Vector<Point> northDelivery;
    private Vector<Point> southDelivery;
    public int numberOfColumns;
    public Integer[] numberOfCells;
    private Vector<Point> coordinatesUpperLeftVertexRow;
    private Vector<Point> coordinatesLowerRightVertexRow;
    public ArrayList<Vector<Vector<String>>> shelves;

    String line = "";
    BufferedReader br = null;

    public static Warehouse getInstance(){
        return instance;
    }
    public Warehouse() {
        super();
        northDelivery = new Vector<Point>();
        southDelivery = new Vector<Point>();
        numberOfColumns = 0;
        numberOfCells = new Integer[0];
        coordinatesUpperLeftVertexRow = new Vector<Point>();
        coordinatesLowerRightVertexRow = new Vector<Point>();
    }

    public void clearAll() {
        try {
            northDelivery.clear();
            southDelivery.clear();
            coordinatesUpperLeftVertexRow.clear();
            coordinatesLowerRightVertexRow.clear();
        } catch (NullPointerException npe) {

        }
    }

    public Point createPointTmp(String[] elements, int i){
        Point tmp = new Point(0,0);
        String x="", y="";
        if (elements.length >= 1 || (elements[i].startsWith("(") && elements[i+1].endsWith(")"))) {
            x = elements[i].replace(",", ".").substring(2);
            y = elements[i + 1].replace(",", ".").substring(0, elements[i + 1].length() - 2);
        }
        try {
            tmp = new Point(x,y);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public void readExpedition(Vector<Point> delivery) throws Exception {
        line = br.readLine();
        line = br.readLine();
        String[] elements = line.split(";");
        for (int i = 0; i < elements.length; i+=2) {
            Point tmp = createPointTmp(elements, i);
            delivery.add(tmp);
        }
    }

    public void readCoordinates(Vector<Point> coordinatesVertexRow) throws Exception {
        String[] elements = line.split(";");
        for (int i = 1; i < elements.length; i+=2) {
            Point tmp = createPointTmp(elements, i);
            coordinatesVertexRow.add(tmp);
        }
    }

    public void readShelves(int numberOfColumns) throws Exception {
        shelves = new ArrayList<Vector<Vector<String>>>(numberOfColumns);
        for (int i = 0; i < numberOfColumns; i++){
            shelves.add(new Vector<Vector<String>>());
        }
        boolean emptyField = true;
        int index = 0;
        while ((line = br.readLine()) != null) {
            String[] rows = line.split(";");
            index++;
            Vector<String> mycells = new Vector<>();
            for (int i=1; i<rows.length; i++){
                if (emptyField) {     //(rows[i].contains("Пустая тара"))
                    mycells = new Vector<>();
                    mycells.add("Пусто");
                    shelves.get(i - 1).add(mycells);
                }
                else {
                    if (index>2){
                        String[] subcells1 = shelves.get(i-1).get(shelves.get(i-1).size()-1).get(0).split("-");
                        String[] subcells2 = rows[i].split("-");
                        if (subcells2[0].contains(subcells1[0]) && subcells2[1].contains(subcells1[1])){
                            shelves.get(i-1).get(shelves.get(i-1).size()-1).add(rows[i]);
                        }
                        else{
                            mycells.add(rows[i]);
                            if (rows[i].length() != 0) shelves.get(i - 1).add(mycells);
                            mycells = new Vector<>();
                        }
                    }
                    else {
                        mycells = new Vector<>();
                        mycells.add(rows[i]);
                        if (rows[i].length() != 0) shelves.get(i - 1).add(mycells);
                        mycells = new Vector<>();
                    }
                }
            }
            emptyField=false;
        }
    }

    public void readWarehouse(String filename) throws Exception {
        clearAll();
        try {
            br = new BufferedReader(new FileReader(filename));
            line = br.readLine();
            String[] elements = line.split(";");
            try {
                directionOfMovement = Integer.parseInt(elements[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            readExpedition(northDelivery);
            readExpedition(southDelivery);

            line = br.readLine();//read number of rows
            elements = line.split(";");
            try {
                numberOfColumns = Integer.parseInt(elements[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            line = br.readLine();
            line = br.readLine();
            elements = line.split(";");
            try {
                numberOfCells = new Integer[numberOfColumns];
                for(int i=1; i<elements.length;i++){
                    numberOfCells[i-1] = Integer.parseInt(elements[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            line = br.readLine();
            readCoordinates(coordinatesUpperLeftVertexRow);

            line = br.readLine();
            readCoordinates(coordinatesLowerRightVertexRow);

            line = br.readLine();
            readShelves(numberOfColumns);


        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done");
    }
}

