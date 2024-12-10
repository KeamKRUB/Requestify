package ku.cs.models.user;

public class UserSession {
    private final User loggedInUser;
    private static UserSession session;

    public UserSession(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public UserSession() {
        this.loggedInUser = null;
    }

    public static UserSession getSession() {
        if (session != null) {
            return session;
        }
        return null;
    }

    public static void createSession(User user) {
        if (session != null) {
            session = null;
        }
        session = new UserSession(user);
    }

    public User getLoggedInUser() {
        if (getSession() == null) {
            return null;
        }
        return loggedInUser;
    }

    public static void clearSession() {
        session = null;
    }

    public boolean isLoggedIn() {
        return this.loggedInUser != null;
    }
}

