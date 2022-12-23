package rahulstech.javafx.visualization.javafxvisualization.controller;

import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import rahulstech.javafx.visualization.javafxvisualization.adapter.ListXYChartAdapter;
import rahulstech.javafx.visualization.javafxvisualization.concurrent.AppExecutors;
import rahulstech.javafx.visualization.javafxvisualization.etapp.ExpenseTrackerDataProcessor;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.Account;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.Person;
import rahulstech.javafx.visualization.javafxvisualization.etapp.model.Transaction;
import rahulstech.javafx.visualization.javafxvisualization.util.FxmlUtil;
import rahulstech.javafx.visualization.javafxvisualization.util.ResourceUtil;

import java.time.format.DateTimeFormatter;
import java.util.*;

import static rahulstech.javafx.visualization.javafxvisualization.util.ChartUtil.createSimpleCompareWithMeanValueChart;
import static rahulstech.javafx.visualization.javafxvisualization.util.LogUtil.log;
import static rahulstech.javafx.visualization.javafxvisualization.util.LogUtil.logError;

@SuppressWarnings("unused")
public class ChartController extends BaseController {

    private static final String TAG = ChartController.class.getSimpleName();

    public Label chartSubject;
    public StackPane chartPanel;
    public FlowPane optionsbar;

    private ExpenseTrackerDataProcessor dataProcessor = null;

    private Optional<ExpenseTrackerDataProcessor.Arguments> oldArgument = Optional.empty();

    private FilterTask oldFilterTask = null;

    public void setDataProcessor(ExpenseTrackerDataProcessor dataProcessor) {
        if (null == dataProcessor) throw new NullPointerException("data processor == null");
        this.dataProcessor = dataProcessor;
    }

    public void setChartSubject(String subject) {
        chartSubject.setText(subject);
    }

    public void setArgument(ExpenseTrackerDataProcessor.Arguments args) {
        if (null == args) {
            oldArgument = Optional.empty();
        }
        else {
            oldArgument = Optional.of(args);
        }
    }

    public void onClickInfo() {

        String headerText;
        String contentText;
        if (oldArgument.isEmpty()) {
            headerText = dataProcessor.getBackupFile().getPath();
            contentText = "Accounts: " + dataProcessor.getAllAccounts().size() + "\n" +
                    "People: " + dataProcessor.getAllPeople().size() + "\n" +
                    "Transactions: " + dataProcessor.getAllTransactions().size() + "\n" +
                    "Transactions Date Range: " + dataProcessor.getMinDate() + " - " + dataProcessor.getMaxDate() +
                    "\n";
        }
        else {
            ExpenseTrackerDataProcessor.Arguments args = oldArgument.get();
            headerText = "Current Filter Arguments";
            StringBuilder contentBuilder = new StringBuilder();
            if (null != args.groupBy) {
                contentBuilder.append("Group By: ").append(args.groupBy).append("\n");
            }
            if (null != args.type) {
                contentBuilder.append("Type: ").append(args.type).append("\n");
            }
            if (!ExpenseTrackerDataProcessor.Arguments.DEFAULT_ENTITY_ID.equals(args.accountId) && null != args.accountId) {
                contentBuilder.append("Account Id: ").append(args.accountId).append("\n");
            }
            if (!ExpenseTrackerDataProcessor.Arguments.DEFAULT_ENTITY_ID.equals(args.personId) && null != args.personId) {
                contentBuilder.append("Person Id: ").append(args.personId).append("\n");
            }
            if (null != args.dateStart) {
                contentBuilder.append("Date Start: ").append(args.dateStart).append("\n");
            }
            if (null != args.dateEnd) {
                contentBuilder.append("Date End: ").append(args.dateEnd).append("\n");
            }
            contentText = contentBuilder.toString();
        }
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Information");
        info.setHeaderText(headerText);
        info.setContentText(contentText);
        info.show();
    }

    public void onClickFilter() {
        FilterController controller = FxmlUtil.loadViewWithController(getWindow(),ResourceUtil.view("filter-view.fxml"));
        controller.setDataProcessor(dataProcessor);
        oldArgument.ifPresent(controller::setArgument);
        controller.showAsDialog();
        controller.argumentProperty().addListener((observable,vOld,vNew) -> filter(vNew));
    }

    private void filter(ExpenseTrackerDataProcessor.Arguments args) {
        //log(TAG,"filter() args="+args);
        setArgument(args);
        if (null != oldFilterTask) {
            oldFilterTask.cancel();
        }
        FilterParameters parameters = new FilterParameters(args);
        oldFilterTask = new FilterTask(dataProcessor,parameters);
        AppExecutors.executeOnBackground(oldFilterTask);
    }

    private void onFilterFinish(Result result) {
        ExpenseTrackerDataProcessor.Arguments args = oldArgument.get();
        List<Number> values = result.values;
        Number average = calculateAverage(values);
        List<String> labels = result.labels;
        double[] bounds = calculateChartBounds(values);
        double lowerBound = bounds[0];
        double upperBound = bounds[1];
        double tickUnit = calculateTickUnit(upperBound-lowerBound);

        //log(TAG,"onFilterFinish(): values="+values+" labels="+labels);

        ListXYChartAdapter<String,Number> valuesAdapter = new ListXYChartAdapter<>(
                new ListXYChartAdapter.SimpleEntry<>(labels,values));
        ListXYChartAdapter<String,Number> meanAdapter = new ListXYChartAdapter<>(
                new ListXYChartAdapter.SimpleEntry<>(labels,Collections.nCopies(labels.size(),average)));

        XYChart<String,Number>[] charts = createSimpleCompareWithMeanValueChart(valuesAdapter,meanAdapter,
                lowerBound,upperBound,tickUnit,getXAsisLabel(args.groupBy),"Amount");

        chartPanel.getChildren().clear();

        chartPanel.getChildren().addAll(charts);
    }

    private String getXAsisLabel(ExpenseTrackerDataProcessor.GroupBy groupBy) {
        switch (groupBy) {
            case MONTH -> {
                return "Month";
            }
            case YEAR -> {
                return "Year";
            }
            case ACCOUNT -> {
                return "Account";
            }
            case PERSON -> {
                return "Person";
            }
            default -> {
                return "";
            }
        }
    }

    private Number[] calculateMinMax(List<Number> numbers) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (Number n : numbers) {
            double v = n.doubleValue();
            min = Math.min(min,v);
            max = Math.max(max,v);
        }
        return new Number[]{min,max};
    }


    private Number calculateAverage(List<Number> numbers) {
        int count = numbers.size();
        double sum = 0;
        for (Number n : numbers) {
            sum = sum + n.doubleValue();
        }
        double avg = sum/count;
        return Math.floor(avg);
    }

    private double[] calculateChartBounds(List<Number> numbers) {
        Number[] low_high = calculateMinMax(numbers);
        Number lowestValue = low_high[0];
        Number highestValue = low_high[1];
        return calculateChartBounds(lowestValue,highestValue);
    }

    private double[] calculateChartBounds(Number lowestValue,Number highestValue) {
        double max = highestValue.doubleValue();
        double min = lowestValue.doubleValue();
        double upperBound = Math.floor(Math.abs(max)*1.5); // 1.5 times floor max
        double lowerBound = -Math.floor(Math.abs(min)*1.5); // 1.5 times floor min
        if (max < 0) upperBound = -upperBound;
        if (min > 0) lowerBound = -lowerBound;
        return new double[]{lowerBound,upperBound};
    }

    private double calculateTickUnit(Number highestValue) {
        double max = highestValue.doubleValue();
        return Math.floor(max*0.1); // 10% of floor maximum
    }

    private class FilterTask extends Task<Result> {
        private static final String TAG = FilterTask.class.getSimpleName();

        private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MMM, yy");

        private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");

        private final ExpenseTrackerDataProcessor dataProcessor;

        private final FilterParameters params;

        public FilterTask(ExpenseTrackerDataProcessor dataProcessor, FilterParameters param) {
            this.dataProcessor = dataProcessor;
            this.params = param;
        }

        @Override
        protected Result call() throws Exception {
            ExpenseTrackerDataProcessor.Arguments args = params.arguments;
            ExpenseTrackerDataProcessor.GroupBy groupBy = args.groupBy;

            List<Transaction> filtered = filterTransactions(dataProcessor,args);

            List<Number> values = extractValues(filtered,args);

            List<String> labels = null;
            if (ExpenseTrackerDataProcessor.GroupBy.MONTH == groupBy) {
                labels = extractLabelsFromTransactions(filtered,MONTH_YEAR_FORMATTER);
            }
            else if (ExpenseTrackerDataProcessor.GroupBy.YEAR == groupBy) {
                labels = extractLabelsFromTransactions(filtered,YEAR_FORMATTER);
            }
            else if (ExpenseTrackerDataProcessor.GroupBy.ACCOUNT == groupBy){
                labels = extractLabelsFromAccounts(dataProcessor,filtered);
            }
            else if (ExpenseTrackerDataProcessor.GroupBy.PERSON == groupBy) {
                labels = extractLabelsFromPeople(dataProcessor,filtered);
            }

            Result result = new Result();
            result.parameters = params;
            result.values = values;
            result.labels = labels;
            return result;
        }

        List<Transaction> filterTransactions(ExpenseTrackerDataProcessor dataProcessor, ExpenseTrackerDataProcessor.Arguments args) {
            List<Transaction> filtered;
            switch (args.groupBy) {
                case ACCOUNT -> filtered = dataProcessor.getTransactionsOfPerson(dataProcessor.getAllTransactions(),args.personId);
                case PERSON -> filtered = dataProcessor.getTransactionsOfAccount(dataProcessor.getAllTransactions(),args.accountId);
                default -> {
                    filtered = dataProcessor.getTransactionsOfAccount(dataProcessor.getAllTransactions(),args.accountId);
                    filtered = dataProcessor.getTransactionsOfPerson(filtered,args.personId);
                }
            }
            filtered = dataProcessor.getTransactionsOfType(filtered,args.type);
            filtered = dataProcessor.getTransactionsBetweenDates(filtered,args.dateStart,args.dateEnd);
            switch (args.groupBy) {
                case ACCOUNT -> filtered = dataProcessor.getTransactionGroupedByAccount(filtered);
                case PERSON -> filtered = dataProcessor.getTransactionGroupedByPerson(filtered);
                case MONTH -> filtered = dataProcessor.getTransactionGroupedByMonths(filtered);
                case YEAR -> filtered = dataProcessor.getTransactionGroupedByYear(filtered);
            }
            return filtered;
        }

        List<Number> extractValues(List<Transaction> transactions, ExpenseTrackerDataProcessor.Arguments args) {
            ArrayList<Number> values = new ArrayList<>();
            for (Transaction t : transactions) {
                values.add(t.getAmount());
            }
            return values;
        }

        List<String> extractLabelsFromTransactions(List<Transaction> transactions, DateTimeFormatter formatter) {
            ArrayList<String> labels = new ArrayList<>();
            for (Transaction t : transactions) {
                labels.add(t.getDate().format(formatter));
            }
            return labels;
        }

        List<String> extractLabelsFromAccounts(ExpenseTrackerDataProcessor processor, List<Transaction> reference) {
            Map<Long,Account> map = processor.getMapAccountIdAccount();
            ArrayList<String> labels = new ArrayList<>();
            for (Transaction t : reference) {
                Account acc = map.get(t.getAccountId());
                if (null == acc) continue;
                labels.add(acc.getAccountName());
            }
            return labels;
        }

        List<String> extractLabelsFromPeople(ExpenseTrackerDataProcessor processor, List<Transaction> reference) {
            Map<Long,Person> map = processor.getMapPersonIdPerson();
            ArrayList<String> labels = new ArrayList<>();
            for (Transaction t : reference) {
                Person p = map.get(t.getPersonId());
                labels.add(Objects.requireNonNullElse(p, Person.ME).getPersonName());
            }
            return labels;
        }

        @Override
        protected void failed() {
            ExpenseTrackerDataProcessor.Arguments args = params.arguments;
            logError(TAG,"filter with argument="+args+" failed",getException());
        }

        @Override
        protected void succeeded() {
            log(TAG,"filter task successful");
            onFilterFinish(getValue());
        }
    }

    public static class FilterParameters {
        final ExpenseTrackerDataProcessor.Arguments arguments;

        public FilterParameters(ExpenseTrackerDataProcessor.Arguments arguments) {
            this.arguments = arguments;
        }
    }

    public static class Result {
        FilterParameters parameters;
        List<Number> values;
        List<String> labels;
    }
}