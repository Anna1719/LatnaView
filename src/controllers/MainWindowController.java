package controllers;

import Models.StorekeeperModel;
import Readers.ReadResult;

import Models.Point;
import Models.StorekeeperModel;
import Models.Storekeepers;
import Readers.Warehouse;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

/**
 * Created by Nikolay Kanatov on 01.03.2017.
 */

public class MainWindowController {
    final Pane container = new Pane();
    @FXML
    public MenuBar MenuBarID;

    @FXML
    public ScrollPane WHouse;

    @FXML
    public ScrollPane Storekeepers;
    private ArrayList<Integer> Columns = new ArrayList<>();

    final int HighPos = 40;
    final int LowerPos = 61*17;
    Rectangle[] rect;
    Rectangle westRect;
    Rectangle eastRect;

    public void openMyFile()  throws Exception {

        MenuItem menuItem = new MenuItem();
        String FileName = null;
        String FilePath = null;
        JFileChooser fileopen = new JFileChooser();
        fileopen.setCurrentDirectory(new File("."));
        if (fileopen.showDialog(null, "Выбрать файл") == JFileChooser.APPROVE_OPTION) {
            for (MenuItem item:MenuBarID.getMenus().get(1).getItems()) {
                String tmp1 = item.getUserData().toString();
                String tmp2 =fileopen.getSelectedFile().getAbsolutePath();
                if(tmp1.equals(tmp2))
                    return;
            }
            FileName = fileopen.getSelectedFile().getName();
            FilePath=fileopen.getSelectedFile().getAbsolutePath();
            menuItem.setUserData(FilePath);
        }

        menuItem.setText(FileName);
        final String finalFilePath = FilePath;
        final String finalFileName = FileName;
        menuItem.setOnAction(event -> {
            try{
                if(menuItem.getText().equals("Warehouse.csv"))
                    GetWareHouse(finalFilePath);
                else if(menuItem.getText().equals("Result.csv"))
                    StorekeepersMenu(finalFilePath);
                else
                    ShowFile(finalFileName,finalFilePath);
            }
            catch (Exception ex){
              ex.printStackTrace();
            }

        });

        MenuBarID.getMenus().get(1).getItems().add(menuItem); //добавляет ко второму меню menuItem

    }
    Warehouse wr = new Warehouse();
    EditDocController edc = new EditDocController();

    public void ShowFile(String fileName,String filePath) throws IOException {

        edc.setResult(fileName,filePath);
    }




    public void GetWareHouse(String path) throws Exception {

        wr.readWarehouse(path);

        eastRect = new Rectangle();
        eastRect.setWidth(50);
        eastRect.setHeight(50);
        VBox nvb = new VBox();
        nvb.getChildren().add(eastRect);

        westRect = new Rectangle();
        westRect.setWidth(50);
        westRect.setHeight(50);
        VBox svb = new VBox();
        svb.getChildren().add(westRect);

        rect = new Rectangle[61*wr.numberOfColumns+1];
        VBox[] vb = new VBox[wr.numberOfColumns+1];

        container.setCenterShape(true);
        container.setPadding(new Insets(0,0,50,0));
        container.getChildren().add(svb);

        for(int i =0;i<wr.numberOfColumns;i++) {
            vb[i]=new VBox();
            for (int j = 0; j < 61; j++) {
                rect[(61*i) + j] = new Rectangle();
                rect[(61*i) + j].setWidth(20);
                rect[(61*i) + j].setHeight(15);
                rect[(61*i) + j].setStrokeWidth(1);
                rect[(61*i) + j].setFill(Color.AZURE);
                rect[(61*i) + j].setStroke(Color.BLACK);
                rect[(61*i) +j].setUserData(wr.shelves.get(i).get(j));
                rect[(61*i) +j].setOnMouseClicked(getInfoShelvesEvent());
                vb[i].getChildren().add(rect[(61*i) + j]);
            }

            if(i>0) {
                if (i % 2 == 0) {
                    vb[i].setLayoutX(vb[i - 1].getLayoutX() + 20);
                    Columns.add((int) vb[i].getLayoutX()+30);
                }else{
                    vb[i].setLayoutX(vb[i - 1].getLayoutX() + 40);
                    Columns.add((int) vb[i].getLayoutX()-10);

                }
            }else{
                vb[0].setLayoutX(65);
                Columns.add((int) vb[i].getLayoutX()+30);
            }

            vb[i].setLayoutY(50);

            container.getChildren().add(vb[i]);
        }
        nvb.setLayoutX(vb[wr.numberOfColumns-1].getLayoutX()+30);
        container.getChildren().add(nvb);

        Columns.add((int) svb.getLayoutX());
        Columns.add((int) nvb.getLayoutX());
        WHouse.setContent(container);
        BuildAllWay(1);
    }

    public ArrayList<Integer> FindShelve(String name){
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Vector<Vector<String>>> shelves = wr.getShelves();
        for(int i=0;i<wr.numberOfColumns;i++){
            for(int j=0;j<shelves.get(i).size();j++){
                for(int k=0;k<shelves.get(i).get(j).size();k++){
                    String str = shelves.get(i).get(j).get(k);
                    if(str.equals(name)){
                        list.add(i);
                        list.add(j);
                        return list;
                    }
                }
            }
        }
        return null;

    }

    Point currentPos = new Point(0,0);
    public void BuildAllWay(int index){
        Storekeepers st = edc.result.getListStorekeeper();
        StorekeeperModel s = st.getStorekeeper(index);
        container.getChildren().addAll(BuildWay(s,1,Color.rgb(255-1,0,1)));
    }

    public ArrayList<Line> BuildWay(StorekeeperModel s, int taskId, Color color){
        ArrayList<Line> LineList = new ArrayList<>();
        ArrayList<String[]>tmp2 = s.getTasks().get(taskId).getListTask();

        tmp2.sort((o1, o2) -> {
            if(o1[0].compareTo(o2[0])<0)
                return -1;
            if(o1[0].compareTo(o2[0])>0)
                return 1;
            return 0;
        });
        Random rand = new Random();

        Color[] col = new Color[30];
        for(int i=0;i<30;i++){
            col[i]=Color.rgb(rand.nextInt(255)+1,rand.nextInt(255)+1,rand.nextInt(255)+1);
        }
        int Size = s.getTasks().get(taskId).getListTask().size();

        if(taskId>0)
            currentPos = new Point(Columns.get(FindShelve(s.getTasks().get(taskId).getListTask().get(0)[0]).get(0)), HighPos);
        else
            currentPos = new Point(Columns.get(0), HighPos);

        Point start = currentPos;

        Point previous = new Point(start.getX(),start.getY());

        for(int i=0;i<Size;i++){
            ArrayList<Integer> shelve = FindShelve(s.getTasks().get(taskId).getListTask().get(i)[0]);

            rect[61*shelve.get(0)+shelve.get(1)].setFill(Color.DARKRED);
            //если надо идти на другой ряд
            if(start.getX()!=Columns.get(shelve.get(0))){
                Line tmp=null,tmp3;
                //доходим до нижней или верхней точки
                if(start.getY()!=HighPos && start.getY()!=LowerPos){
                    if(previous.getY()>start.getY()){
                        tmp = new Line(start.getX(),start.getY(),start.getX(),HighPos);
                        start.setY(HighPos);
                    }else{
                        tmp = new Line(start.getX(),start.getY(),start.getX(),LowerPos);
                        start.setY(LowerPos);
                    }
                }
                //переходим на другой ряд
                tmp3 = new Line(start.getX(),start.getY(),Columns.get(shelve.get(0)),start.getY());
                start.setX(Columns.get(shelve.get(0)));

                tmp.setFill(Color.RED);
                LineList.add(tmp);
                LineList.add(tmp3);
                previous.setY(start.getY());
            }

            //двигаемся к конкретной ячейке
            Line line2=null;
            if(previous.getY()==HighPos){
                line2 =new Line(start.getX(),start.getY(),start.getX(),(shelve.get(1))*16+50+7);
                start.setY((shelve.get(1))*16+50+7); // с 50 начина.тся полки
            }
            if(previous.getY()==LowerPos){
                line2 =new Line(start.getX(),start.getY(),start.getX(),(shelve.get(1))*16+50+7);
                start.setY((shelve.get(1))*16+50+7); // с 50 начина.тся полки
            }
            line2.setStroke(Color.BLACK);//(col[i]);
            LineList.add(line2);

            //поворачиваемся и подходим к ячейке
            Line line3;
            if(shelve.get(0)%2==0)
                line3 = new Line(start.getX(),start.getY(),start.getX()-5,start.getY());
            else
                line3=new Line(start.getX(),start.getY(),start.getX()+5,start.getY());
            LineList.add(line3);

        }
        LineList.addAll(goToUnloading(start));
        return LineList;
    }

    private ArrayList<Line> goToUnloading(Point start){
        ArrayList<Line> list = new ArrayList<>();
        if(start.getY()!=HighPos){
            Line tmp = new Line(start.getX(),start.getY(),start.getX(),HighPos);
            start.setY(HighPos);
            list.add(tmp);
        }
        Line line = null;
        if(Math.abs(Columns.get(wr.numberOfColumns) - start.getX()) < Math.abs(Columns.get(wr.numberOfColumns+1) - start.getX())){
            line = new Line(start.getX(),start.getY(), Columns.get(wr.numberOfColumns), westRect.getHeight()/2);
        }else{
            line = new Line(start.getX(),start.getY(), Columns.get(wr.numberOfColumns+1), eastRect.getHeight()/2);
        }
        list.add(line);
        return list;
    }
    private Line UpDown(Point currentPos, int row) {
            return new Line(currentPos.getX(),currentPos.getY(),currentPos.getX(),row*15);
    }

   void StorekeepersMenu(String path) throws Exception {
        ReadResult result=new ReadResult();
        result.readResult(path);
        result.readLog(path.replace("Result.csv","test_log.1"));
        Models.Storekeepers storekeepersVector=result.listStorekeeper;;

        ComboBox stKeeperComboBox = new ComboBox();
        for (int i = 1; i < storekeepersVector.toStringList().size(); i++) {
            stKeeperComboBox.getItems().add("Storekeeper " + i);
        }
        stKeeperComboBox.setPromptText("Storekeepers : ");
        GridPane grid = new GridPane();
        grid.setVgap(30);
        grid.setHgap(30);
        grid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(stKeeperComboBox, 1, 0);

       Storekeepers.setContent(grid);

        ComboBox tasksComboBox = new ComboBox();

        stKeeperComboBox.setOnAction((event) -> {
            grid.getChildren().clear();
            grid.add(stKeeperComboBox, 1, 0);
            String[] vectorNames=storekeepersVector.getHeaders();
            ArrayList<Label> labels=new ArrayList<>();

            int selectedIndx = stKeeperComboBox.getSelectionModel().getSelectedIndex()+1;
            StorekeeperModel st=storekeepersVector.getStorekeeper(selectedIndx);
            ArrayList<String> values=new ArrayList<>();
            values.add(Integer.toString(st.getId()));
            values.add(Integer.toString(st.getNumberOfTask()));
            values.add(Double.toString(st.getSummLength()));
            values.add(Integer.toString(st.getSummTime()));
            values.add(Integer.toString(st.getTimeOnSort()));
            values.add(Integer.toString(st.getTimeOnMove()));
            String text=null;
            for (int i = 0; i < values.size(); i++) {
                Label l = new Label("");
                text=vectorNames[i]+" : "+values.get(i)+"\n";
                l.setText(text);
                if((i%2)==0) {
                    l.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                }
                l.setFont(Font.font(13));
                labels.add(l);
            }
            for (int i = 1; i < storekeepersVector.getStorekeeper(selectedIndx).getTasks().size(); i++) {
                tasksComboBox.getItems().add("Task " + storekeepersVector.getStorekeeper(selectedIndx).getTasks().get(i).getId());
            }

            int i=0;
            for (i=0; i < labels.size(); i++) {
                grid.add(labels.get(i),1,i+1);
            }
            tasksComboBox.setPromptText("Tasks : ");
            grid.add(tasksComboBox,1,i+1);

            Storekeepers.setContent(grid);
        });
    }

    public EventHandler<MouseEvent> getInfoShelvesEvent(){
        return event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(((Rectangle)event.getSource()).getUserData().toString());
            alert.show();
        };
    }
}
