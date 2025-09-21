package Models;

import java.util.Date;

public class Doctor extends User {
    private String Specialty;

    public String getSpecialty() { return Specialty; }
    public void setSpecialty(String specialty) { Specialty = specialty; }

    public Doctor(String name, String family, String password, String personal_Number,
                  String specialty, String email, String city, String address,
                  String city_Code, String phone_Number, Date working_Since) {
        this.Name = name;
        this.Family = family;
        this.Password = password;
        this.Personal_Number = personal_Number;
        this.Specialty = specialty;
        this.Email = email;
        this.City = city;
        this.Address = address;
        this.City_Code = city_Code;
        this.Phone_Number = phone_Number;
        this.Working_Since = working_Since;
    }
}
