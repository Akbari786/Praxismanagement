package Models;

import java.util.Date;

public class Personal extends User {

    public Personal(String name, String family, String password, String personal_Number,
                    String email, String city, String address, String city_Code,
                    String phone_Number, Date working_Since) {
        this.Name = name;
        this.Family = family;
        this.Password = password;
        this.Personal_Number = personal_Number;
        this.Email = email;
        this.City = city;
        this.Address = address;
        this.City_Code = city_Code;
        this.Phone_Number = phone_Number;
        this.Working_Since = working_Since;
    }
    public Personal(String personalNumber, String name, String family) {
        this.Personal_Number = personalNumber;
        this.Name = name;
        this.Family = family;
    }


}
