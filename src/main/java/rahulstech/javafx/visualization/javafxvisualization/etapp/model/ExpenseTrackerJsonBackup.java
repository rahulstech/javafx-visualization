package rahulstech.javafx.visualization.javafxvisualization.etapp.model;

import java.util.List;

@SuppressWarnings("unused")
public class ExpenseTrackerJsonBackup {

    private List<Account> accounts;
    private List<Person> people;
    private List<Transaction> transactions;
    private int version;

    public int getVersion() {
        return version;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Person> getPeople() {
        return people;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
