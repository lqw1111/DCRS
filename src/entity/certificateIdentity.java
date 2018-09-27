package entity;

public class certificateIdentity {
    public String department;
    public String status;
    public String id;

    public certificateIdentity() {
    }

    public certificateIdentity(String department, String status, String id){
        this.department = department;
        this.status = status;
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
