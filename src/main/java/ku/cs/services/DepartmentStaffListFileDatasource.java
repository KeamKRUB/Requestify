package ku.cs.services;

import ku.cs.models.department.DepartmentStaff;
import ku.cs.models.department.DepartmentStaffList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DepartmentStaffListFileDatasource implements Datasource<DepartmentStaffList>{
    private String directoryName;
    private String fileName;

    public DepartmentStaffListFileDatasource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();
    }

    private void checkFileIsExisted() {
        File file = new File(directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = directoryName + File.separator + fileName;
        file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public DepartmentStaffList readData() {
        DepartmentStaffList departmentStaffList = new DepartmentStaffList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        // เตรียม object ที่ใช้ในการอ่านไฟล์
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream,
                StandardCharsets.UTF_8
        );

        BufferedReader buffer = new BufferedReader(inputStreamReader);

        String line = "";
        try {
            // ใช้ while loop เพื่ออ่านข้อมูลรอบละบรรทัด
            while ( (line = buffer.readLine()) != null ){
                // ถ้าเป็นบรรทัดว่าง ให้ข้าม
                if (line.equals("")) continue;

                // แยกสตริงด้วย ,
                String[] data = line.split(",");

                // อ่านข้อมูลตาม index แล้วจัดการประเภทของข้อมูลให้เหมาะสม
                String username = data[0];
                String password = data[1];
                String role = data[2];
                String firstName = data[3].trim();
                String lastName = data[4].trim();
                String faculty = data[5].trim();
                String department = data[6].trim();
                // เพิ่มข้อมูลลงใน list
                departmentStaffList.addStaff(username, password, role, firstName, lastName, faculty, department);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return departmentStaffList;
    }

    @Override
    public void writeData(DepartmentStaffList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        // เตรียม object ที่ใช้ในการเขียนไฟล์
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream,
                StandardCharsets.UTF_8
        );
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);

        try {
            // สร้าง csv ของ FacultyCertifier และเขียนลงในไฟล์ทีละบรรทัด
            for (DepartmentStaff staff : data.getDepartmentStaffList()) {
                String line = staff.toString();
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}