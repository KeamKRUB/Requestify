package ku.cs.models.department;

public class Department{

    private String departmentName;
    private String facultyName;
    private String departmentId;

    public Department(String departmentId, String departmentName, String facultyName) {
        this.departmentName = departmentName;
        this.departmentId = departmentId;
        this.facultyName = facultyName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public String toString() {
        return String.join(",",departmentId, departmentName, facultyName);
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String textToSearch(){
        return String.join(" ", departmentId, departmentName, facultyName).toLowerCase();
    }

}