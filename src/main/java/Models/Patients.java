package Models;

public class Patients {

    public Patients(String insurance_number, String first_name, String last_name, String birthday, String address, String phone_number, String email, String insurance) {
        this.insurance_number = insurance_number;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthday = birthday;
        this.address = address;
        this.phone_number = phone_number;
        this.email = email;
        this.insurance = insurance;
    }

    public String getInsurance_number() {
        return insurance_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail() {
        return email;
    }

    public String getInsurance() {
        return insurance;
    }

    private String insurance_number;
    private String first_name;
    private String last_name;
    private String birthday;
    private String address;
    private String phone_number;
    private String email;
    private String insurance;
}
