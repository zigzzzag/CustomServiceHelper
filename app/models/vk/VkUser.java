package models.vk;

import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * Created by sbt-nikiforov-mo on 01.09.16.
 */
@Entity
@Table(name = "vk_user")
public class VkUser extends Model {

    @Id
    public Long id;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public Long vkId;

    public String firstName;

    public String lastName;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "vkUsers")
    public Set<VkGroup> vkGroups;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vkUser")
    public Set<VkActivityHistory> activityHistory;

    public VkUser() {
    }

    public VkUser(Long vkId) {
        this.vkId = vkId;
    }

    public static Model.Finder<Long, VkUser> finder = new Model.Finder<>(Long.class, VkUser.class);

    public static VkUser findByVkId(Long vkId) {
        return finder.where().eq("vkId", vkId).findUnique();
    }

    public static VkUser findOrCreate(Long vkId) {
        VkUser vkUser = findByVkId(vkId);
        if (vkUser == null) {
            vkUser = new VkUser(vkId);
            vkUser.save();
        }
        return vkUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VkUser vkUser = (VkUser) o;

        if (!id.equals(vkUser.id)) return false;
        return vkId.equals(vkUser.vkId);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + vkId.hashCode();
        return result;
    }
}
