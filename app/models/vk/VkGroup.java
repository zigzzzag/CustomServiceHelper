package models.vk;

import com.avaje.ebean.Model;
import models.User;
import play.data.validation.Constraints;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Zigzag on 03.09.2016.
 */
@Entity
@Table(name = "vk_group")
public class VkGroup extends Model {

    @Id
    public Long id;

    @Constraints.Required
    public String vkId;

    @Constraints.Required
    public String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "vkgroup_vkuser", joinColumns = {
            @JoinColumn(name = "vk_group_id", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "vk_user_id", nullable = false, updatable = false)})
    public Set<VkUser> vkUsers;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "vkGroups")
    public Set<User> users = new HashSet<>();

    /**
     * является ли сообщество закрытым. Возможные значения:
     * 0 — открытое;
     * 1 — закрытое;
     * 2 — частное.
     */
    @Column(name = "is_closed")
    public Integer isClosed;

    @Column(name = "type")
    public String type;

    @Column(name = "photo")
    public String photo;

    @Column(name = "photo_medium")
    public String photoMedium;

    @Column(name = "photo_big")
    public String photoBig;

    @Column(name = "update_count", nullable = false, columnDefinition = "int default '0'")
    public Integer updateCount = 0;


    public static Model.Finder<Long, VkGroup> finder = new Model.Finder<>(Long.class, VkGroup.class);

    public static List<VkGroup> getAll() {
        return finder.all();
    }

    public static VkGroup findByVkId(String vkId) {
        return finder.where().eq("vkId", vkId).findUnique();
    }

    public static List<VkGroup> findByEmail(String email) {
        return finder.where().eq("users.email", email).findList();
    }

    public static VkGroup findBy(String vkId, String email) {
        return finder.where().eq("vkId", vkId).eq("users.email", email).findUnique();
    }

    public Set<VkUser> getExitVkUsers(Set<VkUser> newVkUsers) {
        Set<VkUser> exitVkUsers = new HashSet<>();
        for (VkUser vkUser : vkUsers) {
            if (!newVkUsers.contains(vkUser)) {
                exitVkUsers.add(vkUser);
            }
        }
        return exitVkUsers;
    }

    public Set<VkUser> getEnterVkUsers(Set<VkUser> newVkUserIds) {
        Set<VkUser> enterVkUsers = new HashSet<>();
        for (VkUser newVkUserId : newVkUserIds) {
            if (!vkUsers.contains(newVkUserId)) {
                enterVkUsers.add(newVkUserId);
            }
        }
        return enterVkUsers;
    }


}
