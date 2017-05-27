package controllers;

import Exceptions.EditException;
import Models.*;
import Readers.ReadResult;
import com.opencsv.CSVWriter;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Nikolay Kanatov on 01.03.2017.
 */

public class EditDocController{


    public TableView fileTableView = new TableView<>();


    public Items itemsVector;
    public Orders ordersVector;
    public Goods goodsVector;
    public Parameters parametersVector;
    public Storekeepers storekeepersVector;
    public CSVWriter writer = null;

    public final ObservableList resultList = FXCollections.observableArrayList();

    public ReadResult result=new ReadResult();

    private Stage stage;


    public void setResult(String fileName,String filePath){
        Scene scene = new Scene(new Group());
        try {
            Stage stage = new Stage();
            boolean combo=false;
            int countField=0;
            switch (fileName){
                case "Items.csv":
                    itemsVector = new Items(filePath);
                    resultList.addAll(itemsVector.toItemsArray());
                    countField = itemsVector.getItem(0).getCountField();
                    break;
                case "Orders.csv":
                    ordersVector = new Orders(filePath);
                    resultList.addAll( ordersVector.toOrdersArray());
                    countField = ordersVector.getOrder(0).getCountField();
                    break;
                case "Goods.csv":
                    goodsVector = new Goods(filePath);
                    resultList.addAll( goodsVector.toGoodsArray());
                    countField = goodsVector.getGood(0).getCountField();
                    break;
                case "Parameters.csv":
                    parametersVector = new Parameters(filePath);
                    resultList.addAll( parametersVector.toParametersArray());
                    countField = parametersVector.getParameter(0).getCountField();
                    break;
                case "Result.csv":
                    result.readResult(filePath);
                    result.readLog(filePath.replace("Result.csv","test_log.1"));
                    storekeepersVector=result.getListStorekeeper();
                    combo=true;
                    drawStKeeper(scene,stage,storekeepersVector);
                    break;
            }
                FillTable(resultList, countField);

                fileTableView.setPrefWidth(600);

                final HBox hbRow = new HBox();
                TextField[] tx = new TextField[countField];

                for (int i = 0; i < countField; i++) {
                    tx[i] = new TextField();
                    tx[i].setMaxHeight(20);
                    tx[i].setMaxWidth(((TableColumn) (fileTableView.getColumns().get(i))).getPrefWidth());
                    tx[i].setId(((TableColumn) (fileTableView.getColumns().get(i))).getText());
                    hbRow.getChildren().add(tx[i]);
                }
                hbRow.setSpacing(5);

                final HBox hbBut = new HBox();
                Button addButton = new Button("Add row");
                Button deleteButton = new Button("Delete");
                Button saveButton = new Button("Save");

                saveButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            writer = new CSVWriter(new FileWriter(filePath), ';');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        switch (resultList.get(0).getClass().getName()) {
                            case "Models.ItemModel":
                                writer.writeNext(itemsVector.getHeaders(), false);
                                writer.writeAll(itemsVector.toStringList(), false);
                                break;
                            case "Models.GoodModel":
                                writer.writeNext(goodsVector.getHeaders(), false);
                                writer.writeAll(goodsVector.toStringList(), false);
                                break;
                            case "Models.OrderModel":
                                writer.writeNext(ordersVector.getHeaders(), false);
                                writer.writeAll(ordersVector.toStringList(), false);
                                break;
                            case "Models.ParameterModel":
                                writer.writeNext(parametersVector.getHeaders(), false);
                                writer.writeAll(parametersVector.toStringList(), false);
                                break;
                        }
                        try {
                            writer.close();
                            AlertDone();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                addButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String tmp = "";
                        try {
                            writer = new CSVWriter(new FileWriter(filePath), ';');
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        for (int i = 0; i < tx.length; i++)
                            if (tx[i].getText().isEmpty()) {
                                tmp = "";
                                return;
                            } else {
                                tmp = tmp.concat(tx[i].getText() + ";");
                                tx[i].setText("");
                            }

                        if (Validate(resultList.get(0).getClass().getName(), tmp)) {
                            switch (resultList.get(0).getClass().getName()) {
                                case "Models.ItemModel":
                                    resultList.add(new ItemModel(tmp));
                                    itemsVector.addItem(new ItemModel(tmp));
                                    writer.writeNext(itemsVector.getHeaders(), false);
                                    writer.writeAll(itemsVector.toStringList(), false);
                                    break;
                                case "Models.GoodModel":
                                    resultList.add(new GoodModel(tmp));
                                    goodsVector.addGood(new GoodModel(tmp));
                                    writer.writeNext(goodsVector.getHeaders(), false);
                                    writer.writeAll(goodsVector.toStringList(), false);
                                    break;
                                case "Models.OrderModel":
                                    resultList.add(new OrderModel(tmp));
                                    ordersVector.addOrder(new OrderModel(tmp));
                                    writer.writeNext(ordersVector.getHeaders(), false);
                                    writer.writeAll(ordersVector.toStringList(), false);
                                    break;
                                case "Models.ParameterModel":
                                    resultList.add(new ParameterModel(tmp));
                                    parametersVector.addParameter(new ParameterModel(tmp));
                                    writer.writeNext(parametersVector.getHeaders(), false);
                                    writer.writeAll(parametersVector.toStringList(), false);
                                    break;
                            }
                            try {
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText(null);
                            alert.setContentText("Wrong input!");

                            alert.showAndWait();
                        }
                    }
                });
                deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (fileTableView.getSelectionModel().getSelectedIndex() >= 0) {
                            resultList.remove(fileTableView.getSelectionModel().getSelectedIndex());
                            try {
                                writer = new CSVWriter(new FileWriter(filePath), ';');
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            switch (fileTableView.getSelectionModel().getSelectedItem().getClass().getName()) {
                                case "Models.ItemModel":
                                    itemsVector.deleteItem(fileTableView.getSelectionModel().getSelectedIndex());
                                    writer.writeNext(itemsVector.getHeaders(), false);
                                    writer.writeAll(itemsVector.toStringList(), false);
                                    break;
                                case "Models.GoodModel":
                                    goodsVector.deleteItem(fileTableView.getSelectionModel().getSelectedIndex());
                                    writer.writeNext(goodsVector.getHeaders(), false);
                                    writer.writeAll(goodsVector.toStringList(), false);
                                    break;
                                case "Models.OrderModel":
                                    ordersVector.deleteItem(fileTableView.getSelectionModel().getSelectedIndex());
                                    writer.writeNext(ordersVector.getHeaders(), false);
                                    writer.writeAll(ordersVector.toStringList(), false);
                                    break;
                                case "Models.ParameterModel":
                                    parametersVector.deleteItem(fileTableView.getSelectionModel().getSelectedIndex());
                                    writer.writeNext(parametersVector.getHeaders(), false);
                                    writer.writeAll(parametersVector.toStringList(), false);
                                    break;
                            }
                            try {
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


                hbBut.getChildren().addAll(addButton, deleteButton, saveButton);
                hbBut.setSpacing(5);

                final VBox vbox = new VBox();
                vbox.setSpacing(5);
                vbox.setPadding(new Insets(10, 10, 0, 10));
                vbox.setFillWidth(true);
                vbox.setMinWidth(fileTableView.getFixedCellSize() * 5);
                vbox.getChildren().addAll(fileTableView, hbRow, hbBut);

                ((Group) scene.getRoot()).getChildren().addAll(vbox);
                stage.setTitle(fileName);
                stage.setMinHeight(700);
                stage.setMinWidth(800);
                stage.setScene(scene);
                stage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }


    public void FillTable(ObservableList resultList,int countField) {
        fileTableView.setItems(resultList);
        fileTableView.setEditable(true);
        String str =resultList.get(0).getClass().getName();
        for(int i=0;i<countField;i++) {
            TableColumn tableColumn=null;
            switch(str){
                case "Models.ItemModel":
                    tableColumn = new TableColumn(itemsVector.getHeaders()[i]);
                    tableColumn.setCellValueFactory(
                            new PropertyValueFactory<ItemModel,StringBinding>(itemsVector.getHeaders()[i]));
                    tableColumn.setCellFactory(TextFieldTableCell.<ParameterModel> forTableColumn());
                    tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent event) {
                            TablePosition<ItemModel, String> pos = event.getTablePosition();
                            String newParameter = (String)event.getNewValue();
                            int row = pos.getRow();
                            ItemModel model = (ItemModel) event.getTableView().getItems().get(row);
                            try {
                                model.applyEdit(newParameter,pos.getColumn());
                            } catch (EditException e) {
                                AlertError(e.toString());
                            }
                        }
                    });
                    break;
                case "Models.GoodModel":
                    tableColumn = new TableColumn(goodsVector.getHeaders()[i]);
                    tableColumn.setCellValueFactory(
                            new PropertyValueFactory<GoodModel,StringBinding>(goodsVector.getHeaders()[i]));
                    tableColumn.setCellFactory(TextFieldTableCell.<ParameterModel> forTableColumn());
                    tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent event) {
                            TablePosition<GoodModel, String> pos = event.getTablePosition();
                            String newParameter = (String)event.getNewValue();
                            int row = pos.getRow();
                            GoodModel model = (GoodModel) event.getTableView().getItems().get(row);
                            try {
                                model.applyEdit(newParameter,pos.getColumn());
                            } catch (EditException e) {
                                AlertError(e.toString());
                            }
                        }
                    });
                    break;
                case "Models.OrderModel":
                    tableColumn = new TableColumn(ordersVector.getHeaders()[i]);
                    tableColumn.setCellValueFactory(
                            new PropertyValueFactory<OrderModel,StringBinding>(ordersVector.getHeaders()[i]));
                    tableColumn.setCellFactory(TextFieldTableCell.<ParameterModel> forTableColumn());
                    tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent event) {
                            TablePosition<OrderModel, String> pos = event.getTablePosition();
                            String newParameter = (String)event.getNewValue();
                            int row = pos.getRow();
                            OrderModel model = (OrderModel) event.getTableView().getItems().get(row);
                            try {
                                model.applyEdit(newParameter,pos.getColumn());
                            } catch (EditException e) {
                                AlertError(e.toString());
                            }
                        }
                    });
                    break;
                case "Models.ParameterModel":
                    tableColumn = new TableColumn(parametersVector.getHeaders()[i]);
                    tableColumn.setCellValueFactory(
                            new PropertyValueFactory<ParameterModel,StringBinding>(parametersVector.getHeaders()[i]));
                    tableColumn.setCellFactory(TextFieldTableCell.<ParameterModel> forTableColumn());
                    tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                        @Override
                        public void handle(TableColumn.CellEditEvent event) {
                            TablePosition<ParameterModel, String> pos = event.getTablePosition();
                            String newParameter = (String)event.getNewValue();
                            int row = pos.getRow();
                            ParameterModel model = (ParameterModel) event.getTableView().getItems().get(row);
                            try {
                                model.applyEdit(newParameter,pos.getColumn());
                            } catch (EditException e) {
                                AlertError(e.toString());
                            }
                        }
                    });
                    break;
            }
            fileTableView.getColumns().add(tableColumn);
        }
    }

    public boolean Validate(String type, String model){
        String[] tmp = model.split(";");
        switch (type){
            case "Models.ItemModel":
                try {
                    //tmp[] = MO,Index,Count,Volume,Rigidity
                    Long.parseLong(tmp[1]);
                    Integer.parseInt(tmp[2]);
                    Double.parseDouble(tmp[3]);
                    Integer.parseInt(tmp[4]);
                }catch (Exception ex){
                    return false;
                }
                break;
            case "Models.OrderModel":
                try{
                    //tmp[] = indexOfShop, wtf, deadline, DeliverySide
                    Long.parseLong(tmp[0]);
                    //add validate time here
                    if (!(tmp[3].equalsIgnoreCase("south") || tmp[3].equalsIgnoreCase("north")))
                        return false;
                }catch (Exception ex){
                    return false;
                }
                break;
            case "Models.GoodModel":
                try{
                    //tmp[] = "IndexOfShop","IndexOfProduct","InPieces","InLiters", "InBoxes", "OneZero"
                    Long.parseLong(tmp[0]);
                    Long.parseLong(tmp[1]);
                    Float.parseFloat(tmp[2]);
                    Float.parseFloat(tmp[3]);
                    Integer.parseInt(tmp[4]);
                    Integer.parseInt(tmp[5]);
                }catch (Exception ex){
                    return false;
                }
                break;
            case "Models.ParameterModel":
                break;
        }
        return true;
    }


    public void AlertError(String error){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }
    public void AlertDone(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Done!");
        alert.showAndWait();
    }
}
