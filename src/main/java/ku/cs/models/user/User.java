package ku.cs.models.user;

import ku.cs.models.advisor.Advisor;
import ku.cs.models.student.Student;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private String username;
    private String password;
    private String role;
    private String profilePath;
    private boolean changedProfile;
    private boolean isLoggedIn;
    private boolean isAvailable;
    private boolean changePassword;
    private String lastestLogin;

    private Setting userSetting;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.changePassword = false;
        this.role = role;
        this.profilePath = "/images/profile.png";
        userSetting = new Setting(username);
        if (role == "Advisor") {
            changePassword = true;
        }
        if ("DepartmentStaff".equals(role)) {
            this.changePassword = true;
        }
        if ("FacultyStaff".equals(role)) {
            this.changePassword = true;
        }

        this.changedProfile = false;
        this.isLoggedIn = false;
        this.isAvailable = true;

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        lastestLogin = currentTime.format(formatter);
    }

    public User() {
        this.username = "";
        this.password = "";
        this.role = "";
        this.profilePath = "/images/profile.png";
        userSetting = new Setting(username);
        this.changedProfile = false;
        this.isLoggedIn = false;
        this.isAvailable = true;
        this.changePassword = false;

        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        lastestLogin = currentTime.format(formatter);
    }

    public void setIsChangePassword(boolean isChangedPassword) {
        this.changePassword = isChangedPassword;
    }
    public boolean getIsChangePassword() {
        return changePassword;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void setChangedProfile (boolean changedProfile) {
        this.changedProfile = changedProfile;
    }

    public boolean isChangedProfile() {
        return changedProfile;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isAvailable() { return isAvailable;}

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean getAvailable() {
        return isAvailable;
    }
    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getId(){
        if (this instanceof Student){
            return ((Student)this).getStudentId();
        }
        if (this instanceof Advisor){
            return ((Advisor)this).getAdvisorId();
        }
        else{
            return "-";
        }
    }

    public void setUserSetting(Setting setting) {
        this.userSetting = setting;
    }

    public Setting getUserSetting() {
        return userSetting;
    }

    public String userToString(){
        return this.toString();
    }

    public void setChangePassword(String password) {
        this.password = password;
        changePassword = false;
    }

    public void setLastestLogin(String lastestLogin) {
        this.lastestLogin = lastestLogin;
    }

    public String getLastestLogin() {
        return this.lastestLogin;
    }

    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return username + "," + password + "," + role + "," + profilePath + "," + changedProfile + "," + isLoggedIn + "," + isAvailable + "," + changePassword + "," + lastestLogin;
    }

    public String textToSearch(){
        return String.join("username","role");
    }
}