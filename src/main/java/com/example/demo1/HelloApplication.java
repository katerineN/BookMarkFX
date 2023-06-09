package com.example.demo1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class HelloApplication extends Application {

    private ObservableList<BookMark> bookmarks;
    private ObservableList<BookMark> allBookmarks;
    private TableView<BookMark> tableView;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        bookmarks = FXCollections.observableArrayList();
        allBookmarks = FXCollections.observableArrayList();

        tableView = new TableView<>();
        tableView.setItems(bookmarks);

        TableColumn<BookMark, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(data -> data.getValue().urlProperty());

        TableColumn<BookMark, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<BookMark, String> call(TableColumn<BookMark, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            setWrapText(true);
                            setTextAlignment(TextAlignment.LEFT);
                            setPrefHeight(Control.USE_COMPUTED_SIZE);
                        }
                    }
                };
            }
        });
        descriptionColumn.setPrefWidth(200);
        descriptionColumn.setStyle("-fx-cell-size: 40px;");

        TableColumn<BookMark, String> categoryColumn = new TableColumn<>("Категория");
        categoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());

        tableView.getColumns().addAll(urlColumn, descriptionColumn, categoryColumn);

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> showAddDialog());

        Button filterButton = new Button("Фильтр");
        filterButton.setOnAction(event -> showFilterDialog());

        HBox buttonBox = new HBox(10, addButton, filterButton);
        buttonBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(createHeader());
        root.setCenter(tableView);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setTitle("Записная книжка");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createHeader() {
        VBox headerBox = new VBox();
        headerBox.getStyleClass().add("header");

        Label titleLabel = new Label("Записная книжка");
        titleLabel.getStyleClass().add("title");

        headerBox.getChildren().add(titleLabel);
        return headerBox;
    }

    private void showAddDialog() {
        Dialog<BookMark> dialog = new Dialog<>();
        dialog.setTitle("Добавить ресурс");

        ButtonType addButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField urlField = new TextField();
        TextArea descriptionArea = new TextArea();
        TextField categoryField = new TextField();

        grid.add(new Label("URL:"), 0, 0);
        grid.add(urlField, 1, 0);
        grid.add(new Label("Описание:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        grid.add(new Label("Категория:"), 0, 2);
        grid.add(categoryField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                String url = urlField.getText();
                String description = descriptionArea.getText();
                String category = categoryField.getText();

                BookMark bookmark = new BookMark(url, description, category);
                bookmarks.add(bookmark);
                allBookmarks = bookmarks;
                saveToFile(bookmark); // Сохранение в файл
                return bookmark;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showFilterDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Фильтр по категории");

        ButtonType filterButton = new ButtonType("Применить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(filterButton, ButtonType.CANCEL);

        ButtonType clearFilterButton = new ButtonType("Снять фильтр", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().add(clearFilterButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll(getUniqueCategories());
        categoryComboBox.getSelectionModel().selectFirst();

        grid.add(new Label("Категория:"), 0, 0);
        grid.add(categoryComboBox, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == filterButton) {
                String selectedCategory = categoryComboBox.getValue();
                filterByCategory(selectedCategory);
            } else if (dialogButton == clearFilterButton) {
                clearFilter();
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void clearFilter() {
        tableView.setItems(allBookmarks);
    }

    private void saveToFile(BookMark bookmark) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bookmarks.txt", false))) {
            writer.write(bookmark.toFileString());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String> getUniqueCategories() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        for (BookMark bookmark : allBookmarks) {
            if (!categories.contains(bookmark.getCategory())) {
                categories.add(bookmark.getCategory());
            }
        }
        return categories;
    }

    private void filterByCategory(String category) {
        ObservableList<BookMark> categoryBookmarks = FXCollections.observableArrayList();
        for (BookMark bookmark : allBookmarks) {
            if (bookmark.getCategory().equals(category)) {
                categoryBookmarks.add(bookmark);
            }
        }
        tableView.setItems(categoryBookmarks);
    }
}