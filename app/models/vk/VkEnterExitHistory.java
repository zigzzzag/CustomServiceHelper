package models.vk;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by Zigzag on 14.09.2016.
 */
@Entity
@Table(name = "vk_enter_exit_history")
public class VkEnterExitHistory extends Model {

    private static final Logger LOG = LoggerFactory.getLogger(VkEnterExitHistory.class);

    @Id
    public Long id;

    @Constraints.Required
    @Column(name = "history_date")
    public DateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vkGroupId", nullable = false)
    public VkGroup vkGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vkUserId", nullable = false)
    public VkUser vkUser;

    @Constraints.Required
    public Status status;

    public static Model.Finder<Long, VkEnterExitHistory> finder = new Model.Finder<>(Long.class, VkEnterExitHistory.class);
    private static final int MAX_ROWS = 1000;

    public VkEnterExitHistory() {
    }

    public VkEnterExitHistory(DateTime date, VkGroup vkGroup, VkUser vkUser, Status status) {
        this.date = date;
        this.vkGroup = vkGroup;
        this.vkUser = vkUser;
        this.status = status;
    }

    public static List<VkEnterExitHistory> find(DateTime from, DateTime to, VkGroup vkGroupId, String status) {
        ExpressionList<VkEnterExitHistory> list = finder.where().eq("vkGroup", vkGroupId);
        if (StringUtils.isNotBlank(status)) {
            try {
                list.eq("status", Status.valueOf(status));
            } catch (IllegalArgumentException ex) {
                LOG.error("Not determine status: " + status, ex);
            }
        }
        if (from != null) {
            list.ge("date", from);
        }
        if (to != null) {
            DateTime toConvert = to.plusDays(1).minusSeconds(1);
            list.le("date", toConvert);
        }
        return list.setMaxRows(MAX_ROWS).findList();
    }

    public enum Status {
        ENTER,
        EXIT,
        INIT
    }
}
