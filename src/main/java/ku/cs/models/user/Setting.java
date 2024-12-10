package ku.cs.models.user;

public class Setting {
    private String username;
    private String theme;
    private String fontSize;
    private String fontFamily;

    public Setting(String username, String theme, String fontSize, String fontFamily) {
        this.username = username;
        this.theme = theme;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
    }

    public Setting(String username) {
        this(username,"/styles/blue-theme.css", "medium-font", "font4");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    @Override
    public String toString() {
        return String.join(",", username, theme, fontSize, fontFamily);
    }
}
