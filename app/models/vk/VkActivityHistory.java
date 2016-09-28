package models.vk;

import javax.persistence.Column;
import org.joda.time.DateTime;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Zigzag on 11.09.2016.
 */
@Entity
@Table(name = "vk_activity_history")
public class VkActivityHistory {

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "history_date")
    public DateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", nullable = false)
    public VkUser vkUser;
}
