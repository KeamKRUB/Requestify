package ku.cs.models.request;

import java.util.ArrayList;

public class SubjectList extends Subject {
    private ArrayList<Subject> subjects = new ArrayList<>();

    public SubjectList() {
        subjects = new ArrayList<Subject>();
    }

    public void addSubject(Subject subject){
        subjects.add(subject);
    }

    public void addAllSubject(ArrayList<Subject> subjectList){
        subjects.addAll(subjectList);
    }

    public ArrayList<Subject> getSubjects(){
        return subjects;
    }

    public SubjectList getSubjectByRequestId (String requestId){
        SubjectList subjectList = new SubjectList();
        for(Subject subject : subjects){
            if (subject.getRequestId().equals(requestId)){
                subjectList.addSubject(subject);
            }
        }
        return subjectList;
    }

    public void removeSubject(Subject subject) {
        subjects.remove(subject);
    }

    public int getSize() {
        return subjects.size();
    }
}
