package cafeteria;

public abstract class User {
	private final int userId;
    private final String username;
    private final String password;
    
    public User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
    
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    
    public abstract String getRole();
    public abstract boolean canEditMenu();
    public abstract boolean canViewReports();

}
