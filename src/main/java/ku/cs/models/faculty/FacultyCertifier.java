package ku.cs.models.faculty;

public class FacultyCertifier {
    private String firstName;
    private String lastName;
    private String faculty;
    private String position;

    public FacultyCertifier(String firstName, String lastName,String faculty, String position) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.faculty = faculty;
        this.position = position;
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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof FacultyCertifier)) return false;
        FacultyCertifier certifier = (FacultyCertifier) obj;
        return firstName.equals(certifier.firstName) &&
                lastName.equals(certifier.lastName) &&
                position.equals(certifier.position);
    }

    public String textToSearch(){
        return String.join(" ",firstName, lastName, position);
    }

    public String toString(){
        return String.join(",",firstName, lastName,faculty, position);
    }
}
