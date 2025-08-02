package dto;

public class Authority {
    public String username;
    public String name;
    public Role role;

    public Authority(String username, String name, Role role) {
        this.username = username;
        this.name = name;
        this.role = role;
    }
}
