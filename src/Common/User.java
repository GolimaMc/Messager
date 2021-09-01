package Common;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID=1L;
    private String Userid;
    private String Password;

    public User() {
    }

    public User(String userid, String password) {
        Userid = userid;
        Password = password;
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
