package ku.cs.models.user;

import ku.cs.models.department.Department;

import java.util.ArrayList;
import java.util.HashMap;

public class UserList {
    private final ArrayList<User> userList = new ArrayList<>();
    private final HashMap<String, User> userHashMap = new HashMap<>();

    public void addUser(User user) {
        String key = user.getUsername();
        userList.add(user);
        userHashMap.put(key, user);
    }

    public void removeUser(User user) {
        String key = user.getUsername();
        userList.remove(user);
        userHashMap.remove(key);
    }

    public void removeAllUser(UserList userList) {
        for(User user : userList.getUserList()) {
            userList.removeUser(user);
        }
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public ArrayList<User> getUserListByRole(String role) {
        ArrayList<User> userListByRole = new ArrayList<>();
        if(role == "All") {
            for(User user : userList) {
                if(!user.getRole().equals("Admin")) {
                    userListByRole.add(user);
                }
            }
        } else {
            for (User user : userList) {
                if (user.getRole().equals(role)) {
                    userListByRole.add(user);
                }
            }
        }
        return userListByRole;
    }

    public User findUserByUsername(String username) {
        String key = username;
        return userHashMap.get(key);
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User user = verifyPassword(username, oldPassword);

        if (user != null) {
            user.setPassword(newPassword);
            user.setIsChangePassword(false);
            return true;
        }

        return false;
    }

    public User verifyPassword(String username, String password) {
        User user = findUserByUsername(username);
        if (user == null) {
            return null;
        }
        if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean isValid(String username) {
        String key = username;
        return !userHashMap.containsKey(key);
    }
}