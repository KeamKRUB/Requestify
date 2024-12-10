package ku.cs.models.department;

import ku.cs.models.request.RequestList;
import ku.cs.models.student.StudentList;
import ku.cs.models.user.User;

public class DepartmentStaff extends User {

    private String firstName;
    private String lastName;
    private String faculty;
    private String department;
    private RequestList departmentRequests = new RequestList();
    private StudentList departmentStudents = new StudentList();

    public DepartmentStaff(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public DepartmentStaff(String username, String password, String role, String firstName, String lastName, String faculty,String department) {
        super(username,password,role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
        this.department = department;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFaculty() { return faculty; }

    public String getDepartment() { return department; }

    @Override
    public String getName() {
        return this.getFirstName() + " " + this.getLastName();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStudentList(StudentList students) {
        departmentStudents = students;
    }
    public void setRequestList(RequestList requests) {
        departmentRequests = requests;
    }
    public RequestList getRequestList() {
        return departmentRequests;
    }
    public StudentList  getStudentList() {
        return departmentStudents;
    }
    public void loadRequestList(RequestList requests) {
        departmentRequests = requests;
    }

    @Override
    public String userToString(){
        return super.toString();
    }

    @Override
    public String toString() {
        return String.join(",",getUsername(),getPassword(),getRole(),firstName,lastName,faculty,department);
    }

    @Override
    public String textToSearch(){
        return String.join(" ",firstName,lastName,getUsername(),faculty,department).toLowerCase();
    }
}
