package ku.cs.services;

import ku.cs.models.advisor.Advisor;
import ku.cs.models.advisor.AdvisorList;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class AdvisorListFileDatasource implements Datasource<AdvisorList> {
    private String directoryName;
    private String fileName;

    public AdvisorListFileDatasource(String directoryName, String fileName) {
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
    public AdvisorList readData() {
        AdvisorList advisorList = new AdvisorList();
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
             BufferedReader buffer = new BufferedReader(inputStreamReader)) {

            String line;
            // Using a while loop to read data line by line
            while ((line = buffer.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                // Split the string by comma
                String[] data = line.split(",");

                // Read data by index and convert types accordingly
                String username = data[0];
                String password = data[1];
                String role = data[2];
                String firstName = data[3];
                String lastName = data[4];
                String advisorId = data[5];
                String faculty = data[6];
                String department = data[7];

                // Add the data to the list
                advisorList.addAdvisor(username, password, role, firstName, lastName, advisorId, faculty, department);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return advisorList;
    }

    @Override
    public void writeData(AdvisorList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
             BufferedWriter buffer = new BufferedWriter(outputStreamWriter)) {

            // สร้าง csv ของ FacultyCertifier และเขียนลงในไฟล์ทีละบรรทัด
            for (Advisor advisor : data.getAdvisorList()) {
                String line = advisor.toString();
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
