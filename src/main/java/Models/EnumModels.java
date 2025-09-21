package Models;

public class EnumModels
{
    public enum UserRole {
        Personal,
        Doctor,
        Admin
    }
    public enum Appointment_Status
    {
        Open,
        Completed,
        Delay,
        Cancel
    }
    public enum VacationType
    {
        VACATION,   // regulärer Urlaub
        TIME_OFF,   // frei
        SICK_LEAVE, // krank
        HALF_DAY    // halber Tag Urlaub
    }


}
