package pl.project.oop2.shoppinglistgui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;


public class ShoppingListApp extends Application {

    final int FONT_SIZE = 18;

    private TextField productNameField;
    private TextField quantityField;
    private ComboBox<String> unitField;
    private CheckBox isIntegerCheckBox;
    private TableView<Product> productTable;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Shopping List");

        Label productNameLabel = new Label("Product Name:");
        productNameLabel.setFont(new Font(FONT_SIZE));

        Label quantityLabel = new Label("Quantity:");
        quantityLabel.setFont(new Font(FONT_SIZE));

        Label unitLabel = new Label("Unit:");
        unitLabel.setFont(new Font(FONT_SIZE));

        productNameField = new TextField();
        productNameField.setPromptText("Product Name");
        productNameField.setMaxWidth(200);

        quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setMaxWidth(200);

        unitField = new ComboBox<>();
        unitField.getItems().addAll("Kg", "L", "g", "ml", "szt");
        unitField.setPromptText("Unit");

        isIntegerCheckBox = new CheckBox("Is Integer");
        isIntegerCheckBox.setFont(new Font(FONT_SIZE));

        Button addButton = new Button("Add Product");
        addButton.setPrefWidth(175);
        addButton.setFont(new Font(FONT_SIZE));
        addButton.setStyle("-fx-background-color: #26940E; -fx-background-radius: 15; -fx-border-radius: 15;");
        addButton.setOnAction(e -> addProduct());

        Button deleteButton = new Button("Delete Product");
        deleteButton.setStyle("-fx-background-color: #B51919; -fx-background-radius: 15; -fx-border-radius: 15;");
        deleteButton.setPrefWidth(175);
        deleteButton.setFont(new Font(FONT_SIZE));
        deleteButton.setOnAction(e -> deleteProduct());

        Button clearButton = new Button("Clear Input");
        clearButton.setStyle("-fx-background-color: #33AECA; -fx-background-radius: 15; -fx-border-radius: 15;");
        clearButton.setPrefWidth(175);
        clearButton.setFont(new Font(FONT_SIZE));
        clearButton.setOnAction(e -> {
            productNameField.clear();
            quantityField.clear();
            unitField.setValue(null);
            isIntegerCheckBox.setSelected(false);
        });

        Button updateButton = new Button("Update Product");
        updateButton.setStyle("-fx-background-color: #D1B500; -fx-background-radius: 15; -fx-border-radius: 15;");
        updateButton.setPrefWidth(175);
        updateButton.setFont(new Font(FONT_SIZE));
        updateButton.setOnAction(e -> updateProduct());

        productTable = new TableView<>();

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setResizable(false);
        nameColumn.setPrefWidth(200);

        TableColumn<Product, Number> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setResizable(false);
        quantityColumn.setPrefWidth(80);

        TableColumn<Product, String> unitColumn = new TableColumn<>("Unit");
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        unitColumn.setResizable(false);
        unitColumn.setPrefWidth(50);

        TableColumn<Product, Boolean> isIntegerColumn = new TableColumn<>("Is Int");
        isIntegerColumn.setCellValueFactory(new PropertyValueFactory<>("isInteger"));
        isIntegerColumn.setResizable(false);
        isIntegerColumn.setPrefWidth(50);

        productTable.getColumns().setAll(nameColumn, quantityColumn, unitColumn, isIntegerColumn);
        productTable.setStyle("-fx-border-color: #000000; -fx-border-width: 2px; -fx-background-color: #ffffff; -fx-border-style: solid;");

        HBox productNameBox = new HBox(productNameLabel, productNameField);
        productNameBox.setSpacing(10);

        HBox quantityBox = new HBox(quantityLabel, quantityField);
        quantityBox.setSpacing(10);

        HBox unitAndIsIntegerBox = new HBox(unitLabel, unitField, isIntegerCheckBox);
        unitAndIsIntegerBox.setSpacing(10);
        isIntegerCheckBox.setPadding(new Insets(0, 0, 0, 90));

        HBox buttonsUpBox = new HBox(addButton, clearButton);
        buttonsUpBox.setSpacing(30);

        HBox buttonsDownBox = new HBox(deleteButton, updateButton);
        buttonsDownBox.setSpacing(30);

        VBox vbox = new VBox(productNameBox, quantityBox, unitAndIsIntegerBox, buttonsUpBox, buttonsDownBox, productTable);
        vbox.setSpacing(10);
        vbox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.setPadding(new Insets(20, 20, 20, 20));
        Scene scene = new Scene(vbox, 426, 600);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        populateProductTable();
    }

    private void updateProduct() {
        Product product = productTable.getSelectionModel().getSelectedItem();
        if (product == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Please select a product to update");
            alert.showAndWait();
            return;
        }

        if (checkIfAnyFieldIsFilled()) return;

        String name = productNameField.getText().isEmpty() ? product.getName() : productNameField.getText();
        try {
            Double tmp = null;
            if (!quantityField.getText().isEmpty()) {
                tmp = Double.parseDouble(quantityField.getText());
            }
            if(tmp != null && tmp <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setContentText("Quantity must be a positive number");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Quantity must be a number");
            alert.showAndWait();
            return;
        }
        double quantity = quantityField.getText().isEmpty() ? product.getQuantity() : Double.parseDouble(quantityField.getText());
        String unit = unitField.getValue() == null ? product.getUnit() : unitField.getValue();
        boolean isInteger = isIntegerCheckBox.isSelected();
        System.out.println(product);
        System.out.println(name + " " + quantity + " " + unit + " " + isInteger);

        String jsonInputString = String.format("{\"isInteger\": %b, \"name\":\"%s\", \"quantity\": %s, \"unit\":\"%s\"}",
                isInteger, name, quantity, unit);

        try {
            URL url = new URL("http://localhost:8080/api/products/" + product.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);
            conn.getOutputStream().write(jsonInputString.getBytes("utf-8"));

            if (conn.getResponseCode() == 200) {
                populateProductTable();
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        populateProductTable();

        productNameField.clear();
        quantityField.clear();
        unitField.setValue(null);
        isIntegerCheckBox.setSelected(false);
    }

    private boolean checkIfAnyFieldIsFilled() {
        if (productNameField.getText().isEmpty() && quantityField.getText().isEmpty() && unitField.getValue() == null && !isIntegerCheckBox.isSelected()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Please fill at least one field");
            alert.showAndWait();
            return true;
        }
        return false;
    }


    private void deleteProduct() {
        Product product = productTable.getSelectionModel().getSelectedItem();
        if (product == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Please select a product to delete");
            alert.showAndWait();
            return;
        }

        try {
            URL url = new URL("http://localhost:8080/api/products/" + product.getId());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");

            if (conn.getResponseCode() == 204) {
                System.out.println("Product deleted");
                populateProductTable();
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        if (checkIfInputFieldsAreFilled()) return;

        String name = productNameField.getText();
        try {
            Double tmp = Double.parseDouble(quantityField.getText());
            if(tmp <= 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setContentText("Quantity must be a positive number");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Quantity must be a number");
            alert.showAndWait();
            return;
        }
        double quantity = Double.parseDouble(quantityField.getText());
        String unit = unitField.getValue();
        boolean isInteger = isIntegerCheckBox.isSelected();

        String jsonInputString = String.format("{\"isInteger\": %b, \"name\":\"%s\", \"quantity\": %s, \"unit\":\"%s\"}",
                isInteger, name, quantity, unit);

        try {
            URL url = new URL("http://localhost:8080/api/products");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);
            conn.getOutputStream().write(jsonInputString.getBytes("utf-8"));

            if (conn.getResponseCode() == 201) {
                populateProductTable();
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        populateProductTable();

        productNameField.clear();
        quantityField.clear();
        unitField.setValue(null);
        isIntegerCheckBox.setSelected(false);
    }

    private boolean AreInputFieldsFilled() {
        return productNameField.getText().isEmpty() || quantityField.getText().isEmpty() || unitField.getValue() == null;
    }

    private boolean checkIfInputFieldsAreFilled() {
        if (AreInputFieldsFilled()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setContentText("Please fill all fields");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    private void populateProductTable() {
        productTable.getItems().clear();

        try {
            URL url = new URL("http://localhost:8080/api/products");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                String response = scanner.nextLine();

                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Product product = new Product(
                            jsonObject.getInt("id"),
                            jsonObject.getString("name"),
                            jsonObject.getDouble("quantity"),
                            jsonObject.getString("unit"),
                            jsonObject.getBoolean("isInteger")
                    );
                    productTable.getItems().add(product);
                }
            }

            scanner.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
