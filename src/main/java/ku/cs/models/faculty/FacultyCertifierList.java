package ku.cs.models.faculty;

import java.util.ArrayList;

public class FacultyCertifierList {
    private ArrayList<FacultyCertifier> facultyCertifierList = new ArrayList<>();

    public void addCertifier(FacultyCertifier certifier) {
        facultyCertifierList.add(certifier);
    }

    public void addCertifier(String firstName, String lastName, String faculty, String position) {
        facultyCertifierList.add(new FacultyCertifier(firstName,lastName,faculty,position));
    }

    public void removeCertifier(FacultyCertifier certifier) {
        facultyCertifierList.remove(certifier);
    }

    public FacultyCertifierList getCertifierListByFaculty(String faculty){
        FacultyCertifierList thisFacultyCertifierList = new FacultyCertifierList();
        for (FacultyCertifier certifier : facultyCertifierList) {
            if (certifier.getFaculty().equals(faculty)) {
                thisFacultyCertifierList.addCertifier(certifier);
            }
        }
        return thisFacultyCertifierList;
    }

    public ArrayList<FacultyCertifier> getCertifierList() {
        return facultyCertifierList;
    }

    public FacultyCertifier findByObject(FacultyCertifier facultyCertifier) {
        for (FacultyCertifier certifier : facultyCertifierList) {
            if (certifier.equals(facultyCertifier)) {
                return certifier;
            }
        }
        return null;
    }

}
