package Models;

import java.util.Date;

public abstract class User {
    protected String Name;
    protected String Family;
    protected String Password;
    protected String Personal_Number;
    protected String Email;
    protected String Address;
    protected String City;
    protected String City_Code;
    protected String Phone_Number;
    protected Date Working_Since;
    protected String specialty;   // <--- neu

    // --- Getter & Setter ---
    public String getName() { return Name; }
    public void setName(String name) { Name = name; }

    public String getFamily() { return Family; }
    public void setFamily(String family) { Family = family; }

    public String getPassword() { return Password; }
    public void setPassword(String password) { Password = password; }

    public String getPersonal_Number() { return Personal_Number; }
    public void setPersonal_Number(String personal_Number) { Personal_Number = personal_Number; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }

    public String getAddress() { return Address; }
    public void setAddress(String address) { Address = address; }

    public String getCity() { return City; }
    public void setCity(String city) { City = city; }

    public String getCity_Code() { return City_Code; }
    public void setCity_Code(String city_Code) { City_Code = city_Code; }

    public String getPhone_Number() { return Phone_Number; }
    public void setPhone_Number(String phone_Number) { Phone_Number = phone_Number; }

    public Date getWorking_Since() { return Working_Since; }
    public void setWorking_Since(Date working_Since) { Working_Since = working_Since; }

    // Getter & Setter für specialty
    public String getSpecialty() { return specialty;}

    public void setSpecialty(String specialty) {this.specialty = specialty; }


}
