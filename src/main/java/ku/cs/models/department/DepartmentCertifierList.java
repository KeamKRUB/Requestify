package ku.cs.models.department;

import ku.cs.models.faculty.FacultyCertifier;
import ku.cs.models.faculty.FacultyCertifierList;

import java.util.ArrayList;

public class DepartmentCertifierList {
    private ArrayList<DepartmentCertifier> departmentCertifierList = new ArrayList<>();

    public void addCertifier(DepartmentCertifier certifier) {
        departmentCertifierList.add(certifier);
    }

    public void addCertifier(String firstName, String lastName,  String department,String position) {
        departmentCertifierList.add(new DepartmentCertifier(firstName, lastName, department, position));
    }

    public void removeCertifier(DepartmentCertifier certifier) {
        departmentCertifierList.remove(certifier);
    }

    public ArrayList<DepartmentCertifier> getCertifierList() {
        return departmentCertifierList;
    }

    public DepartmentCertifierList getCertifierListByDepartment (String department){
        DepartmentCertifierList thisDepartmentCertifierList = new DepartmentCertifierList();
        for (DepartmentCertifier certifier : departmentCertifierList) {
            if (certifier.getDepartment().equals(department)) {
                thisDepartmentCertifierList.addCertifier(certifier);
            }
        }
        return thisDepartmentCertifierList;
    }


    public DepartmentCertifier findByObject(DepartmentCertifier departmentCertifier) {
        for (DepartmentCertifier certifier : departmentCertifierList) {
            if (certifier.equals(departmentCertifier)) {
                return certifier;
            }
        }
        return null;
    }

}
