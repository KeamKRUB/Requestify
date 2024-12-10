package ku.cs.services;

import ku.cs.models.department.DepartmentCertifier;
import ku.cs.models.department.DepartmentCertifierList;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DepartmentCertifierListFileDatasource implements Datasource<DepartmentCertifierList>{
    private String directoryName;
    private String fileName;

    public DepartmentCertifierListFileDatasource(String directoryName, String fileName) {
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
    public DepartmentCertifierList readData() {
        DepartmentCertifierList departmentCertifier = new DepartmentCertifierList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

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
            while ((line = buffer.readLine()) != null) {
                if (line.equals("")) continue;

                String[] data = line.split(",");

                if (data.length < 3) {
                    System.err.println("Invalid data format: " + line);
                    continue;
                }

                String firstName = data[0].trim();
                String lastName = data[1].trim();
                String department = data[2].trim();
                String position = data[3].trim();

                departmentCertifier.addCertifier(firstName, lastName, department, position);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return departmentCertifier;
    }


    @Override
    public void writeData(DepartmentCertifierList data) {
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
            for (DepartmentCertifier certifier : data.getCertifierList()) {
                String line = certifier.toString();
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
