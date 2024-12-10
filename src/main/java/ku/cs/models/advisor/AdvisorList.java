package ku.cs.models.advisor;

import java.util.ArrayList;

public class AdvisorList {
    private ArrayList<Advisor> advisorList;

    public AdvisorList() {
        this.advisorList = new ArrayList<>();
    }

    public void addAdvisor(Advisor advisor) {
        advisorList.add(advisor);
    }

    public void addAdvisor(String username, String password, String role, String firstName, String lastName, String advisorId, String faculty, String department) {
        advisorList.add(new Advisor(username, password, role, firstName, lastName, advisorId, faculty, department));
    }

    public void removeAdvisor(Advisor advisor) {
        advisorList.remove(advisor);
    }

    public ArrayList<Advisor> getAdvisorList() {
        return advisorList;
    }

    public ArrayList<Advisor> getAdvisorListByDepartment(String department) {
        ArrayList<Advisor> advisorListByDepartment = new ArrayList<>();
        for (Advisor advisor : advisorList) {
            if (advisor.getDepartment().equals(department)) {
                advisorListByDepartment.add(advisor);
            }
        }
        return advisorListByDepartment;
    }

    public ArrayList<Advisor> getAdvisorListByFaculty(String faculty) {
        ArrayList<Advisor> advisorListByFaculty = new ArrayList<>();
        for (Advisor advisor : advisorList) {
            if (advisor.getFaculty().equals(faculty)) {
                advisorListByFaculty.add(advisor);
            }
        }
        return advisorListByFaculty;
    }

    public Advisor findAdvisorByUsername(String username) {
        for (Advisor advisor : advisorList) {
            if (advisor.getUsername().equals(username)) {
                return advisor;
            }
        }
        return null;
    }

    public Advisor findAdvisorById(String id) {
        for (Advisor advisor : advisorList) {
            if (advisor.getAdvisorId().equals(id)) {
                return advisor;
            }
        }
        return null;
    }

}
