package Models;


import java.sql.Timestamp;
import java.util.Date;

public class ToDos
{
    private int id;
    private String title;
    private String description;
    private String personalId;
    private String doctorId;
    private Date dueDate;
    private boolean isDone;
    private String priority;
    private Timestamp createdAt;

    public ToDos(int id, String title, String description, String personalId, String doctorId, java.sql.Date dueDate, boolean isDone, String priority, java.sql.Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.personalId = personalId;
        this.doctorId = doctorId;
        this.dueDate = dueDate;
        this.isDone = isDone;
        this.priority = priority;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }


    @Override
    public String toString() {
        return "ToDos{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", personalId=" + personalId +
                ", doctorId=" + doctorId +
                ", dueDate=" + dueDate +
                ", isDone=" + isDone +
                ", priority='" + priority + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}
