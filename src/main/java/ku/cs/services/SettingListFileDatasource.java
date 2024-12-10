package ku.cs.services;

import ku.cs.models.user.Setting;
import ku.cs.models.user.SettingList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SettingListFileDatasource implements Datasource<SettingList> {
    private String directoryName;
    private String fileName;
    private DataRepository dataRepository;

    public SettingListFileDatasource(String directoryName, String fileName) {
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
    public SettingList readData() {
        SettingList settingList = new SettingList();

        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        BufferedReader buffer = new BufferedReader(inputStreamReader);

        String line;
        try {

            while ((line = buffer.readLine()) != null) {

                if (line.equals("")) continue;

                String[] data = line.split(",");

                String username = data[0];
                String theme = data[1];
                String fontSize = data[2];
                String fontFamily = data[3];

                Setting setting = new Setting(username, theme, fontSize, fontFamily);

                settingList.addSetting(setting);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return settingList;
    }



    @Override
    public void writeData(SettingList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
        BufferedWriter buffer = new BufferedWriter(outputStreamWriter);

        try {
            for (HashMap.Entry<String, Setting> entry : data.getSettingHashMap().entrySet()) {
                Setting setting = entry.getValue();
                String line = setting.toString();
                buffer.append(line);
                buffer.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
