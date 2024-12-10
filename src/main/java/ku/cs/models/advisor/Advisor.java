package ku.cs.models.advisor;

import ku.cs.models.request.Request;
import ku.cs.models.request.RequestList;

import ku.cs.models.student.StudentList;
import ku.cs.models.user.User;


public class Advisor extends User {

    private String firstName;
    private String lastName;
    private String advisorId;
    private String faculty;
    private String department;
    private StudentList advisorStudents = new StudentList();
    private RequestList advisorRequests;

    public Advisor(String username, String password, String role, String firstName, String lastName, String advisorId, String faculty, String department) {
        super(username, password, role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.advisorId = advisorId;
        this.faculty = faculty;
        this.department = department;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAdvisorId() {
        return advisorId;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getDepartment() {
        return department;
    }

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

    public void setAdvisorId(String advisorId) {
        this.advisorId = advisorId;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    public void setStudentList(StudentList students) {
        advisorStudents = students;
    }

    public StudentList getStudentList() {
        return advisorStudents;
    }

    public RequestList getRequestList() {
        return advisorRequests;
    }

    @Override
    public String userToString(){
        return super.toString();
    }

    @Override
    public String textToSearch(){
        return String.join(" ",advisorId,firstName,lastName,getUsername(),faculty,department).toLowerCase();
    }

    @Override
    public String toString() {
        return String.join(",",
                getUsername(),
                getPassword(),
                getRole(),
                firstName,
                lastName,
                advisorId,
                faculty,
                department);
    }


}