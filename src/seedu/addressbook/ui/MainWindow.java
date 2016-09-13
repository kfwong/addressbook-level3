package seedu.addressbook.ui;


import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import seedu.addressbook.commands.ExitCommand;
import seedu.addressbook.logic.Logic;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.data.person.ReadOnlyPerson;

import java.util.List;
import java.util.Optional;

import static seedu.addressbook.common.Messages.*;

/**
 * Main Window of the GUI.
 */
public class MainWindow {
    
    @FXML
    private TableView<ReadOnlyPerson> personTable;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> nameTableColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> phoneTableColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> emailTableColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> addressTableColumn;
    
    @FXML
    private TableColumn<ReadOnlyPerson, String> tagsTableColumn;
    
    @FXML
    private TabPane tabs;
    
    @FXML
    private Tab consoleTab;
    
    @FXML
    private Tab tableTab;

    private Logic logic;
    private Stoppable mainApp;

    public MainWindow(){
    }

    public void setLogic(Logic logic){
        this.logic = logic;
    }

    public void setMainApp(Stoppable mainApp){
        this.mainApp = mainApp;
    }

    @FXML
    private TextArea outputConsole;

    @FXML
    private TextField commandInput;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize(){
        // Binding Person model to the column entries
        nameTableColumn.setCellValueFactory(person-> new ReadOnlyStringWrapper(person.getValue().getName().fullName));
        phoneTableColumn.setCellValueFactory(person->new ReadOnlyStringWrapper(person.getValue().getPhone().value));
        emailTableColumn.setCellValueFactory( person -> new ReadOnlyStringWrapper(person.getValue().getEmail().value));
        addressTableColumn.setCellValueFactory( person -> new ReadOnlyStringWrapper(person.getValue().getAddress().value));
        tagsTableColumn.setCellValueFactory( person -> new ReadOnlyStringWrapper(person.getValue().getTags().toString()));
    }
    

    @FXML
    void onCommand(ActionEvent event) {
        try {
            String userCommandText = commandInput.getText();
            CommandResult result = logic.execute(userCommandText);
            if(isExitCommand(result)){
                exitApp();
                return;
            }
            
            if(hasRelevantPersonsList(result)){
                
                // need to convert list to observable list in fx
                // http://stackoverflow.com/questions/36629522/convert-arraylist-to-observable-list-for-javafx-program
                ObservableList<ReadOnlyPerson> readOnlyPersons = FXCollections.observableArrayList(result.getRelevantPersons().get());
                personTable.setItems(readOnlyPersons);
                
                // swtich to table view
                // http://stackoverflow.com/questions/20955633/how-to-switch-through-tabs-programmatically-in-javafx
                tabs.getSelectionModel().select(tableTab);
            }else{
                tabs.getSelectionModel().select(consoleTab);
            }
            
            displayResult(result);
            clearCommandInput();
        } catch (Exception e) {
            display(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void exitApp() throws Exception {
        mainApp.stop();
    }

    /** Returns true of the result given is the result of an exit command */
    private boolean isExitCommand(CommandResult result) {
        return result.feedbackToUser.equals(ExitCommand.MESSAGE_EXIT_ACKNOWEDGEMENT);
    }
    
    /** Returns true of the result given is the result of an list command */
    private boolean hasRelevantPersonsList(CommandResult result) {
        return result.getRelevantPersons().isPresent();
    }

    /** Clears the command input box */
    private void clearCommandInput() {
        commandInput.setText("");
    }

    /** Clears the output display area */
    public void clearOutputConsole(){
        outputConsole.clear();
    }

    /** Displays the result of a command execution to the user. */
    public void displayResult(CommandResult result) {
        clearOutputConsole();
        final Optional<List<? extends ReadOnlyPerson>> resultPersons = result.getRelevantPersons();
        if(resultPersons.isPresent()) {
            display(resultPersons.get());
        }
        display(result.feedbackToUser);
    }

    public void displayWelcomeMessage(String version, String storageFilePath) {
        String storageFileInfo = String.format(MESSAGE_USING_STORAGE_FILE, storageFilePath);
        display(MESSAGE_WELCOME, version, MESSAGE_PROGRAM_LAUNCH_ARGS_USAGE, storageFileInfo);
    }

    /**
     * Displays the list of persons in the output display area, formatted as an indexed list.
     * Private contact details are hidden.
     */
    private void display(List<? extends ReadOnlyPerson> persons) {
        display(new Formatter().format(persons));
    }

    /**
     * Displays the given messages on the output display area, after formatting appropriately.
     */
    private void display(String... messages) {
        outputConsole.setText(outputConsole.getText() + new Formatter().format(messages));
    }

}
