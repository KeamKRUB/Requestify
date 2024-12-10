package ku.cs.models.faculty;

import ku.cs.models.request.RequestList;
import ku.cs.models.user.User;

public class FacultyStaff extends User {

    private String firstName;
    private String lastName;
    private String faculty;
    private RequestList facultyRequests = new RequestList();

    public FacultyStaff(String username,String password, String role,String firstName, String lastName, String faculty) {
        super(username,password,role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFaculty() { return faculty; }

    @Override
    public String getName() {
        return this.getFirstName() + " " + this.getLastName();
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public RequestList getRequestList() {
        return facultyRequests;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof FacultyStaff)) return false;
        FacultyStaff staff = (FacultyStaff) obj;
        return getUsername().equals(staff.getUsername());
    }

    @Override
    public String textToSearch(){
        return String.join(" ",getFirstName(),getLastName(),getFaculty(),getUsername());
    }

    @Override
    public String userToString(){
        return super.toString();
    }

    @Override
    public String toString(){
        return String.join(",",getUsername(),getPassword(),getRole(),firstName,lastName,faculty);
    }
}
