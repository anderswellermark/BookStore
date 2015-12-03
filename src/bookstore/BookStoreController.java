/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore;

import bookstore.model.Book;
import bookstore.model.BookList;
import bookstore.model.OrderRow;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 *
 * @author Anders
 */
public class BookStoreController implements Initializable, BookList {

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableView<OrderRow> cartTable;
    @FXML
    private ObservableList<Book> booksData;
    @FXML
    private final ObservableList<Book> booksSearchData = FXCollections.observableArrayList();
    @FXML
    private ObservableList<OrderRow> cartData;
    @FXML
    private TableColumn titleColumn;
    @FXML
    private TableColumn authorColumn;
    @FXML
    private TableColumn priceColumn;
    @FXML
    private TableColumn stockBalanceColumn;
    @FXML
    private TableColumn pcsColumn;
    @FXML
    private TableColumn carttitleColumn;
    @FXML
    private TableColumn rowsumColumn;
    @FXML
    private TableColumn removeFromCartButtonColumn;
    @FXML
    private TableColumn buyButtonColumn;
    @FXML
    private Label lblCartSum;
    @FXML
    private Button btnCheckout;
    @FXML
    private TextField txtSearchInventory;
    @FXML
    private Label lblStatusBar;
    @FXML
    private MenuItem mnuItemImport;
    @FXML
    private MenuItem mnuItemQuit;
    
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void handleImportAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage.getOwner());
        if (file != null) {
            int importCount = 0;
            Book[] books = BookUnmarshaller.BooksFromCsv(file);
            if(books != null) {
                booksData.addAll(books);
                importCount = books.length;
            }
            lblStatusBar.setText("Retreived " + String.valueOf(importCount) + " items(s) from " + file.getPath() + ".");
            //If search filtering is applied, trigger an update to get the updated stock balances
            if (!txtSearchInventory.getText().trim().equals("")) {
                txtSearchInventory.getOnKeyReleased().handle(null);
            }
        }
    }

    @FXML
    private void handleQuitAction(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleCheckoutAction(ActionEvent event) {
        ArrayList<Pair<Book, BuyStatus>> buyResults = new ArrayList<>();
        for (OrderRow orderRow : cartData) {
            for (int i = 0; i < orderRow.getPcs(); i++) {
                int[] buyStatuses = buy(orderRow.getBook());
                BuyStatus buyStatus = BuyStatus.values()[buyStatuses[0]];
                buyResults.add(new Pair(orderRow.getBook(), buyStatus));
            }
        }
        showCheckoutConfirmation(buyResults);
    }

    private void showCheckoutConfirmation(ArrayList<Pair<Book, BuyStatus>> buyResults) {
        HashMap<Book, Integer> missingBookQtys = new HashMap<>();
        for (Pair<Book, BuyStatus> buyResult : buyResults) {
            if (buyResult.getValue() == BuyStatus.NOT_IN_STOCK || buyResult.getValue() == BuyStatus.DOES_NOT_EXIST) {
                if (missingBookQtys.get(buyResult.getKey()) != null) {
                    missingBookQtys.put(buyResult.getKey(), missingBookQtys.get(buyResult.getKey()) + 1);
                } else {
                    missingBookQtys.put(buyResult.getKey(), 1);
                }
            }
        }
        String dialogText = "";
        if (missingBookQtys.size() > 0) {
            dialogText = Constants.ITEMS_MISSING_ANNOUNCEMENT;
        }
        for (Entry<Book, Integer> missingBook : missingBookQtys.entrySet()) {
            dialogText += missingBook.getKey().getTitle() + ": " + String.valueOf(missingBook.getValue()) + "pcs\n";
        }
        dialogText += Constants.CHECKOUT_INFO_ENDTEXT;
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(Constants.CHECKOUT_INFO_HEADLINE);
        alert.setHeaderText(Constants.CHECKOUT_INFO_HEADLINE);
        alert.setContentText(dialogText);
        alert.setOnCloseRequest((DialogEvent event) -> {
            clearCart();
        });
        alert.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblCartSum.setText("0 " + Constants.CURRENCY_SYMBOL);
        btnCheckout.setDisable(true);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockBalanceColumn.setCellValueFactory(new PropertyValueFactory<>("stockBalance"));
        pcsColumn.setCellValueFactory(new PropertyValueFactory<>("pcs"));
        carttitleColumn.setCellValueFactory(new Callback<CellDataFeatures<OrderRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<OrderRow, String> data) {
                return data.getValue().getBook().title;
            }
        });
        rowsumColumn.setCellValueFactory(new Callback<CellDataFeatures<OrderRow, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<OrderRow, String> data) {
                double rowsum = data.getValue().getBook().getPriceDouble() * data.getValue().getPcs();
                DecimalFormat decim = Constants.DECIMAL_FORMAT;
                String rowsumStr = decim.format(rowsum) + Constants.CURRENCY_SYMBOL;
                return new ReadOnlyObjectWrapper<>(rowsumStr);
            }
        });
        removeFromCartButtonColumn = new TableColumn();
        removeFromCartButtonColumn.setCellValueFactory(new PropertyValueFactory<>(""));
        Callback<TableColumn<OrderRow, String>, TableCell<OrderRow, String>> removeFromCartBtnColumnCellFactory
                = new Callback<TableColumn<OrderRow, String>, TableCell<OrderRow, String>>() {
                    @Override
                    public TableCell call(final TableColumn<OrderRow, String> param) {
                        final TableCell<OrderRow, String> cell = new TableCell<OrderRow, String>() {
                            final Button btn = new Button(Constants.REMOVE_BTN_TEXT);

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction((ActionEvent event) -> {
                                        getTableView().getItems().remove(getIndex());
                                        refreshCart();
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        removeFromCartButtonColumn.setCellFactory(removeFromCartBtnColumnCellFactory);
        cartTable.getColumns().add(removeFromCartButtonColumn);

        buyButtonColumn = new TableColumn();
        buyButtonColumn.setCellValueFactory(new PropertyValueFactory<>(""));
        Callback<TableColumn<Book, String>, TableCell<Book, String>> buyBtnCellFactory
                = new Callback<TableColumn<Book, String>, TableCell<Book, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Book, String> param) {
                        final TableCell<Book, String> cell = new TableCell<Book, String>() {
                            final Button btn = new Button(Constants.ADD_TO_CART_BTN_TEXT);

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction((ActionEvent event) -> {
                                        Book book = getTableView().getItems().get(getIndex());
                                        showAddToCartDialog(event, book);
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        buyButtonColumn.setCellFactory(buyBtnCellFactory);
        bookTable.getColumns().add(0, buyButtonColumn);
        booksData = FXCollections.observableArrayList();
        booksData.addAll(BookUnmarshaller.BooksFromCsv(Constants.BOOKS_URL));
        lblStatusBar.setText("Retreived " + String.valueOf(booksData.size()) + " items(s) from " + Constants.BOOKS_URL + ".");
        bookTable.setItems(booksData);
        cartData = FXCollections.observableArrayList();
        cartTable.setItems(cartData);
        cartTable.setPlaceholder(new Label(Constants.EMPTY_CART_TEXT));
        bookTable.setPlaceholder(new Label(Constants.EMPTY_INVENTORY_TEXT));
        initializeSearch();
    }

    private void initializeSearch() {
        txtSearchInventory.setOnKeyReleased((KeyEvent event) -> {
            if (!txtSearchInventory.getText().trim().equals("")) {
                booksSearchData.clear();
                booksSearchData.addAll(list(txtSearchInventory.getText()));
                bookTable.setItems(booksSearchData);
            } else {
                bookTable.setItems(booksData);
                lblStatusBar.setText("Viewing all " + String.valueOf(booksData.size()) + " items.");
            }
            bookTable.refresh();
        });
    }

    @Override
    public boolean add(Book book, int amount) {
        OrderRow orderRow = new OrderRow();
        orderRow.setBook(book);
        orderRow.setPcs(amount);
        cartData.add(orderRow);
        refreshCart();
        return getCartPcs(book) <= book.getStockBalance();
    }

    public int getCartPcs(Book book) {
        int pcs = 0;
        for (OrderRow row : cartData) {
            if (book.equals(row.getBook())) {
                pcs += row.getPcs();
            }
        }
        return pcs;
    }

    public void clearCart() {
        cartData.clear();
        refreshCart();
    }

    @Override
    public Book[] list(String searchString) {
        String[] searchStringParts = null;
        String searchStatusBarText = Constants.SEARCH_STATUS_BAR_TEXT;
        if (searchString.length() > 1 && searchString.startsWith("\"") && searchString.endsWith("\"")) { //Match exact phrase
            searchStringParts = new String[]{searchString.substring(1, searchString.length() - 1)};
        } else {
            searchStringParts = searchString.split(" ");
        }
        int[] matchPerWord = new int[searchStringParts.length];
        ArrayList<Book> result = new ArrayList<>();
        int searchStrCount = 0;
        for (String searchStrPart : searchStringParts) {
            for (Book book : booksData) {
                if (book.getTitle().toLowerCase().contains(searchStrPart.toLowerCase())
                        || book.getAuthor().toLowerCase().contains(searchStrPart.toLowerCase())) {
                    if (!result.contains(book)) {
                        result.add(book);
                    }
                    matchPerWord[searchStrCount]++;
                }
            }
            searchStatusBarText += " <\"" + searchStrPart + "\": " + matchPerWord[searchStrCount] + "> ";
            searchStrCount++;
        }
        lblStatusBar.setText(searchStatusBarText);
        return result.toArray(new Book[result.size()]);
    }

    public void refreshCart() {
        DecimalFormat decim = Constants.DECIMAL_FORMAT;
        double cartsum = 0;
        cartsum = cartData.stream().map((orderRow)
                -> orderRow.getBook().getPriceDouble() * orderRow.getPcs())
                .reduce(cartsum, (accumulator, _item) -> accumulator + _item);
        String cartsumStr = decim.format(cartsum) + Constants.CURRENCY_SYMBOL;
        lblCartSum.setText(cartsumStr);
        if (cartData.size() > 0) {
            btnCheckout.setDisable(false);
        } else {
            btnCheckout.setDisable(true);
        }
    }

    public void showAddToCartDialog(ActionEvent event, Book book) {
        TextInputDialog tid = new TextInputDialog("1");
        tid.initOwner(((Node) event.getTarget()).getScene().getWindow());
        tid.setTitle(Constants.CART_DIALOG_HEADER);
        tid.setHeaderText(Constants.CART_DIALOG_HEADER);
        tid.setContentText(Constants.CART_DIALOG_TEXT);
        tid.show();
        tid.setOnCloseRequest((DialogEvent event1) -> {
            if (tid.getResult() != null) {
                try {
                    int amount = Integer.parseInt(tid.getResult());
                    if (amount > 0) {
                        if (!add(book, amount)) {
                            Alert alert = new Alert(AlertType.WARNING);
                            alert.setTitle(Constants.EMPTY_STOCK_DIALOG_HEADER);
                            alert.setHeaderText(Constants.EMPTY_STOCK_DIALOG_HEADER);
                            alert.setContentText(Constants.EMPTY_STOCK_DIALOG_TEXT);
                            alert.show();
                        }
                    }
                } catch (NumberFormatException e) {
                }
            }
        });
    }

    @Override
    public int[] buy(Book... books) {
        int[] buyStatuses = new int[books.length];
        for (int i = 0; i < books.length; i++) {
            if (booksData.contains(books[i]) == false) {
                buyStatuses[i] = BuyStatus.DOES_NOT_EXIST.ordinal();
            } else if (books[i].getStockBalance() <= 0) {
                buyStatuses[i] = BuyStatus.NOT_IN_STOCK.ordinal();
            } else {
                books[i].stockBalance.set(books[i].getStockBalance() - 1);
                booksData.set(booksData.indexOf(books[i]), books[i]);
                //If search filtering is applied, trigger an update to get the updated stock balances
                if (!txtSearchInventory.getText().trim().equals("")) {
                    txtSearchInventory.getOnKeyReleased().handle(null);
                }
                txtSearchInventory.getOnKeyReleased().handle(null);
                buyStatuses[i] = BuyStatus.OK.ordinal();
            }
        }
        return buyStatuses;
    }

    public enum BuyStatus {

        OK, NOT_IN_STOCK, DOES_NOT_EXIST
    }
}
