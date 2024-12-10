package ku.cs.models.student;

import ku.cs.models.user.User;

public class Student extends User {
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String advisor;
    private String department;
    private String faculty;

    public Student(String username, String password, String role, String studentId, String firstName, String lastName, String email, String advisor, String department, String faculty) {
        super(username, password, role);
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.advisor = advisor;
        this.department = department;
        this.faculty = faculty;
    }

    public Student(){
        this.studentId = "";
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.advisor = "undefined";
        this.department = "";
        this.faculty = "";
    }


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdvisor() {
        return advisor;
    }

    public void setAdvisor(String advisor) {
        this.advisor = advisor;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) { this.department = department; }

    public String getFaculty() { return faculty; }

    public void setFaculty(String faculty) { this.faculty = faculty; }

    public void hasAdvisor(){
        if (advisor.equals("undefined") || advisor.isEmpty()){
            throw new IllegalArgumentException("คุณยังไม่มีอาจารย์ที่ปรึกษา ไม่สามารถสร้างคำร้องได้\nโปรดติดต่อที่ผู้ดูแลระบบ");
        }

    }

    @Override
    public String getName() {
        return this.getFirstName() + " " + this.getLastName();
    }

    @Override
    public String userToString(){
        return super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student student)) return false;
        return studentId.equals(student.getStudentId()) &&
                firstName.equals(student.getFirstName()) &&
                lastName.equals(student.getLastName()) &&
                email.equals(student.getEmail());
    }

    @Override
    public String toString() {
        return String.join(",",getUsername(),getPassword(),getRole(),studentId,firstName,lastName,email,advisor,department,faculty);
    }

    @Override
    public String textToSearch(){
        return String.join(" ",studentId,firstName,lastName,email,advisor,department).toLowerCase();
    }
}