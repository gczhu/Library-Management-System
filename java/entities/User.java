package entities;

import java.util.Objects;
import java.util.Random;

/**
 * @author 祝广程
 * @version 1.0
 */
public final class User {

    public enum UserRole {
        User("U"),
        Admin("A");

        private final String str;

        UserRole(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }

        public static User.UserRole values(String s) {
            if ("U".equals(s)) {
                return User;
            } else if ("A".equals(s)) {
                return Admin;
            } else {
                return null;
            }
        }

        public static UserRole random() {
            return values()[new Random().nextInt(values().length)];
        }
    };

    private String account;
    private String password;
    private UserRole role;
    private Integer cardId;

    /* we assume that two users are equal iff their account is equal */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return account.equals(user.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, password, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    public User() {

    }

    public User(String account, String password, UserRole role, Integer cardId) {
        this.account = account;
        this.password = password;
        this.role = role;
        this.cardId = cardId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Integer getCardId() {
        return cardId;
    }

    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

}
