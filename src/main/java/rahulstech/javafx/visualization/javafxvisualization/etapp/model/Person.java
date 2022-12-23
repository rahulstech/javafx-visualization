package rahulstech.javafx.visualization.javafxvisualization.etapp.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Person {

    public static final Person ME = new Person();
    static {
        ME.setPersonId(null);
        ME.setPersonName("Me");
    }

    @SerializedName("_id")
    private Long personId;

    @SerializedName("person_name")
    private String personName;

    public Person() {}

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
