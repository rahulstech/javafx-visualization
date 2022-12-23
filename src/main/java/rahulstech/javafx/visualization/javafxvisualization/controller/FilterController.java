package rahulstech.javafx.visualization.javafxvisualization.controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import rahulstech.javafx.visualization.javafxvisualization.etapp.ExpenseTrackerDataProcessor;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.Account;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.Person;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.TransactionType;
import rahulstech.javafx.visualization.javafxvisualization.util.DialogUtil;

import java.net.URL;
import java.util.*;

public class FilterController extends BaseController {

    public static final Person ALL_PEOPLE = new Person();

    public static final Account ALL_ACCOUNTS = new Account();

    static {
        ALL_ACCOUNTS.setAccountId(ExpenseTrackerDataProcessor.Arguments.DEFAULT_ENTITY_ID);
        ALL_ACCOUNTS.setAccountName("All");
        ALL_PEOPLE.setPersonId(ExpenseTrackerDataProcessor.Arguments.DEFAULT_ENTITY_ID);
        ALL_PEOPLE.setPersonName("All");
    }

    public CheckBox choiceExpense;
    public CheckBox choiceIncome;
    public DatePicker dateStartPicker;
    public DatePicker dateEndPicker;

    private ListCell<ExpenseTrackerDataProcessor.GroupBy> createNewGroupByListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(ExpenseTrackerDataProcessor.GroupBy item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(item.toString());
                }
            }
        };
    }

    private ListCell<Account> createNewFilterAccountListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Account item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(item.getAccountName());
                }
            }
        };
    }

    private ListCell<Person> createNewFilterPersonListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Person item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || null == item) {
                    setText(null);
                    setGraphic(null);
                }
                else {
                    setText(item.getPersonName());
                }
            }
        };
    }

    public ComboBox<Account> filterAccount;
    public ComboBox<Person> filterPerson;
    public ComboBox<ExpenseTrackerDataProcessor.GroupBy> groupBy;

    private ExpenseTrackerDataProcessor dataProcessor = null;

    private final ObjectProperty<ExpenseTrackerDataProcessor.Arguments> argumentProperty = new SimpleObjectProperty<>(null);

    public ObjectProperty<ExpenseTrackerDataProcessor.Arguments> argumentProperty() {
        return argumentProperty;
    }

    public void setArgument(ExpenseTrackerDataProcessor.Arguments args) {
        argumentProperty.setValue(args);
    }

    public Optional<ExpenseTrackerDataProcessor.Arguments> getArguments() {
        ExpenseTrackerDataProcessor.Arguments args = argumentProperty.getValue();
        return null == args ? Optional.empty() : Optional.of(args);
    }

    @Override
    protected void onInitializeView(URL location, ResourceBundle resources) {
        super.onInitializeView(location, resources);
        groupBy.setCellFactory((cb)->createNewGroupByListCell());
        groupBy.setButtonCell(createNewGroupByListCell());
        filterAccount.setCellFactory((cb)->createNewFilterAccountListCell());
        filterAccount.setButtonCell(createNewFilterAccountListCell());
        filterPerson.setCellFactory((cb)->createNewFilterPersonListCell());
        filterPerson.setButtonCell(createNewFilterPersonListCell());
    }

    public void setDataProcessor(ExpenseTrackerDataProcessor dataProcessor) {
        if (null == dataProcessor) throw new NullPointerException("null == data processor");
        this.dataProcessor = dataProcessor;
    }

    public void onClickFilter() {
        ExpenseTrackerDataProcessor.Arguments arg = new ExpenseTrackerDataProcessor.Arguments();
        Account account = filterAccount.getValue();
        Person person = filterPerson.getValue();
        if (account != ALL_ACCOUNTS) {
            arg.setAccount(account.getAccountId());
        }
        if (person != ALL_PEOPLE) {
            arg.setPerson(person.getPersonId());
        }
        arg.setGroupBy(groupBy.getValue());
        arg.setDateStart(dateStartPicker.getValue());
        arg.setDateEnd(dateEndPicker.getValue());
        if (choiceIncome.isSelected() && !choiceExpense.isSelected()){
            arg.setType(TransactionType.INCOME);
        }
        else if (choiceExpense.isSelected() && !choiceIncome.isSelected()) {
            arg.setType(TransactionType.EXPENSE);
        }
        else {
            arg.setType(TransactionType.ALL);
        }
        argumentProperty.setValue(arg);
        dismiss();
    }

    public void onClickCancel() {
        dismiss();
    }

    public void showAsDialog() {
        DialogUtil.showAsDialog(getWindow(),getView());
        init();
    }

    public void dismiss() {
        DialogUtil.dismissDialog(getWindow(),getView());
    }

    private void init() {
        List<Person> people = new ArrayList<>(dataProcessor.getAllPeople());
        List<Account> accounts = new ArrayList<>(dataProcessor.getAllAccounts());
        people.add(0,ALL_PEOPLE);
        people.add(1,Person.ME);
        accounts.add(0,ALL_ACCOUNTS);
        groupBy.setItems(FXCollections.observableArrayList(ExpenseTrackerDataProcessor.GroupBy.values()));
        filterPerson.setItems(FXCollections.observableArrayList(people));
        filterAccount.setItems(FXCollections.observableArrayList(accounts));

        Optional<ExpenseTrackerDataProcessor.Arguments> oArguments = getArguments();
        if (oArguments.isEmpty()) {
            groupBy.getSelectionModel().select(ExpenseTrackerDataProcessor.GroupBy.MONTH);
            filterPerson.getSelectionModel().select(ALL_PEOPLE);
            filterAccount.getSelectionModel().select(ALL_ACCOUNTS);
            choiceIncome.setSelected(true);
            choiceExpense.setSelected(true);
        }
        else {
            ExpenseTrackerDataProcessor.Arguments args = oArguments.get();
            groupBy.getSelectionModel().select(args.groupBy);
            filterPerson.getSelectionModel().select(getPersonToSelect(filterPerson.getItems(), args.personId));
            filterAccount.getSelectionModel().select(getAccountToSelect(filterAccount.getItems(),args.accountId));
            dateStartPicker.setValue(args.dateStart);
            dateEndPicker.setValue(args.dateEnd);
            if (args.type == TransactionType.EXPENSE) {
                choiceExpense.setSelected(true);
            }
            else if (args.type == TransactionType.INCOME) {
                choiceIncome.setSelected(true);
            }
            else {
                choiceExpense.setSelected(true);
                choiceIncome.setSelected(true);
            }
        }
    }

    private Person getPersonToSelect(List<Person> people, Long personId) {
        if (ExpenseTrackerDataProcessor.Arguments.DEFAULT_ENTITY_ID.equals(personId))
            return ALL_PEOPLE;
        if (Objects.equals(Person.ME.getPersonId(), personId))
            return Person.ME;
        for (Person p : people) {
            if (Objects.equals(p.getPersonId(), personId))
                return p;
        }
        return ALL_PEOPLE;
    }

    private Account getAccountToSelect(List<Account> accounts, Long accountId) {
        if (ExpenseTrackerDataProcessor.Arguments.DEFAULT_ENTITY_ID.equals(accountId))
            return ALL_ACCOUNTS;
        for (Account a : accounts) {
            if (Objects.equals(a.getAccountId(),accountId))
                return a;
        }
        return ALL_ACCOUNTS;
    }
}
