package ku.cs.models.user;

import java.util.HashMap;

public class SettingList {
    private final HashMap<String, Setting> settingHashMap = new HashMap<>();

    public void addSetting(Setting setting) {
        String key = setting.getUsername();
        settingHashMap.put(key, setting);
    }

    public void removeSetting(Setting setting) {
        String key = setting.getUsername();
        settingHashMap.remove(key);
    }

    public Setting getSetting(String key) {
        return settingHashMap.get(key);
    }

    public HashMap<String, Setting> getSettingHashMap() {
        return settingHashMap;
    }
}
