<h2>JavaFX Visusalization</h2>
<hr/>
<p>This is an example of <code>javafx.chart</code>, specifically BarChart and LineChart.
This app showcase the transaction data from another android app <a href="https://github.com/rahulstech/expense-tracker-android-app">Expense Tracker</a>.
</p>
<h3>Library In Use</h3>
<hr/>
<ul>
<li><a href="https://github.com/kordamp/bootstrapfx">BootstrapFX</a>: for bootstrap like styling</li>
<li><a href="https://github.com/kordamp/ikonli">ikonli</a>: for icons</li>
<li><a href="https://kordamp.org/ikonli/">ikonli-fontawesome5-pack</a>: for font awesome ikonli icon pack </li>
<li><a href="https://github.com/google/gson">Gson</a>: for reading json file </li>
<li>Java 17</li>
<li>JavaFX 17.0.2</li>
</ul>
<h3>Description</h3>
<hr/>
<p>
To use this app get the Expense Tracker backup file. Currently, this app handles backup file schema
version 7 only. A sample json file is provided for test purpose.
<br/>
<img src="/screenshots/home.png" alt="Home Screen"/>
<br/>
Use the upload button to choose a file from file system.
<br/>
On successful upload following screen will show
<br/>
<img src="/screenshots/chart_view.png" alt="Chart View"/>
<br/>
Use the info button to get the upload info like this:
<img src="/screenshots/upload_info.png" alt="Upload Info"/>
<br/>
Use filter button to filter data and show chart based on filtered data. Filter button
show a custom dialog with a backdrop. Below are some example of inputting filter data:
<br/>
<img src="/screenshots/filter_dialog_1.png" alt="Filter Dialog"/>
<br/>
<img src="/screenshots/filter_dialog_groupby.png" alt="Filter Dialog Group By"/>
<br/>
<img src="/screenshots/filter_dialog_account.png" alt="Filter Dialog Account"/>
<br/>
<img src="/screenshots/filter_dialog_person.png" alt="Filter Dialog Person"/>
<br/>
<img src="/screenshots/filter_dialog_date_start.png" alt="Filter Dialog Date Start"/>
<br/>
<img src="/screenshots/filter_dialog_date_end.png" alt="Filter Dialog Date End"/>
<br/>
<img src="/screenshots/filter_dialog_filled_input.png" alt="Filter Dialog Filled Input"/>
<br/>
<img src="/screenshots/filter_dialog_filled_input_2.png" alt="Filter Dialog Filled Input 2"/>
<br/>
Click filter button to apply filter or cancel to cancel. When chart data is prepared chart is shown.
Following are some example of chart:
<br/>
<img src="/screenshots/chart1.png" alt="chart1"/>
<br/>
<img src="/screenshots/chart2.png" alt="chart2"/>
<br/>
<img src="/screenshots/chart3.png" alt="chart3"/>
<br/>
Use the info button to get the info about the current chart:
<br/>
<img src="/screenshots/chart_info_1.png" alt="Chart Info 1"/>
<br/>
<img src="/screenshots/chart_info_2.png" alt="Chart Info 2"/>
<br/>
<img src="/screenshots/chart_info_3.png" alt="Chart Info 3"/>
<br/>
For and working example checkout the <a href="https://youtu.be/mD2W7vwvo_g">video</a>
</p>