package models;

import models.utils.AppException;
import models.utils.Hash;
import models.vk.VkGroup;
import play.data.format.Formats;
import play.data.validation.Constraints;
import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

/**
 * User: yesnault
 * Date: 20/01/12
 */
@Entity
@Table(name = "User_table")
public class User extends Model {

    @Id
    private Long id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    private String email;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    private String fullname;

    private String confirmationToken;

    @Constraints.Required
    @Formats.NonEmpty
    private String passwordHash;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateCreation;

    @Formats.NonEmpty
    private Boolean validated = false;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_vkGroup",
            joinColumns = {
                    @JoinColumn(name = "userId", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "vkGroupId", nullable = false, updatable = false)})
    private Set<VkGroup> vkGroups;

    // -- Queries (long id, user.class)
    private static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);

    /**
     * Retrieve a user from an email.
     *
     * @param email email to search
     * @return a user
     */
    public static User findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    /**
     * Retrieve a user from a fullname.
     *
     * @param fullname Full name
     * @return a user
     */
    public static User findByFullname(String fullname) {
        return find.where().eq("fullname", fullname).findUnique();
    }

    /**
     * Retrieves a user from a confirmation token.
     *
     * @param token the confirmation token to use.
     * @return a user if the confirmation token is found, null otherwise.
     */
    public static User findByConfirmationToken(String token) {
        return find.where().eq("confirmationToken", token).findUnique();
    }

    /**
     * Authenticate a User, from a email and clear password.
     *
     * @param email         email
     * @param clearPassword clear password
     * @return User if authenticated, null otherwise
     * @throws AppException App Exception
     */
    public static User authenticate(String email, String clearPassword) throws AppException {

        // get the user with email only to keep the salt password
        User user = find.where().eq("email", email).findUnique();
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword, user.passwordHash)) {
                return user;
            }
        }
        return null;
    }

    public void changePassword(String password) throws AppException {
        this.passwordHash = Hash.createPassword(password);
        this.save();
    }

    /**
     * Confirms an account.
     *
     * @return true if confirmed, false otherwise.
     * @throws AppException App Exception
     */
    public static boolean confirm(User user) throws AppException {
        if (user == null) {
            return false;
        }

        user.confirmationToken = null;
        user.validated = true;
        user.save();
        return true;
    }

    public User email(String email) {
        this.email = email;
        return this;
    }

    public User fullname(String fullname) {
        this.fullname = fullname;
        return this;
    }

    public User passwordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public User confirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public Set<VkGroup> getVkGroups() {
        return vkGroups;
    }

    public void setVkGroups(Set<VkGroup> vkGroups) {
        this.vkGroups = vkGroups;
    }
}
