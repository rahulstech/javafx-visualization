package rahulstech.javafx.visualization.javafxvisualization.etapp.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Account {

    @SerializedName("_id")
    private Long accountId;
    @SerializedName("account_name")
    private String accountName;

    public Account() {}

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
