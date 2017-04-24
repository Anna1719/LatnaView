package controllers;

import Models.StorekeeperModel;
import Readers.ReadResult;
import Readers.Warehouse;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nikolay Kanatov on 01.03.2017.
 */

public class MainWindowController {

    @FXML
    public MenuBar MenuBarID;

    @FXML
    public ScrollPane WHouse;

    @FXML
    public ScrollPane Storekeepers;

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

    public void ShowFile(String fileName,String filePath) throws IOException {

        EditDocController edc = new EditDocController();
        edc.setResult(fileName,filePath);
    }

    public void GetWareHouse(String path) throws Exception {
        Warehouse wr = new Warehouse();
        wr.readWarehouse(path);

        Rectangle[] rect = new Rectangle[61*wr.numberOfColumns+1];
        VBox[] vb = new VBox[wr.numberOfColumns+1];
        final Pane container = new Pane();

        for(int i =0;i<wr.numberOfColumns;i++) {
            vb[i]=new VBox();
            for (int j = 0; j < 61; j++) {
                rect[(61*i) + j] = new Rectangle();
                rect[(61*i) + j].setWidth(20);
                rect[(61*i) + j].setHeight(15);
                rect[(61*i) + j].setFill(Color.AZURE);
                rect[(61*i) + j].setStroke(Color.BLACK);
                rect[(61*i) +j].setUserData(wr.shelves.get(i).get(j));
                rect[(61*i) +j].setOnMouseClicked(getInfoShelvesEvent());
                vb[i].getChildren().add(rect[(61*i) + j]);
            }

            if(i>0) {
                if ((i+1) % 3 == 0)
                    vb[i].setLayoutX(vb[i - 1].getLayoutX() + 20);
                else
                    vb[i].setLayoutX(vb[i - 1].getLayoutX() + 40);
            }else{
                vb[0].setLayoutX(20);
            }
            vb[i].setLayoutY(50);
            container.getChildren().add(vb[i]);
        }
        WHouse.setContent(container);
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
