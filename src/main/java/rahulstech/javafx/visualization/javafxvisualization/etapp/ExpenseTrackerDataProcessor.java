package rahulstech.javafx.visualization.javafxvisualization.etapp;

import rahulstech.javafx.visualization.javafxvisualization.etapp.model.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;

@SuppressWarnings("unused")
public class ExpenseTrackerDataProcessor {

    private static final String TAG = ExpenseTrackerDataProcessor.class.getSimpleName();

    private final File backupFile;

    private final int version;

    private final List<Account> accounts;

    private final HashMap<Long,Account> mapAccountIdAccount = new HashMap<>();

    private final List<Person> people;

    private final HashMap<Long,Person> mapPersonIdPerson = new HashMap<>();

    private final List<Transaction> transactions;

    private LocalDate minDate;

    private LocalDate maxDate;


    public ExpenseTrackerDataProcessor(File backupFile, ExpenseTrackerJsonBackup data) {
        if (null == backupFile) throw new NullPointerException("null == backupFile");
        if (null == data) throw new NullPointerException("backup data == null");
        this.backupFile = backupFile;
        this.version = data.getVersion();
        this.accounts = Collections.unmodifiableList(data.getAccounts());
        for (Account a : this.accounts) {
            mapAccountIdAccount.put(a.getAccountId(),a);
        }
        this.people = Collections.unmodifiableList(data.getPeople());
        for (Person p : this.people) {
            mapPersonIdPerson.put(p.getPersonId(),p);
        }
        if (hasTransactions(data.getTransactions())) {
            data.getTransactions().sort(SORT_TRANSACTION_BY_DATE_ASC);
            minDate = data.getTransactions().get(0).getDate();
            maxDate = data.getTransactions().get(data.getTransactions().size()-1).getDate();
            this.transactions = Collections.unmodifiableList(data.getTransactions());
        }
        else {
            this.transactions = Collections.emptyList();
        }
    }

    public File getBackupFile() {
        return backupFile;
    }

    public int getVersion() {
        return version;
    }

    public boolean hasTransactions(List<Transaction> transactions) {
        return null != transactions && !transactions.isEmpty();
    }

    public LocalDate getMinDate() {
        return minDate;
    }

    public LocalDate getMaxDate() {
        return maxDate;
    }

    public List<Account> getAllAccounts() {
        return null == accounts ? Collections.emptyList() : accounts;
    }

    public Map<Long, Account> getMapAccountIdAccount() {
        return mapAccountIdAccount;
    }

    public List<Person> getAllPeople() {
        return null == people ? Collections.emptyList() : people;
    }

    public Map<Long, Person> getMapPersonIdPerson() {
        return mapPersonIdPerson;
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    public List<Transaction> getTransactionsOfPerson(List<Transaction> transactions, Long personId) {
        if (!hasTransactions(transactions)) return Collections.emptyList();
        if (Objects.equals(personId, Arguments.DEFAULT_ENTITY_ID)) return transactions;
        ArrayList<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactions) {
            if (Objects.equals(t.getPersonId(), personId)) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    public List<Transaction> getTransactionsOfAccount(List<Transaction> transactions, Long accountId) {
        if (!hasTransactions(transactions)) return Collections.emptyList();
        if (Objects.equals(accountId, Arguments.DEFAULT_ENTITY_ID)) return transactions;
        ArrayList<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactions) {
            if (Objects.equals(t.getAccountId(), accountId)) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    public List<Transaction> getTransactionsBetweenDates(List<Transaction> transactions, LocalDate start, LocalDate end) {
        if (null == start) start = minDate;
        if (null == end) end = maxDate;
        if (!hasTransactions(transactions)) return Collections.emptyList();
        // when start and end date are global min and max dates,
        // then no needed to do filter as all transactions are included
        if (start == minDate && end == maxDate) return transactions;
        ArrayList<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactions) {
            if (isDateBetween(t.getDate(),start,end)) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    public List<Transaction> getTransactionsOfType(List<Transaction> transactions, TransactionType type) {
        if (!hasTransactions(transactions) || null == type) return Collections.emptyList();
        // type is ALL then no extract filtering needed
        if (TransactionType.ALL == type) return transactions;
        ArrayList<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactions) {
            if (!isTransactionTypeAccepted(type,t.getType())) continue;
            filtered.add(t);
        }
        return filtered;
    }

    public List<Transaction> getTransactionGroupedByMonths(List<Transaction> transactions) {
        if (!hasTransactions(transactions)) return Collections.emptyList();
        ArrayList<Transaction> grouped = new ArrayList<>();
        YearMonth cursor = null;
        Transaction last = null;
        for (Transaction t : transactions) {
            YearMonth ym = YearMonth.from(t.getDate());
            TransactionType type = t.getType();
            if (!ym.equals(cursor)) {
                cursor = ym;
                last = new Transaction();
                last.setAmount(BigDecimal.ZERO);
                grouped.add(last);
            }
            // last date of transaction of the month will be stored in the grouped value
            // this will help the get the last date of transaction of the month
            last.setDate(t.getDate());
            last.setAmount(type == TransactionType.EXPENSE
                    ? last.getAmount().subtract(t.getAmount())
                    : last.getAmount().add(t.getAmount()));
        }
        return grouped;
    }

    public List<Transaction> getTransactionGroupedByYear(List<Transaction> transactions) {
        if (!hasTransactions(transactions)) return Collections.emptyList();
        ArrayList<Transaction> grouped = new ArrayList<>();
        Year cursor = null;
        Transaction last = null;
        for (Transaction t : transactions) {
            Year y = Year.from(t.getDate());
            TransactionType type = t.getType();
            if (!y.equals(cursor)) {
                cursor = y;
                last = new Transaction();
                last.setAmount(BigDecimal.ZERO);
                grouped.add(last);
            }
            // last date of transaction of the month will be stored in the grouped value
            // this will help the get the last date of transaction of the year
            last.setDate(t.getDate());
            last.setAmount(type == TransactionType.EXPENSE
                    ? last.getAmount().subtract(t.getAmount())
                    : last.getAmount().add(t.getAmount()));
        }
        return grouped;
    }

    public List<Transaction> getTransactionGroupedByAccount(List<Transaction> transactions) {
        if (!hasTransactions(transactions)) return Collections.emptyList();
        HashMap<Long,Transaction> map = new HashMap<>();
        for (Transaction t : transactions) {
            TransactionType type = t.getType();
            Long accountId = t.getAccountId();
            Transaction last = map.get(accountId);
            if (null == last) {
                last = new Transaction();
                last.setAccountId(accountId);
                last.setAmount(BigDecimal.ZERO);
                map.put(accountId, last);
            }
            last.setDate(t.getDate());
            // transaction amount is an absolute value
            // depending on the transaction type we
            // subtract the expense amount and add income
            // amount while calculating sum.
            // sum amount may be +ve or -ve
            last.setAmount(type == TransactionType.EXPENSE
                    ? last.getAmount().subtract(t.getAmount())
                    : last.getAmount().add(t.getAmount()));
        }
        return new ArrayList<>(map.values());
    }

    public List<Transaction> getTransactionGroupedByPerson(List<Transaction> transactions) {
        if (!hasTransactions(transactions)) return Collections.emptyList();
        HashMap<Long,Transaction> map = new HashMap<>();
        for (Transaction t : transactions) {
            TransactionType type = t.getType();
            Long personId = t.getPersonId();
            Transaction last = map.get(personId);
            if (null == last) {
                last = new Transaction();
                last.setPersonId(personId);
                last.setAmount(BigDecimal.ZERO);
                map.put(personId, last);
            }
            last.setDate(t.getDate());
            // transaction amount is an absolute value
            // depending on the transaction type we
            // subtract the expense amount and add income
            // amount while calculating sum.
            // sum amount may be +ve or -ve
            last.setAmount(type == TransactionType.EXPENSE
                    ? last.getAmount().subtract(t.getAmount())
                    : last.getAmount().add(t.getAmount()));
        }
        return new ArrayList<>(map.values());
    }

    public Transaction calculateAverage(List<Transaction> transactions) {
        if (!hasTransactions(transactions)) return null;
        Transaction avg = new Transaction();
        avg.setAmount(BigDecimal.ZERO);
        for (Transaction t : transactions) {
            avg.setPersonId(t.getPersonId());
            avg.setAccountId(t.getAccountId());
            avg.setDate(t.getDate());
            avg.setAmount(avg.getAmount().add(t.getAmount()));
        }
        avg.setAmount(avg.getAmount().divide(BigDecimal.valueOf(transactions.size()), RoundingMode.FLOOR));
        return avg;
    }

    private boolean isDateBetween(LocalDate test, LocalDate start, LocalDate end) {
        return (test.isEqual(start) || test.isAfter(start))
                && (test.isEqual(end) || test.isBefore(end));
    }

    private boolean isTransactionTypeAccepted(TransactionType target, TransactionType actual) {
        return actual == target;
    }

    private Comparator<Transaction> SORT_TRANSACTION_BY_DATE_ASC = (left, right) -> {
        LocalDate dateLeft = left.getDate();
        LocalDate dateRight = right.getDate();
        if (dateLeft.isBefore(dateRight)) return -1;
        if (dateLeft.isAfter(dateRight)) return 1;
        return 0;
    };

    public enum GroupBy {
        MONTH,
        YEAR,
        PERSON,
        ACCOUNT
    }

    public static class Arguments {
        public static final Long DEFAULT_ENTITY_ID = -1L;

        public Long personId = DEFAULT_ENTITY_ID;

        public Long accountId = DEFAULT_ENTITY_ID;

        public TransactionType type = TransactionType.ALL;
        public GroupBy groupBy = GroupBy.MONTH;

        public LocalDate dateStart = null;

        public LocalDate dateEnd = null;

        public void setType(TransactionType type) {
            this.type = type;
        }

        public void setPerson(Long personId) {
            this.personId = personId;
        }

        public void setAccount(Long accountId) {
            this.accountId = accountId;
        }

        public void setGroupBy(GroupBy groupBy) {
            this.groupBy = groupBy;
        }

        public void setDateStart(LocalDate dateStart) {
            this.dateStart = dateStart;
        }

        public void setDateEnd(LocalDate dateEnd) {
            this.dateEnd = dateEnd;
        }

        @Override
        public String toString() {
            return "Arguments{" +
                    "personId=" + personId +
                    ", accountId=" + accountId +
                    ", type=" + type +
                    ", groupBy=" + groupBy +
                    ", dateStart=" + dateStart +
                    ", dateEnd=" + dateEnd +
                    '}';
        }
    }
}
