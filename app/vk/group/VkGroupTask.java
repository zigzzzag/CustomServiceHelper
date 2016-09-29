package vk.group;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.vk.VkEnterExitHistory;
import models.vk.VkGroup;
import models.vk.VkUser;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static models.vk.VkEnterExitHistory.Status.ENTER;
import static models.vk.VkEnterExitHistory.Status.EXIT;
import static models.vk.VkEnterExitHistory.Status.INIT;

/**
 * Created by sbt-nikiforov-mo on 28.09.16.
 */
public class VkGroupTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(VkGroupTask.class);

    @Override
    public void run() {
        List<VkGroup> allVkGroups = VkGroup.getAll();

        LOG.info("allVkGroups: {}", allVkGroups);

        for (VkGroup vkGroup : allVkGroups) {
            analizeVkGroup(vkGroup);
        }
    }

    private void analizeVkGroup(VkGroup vkGroup) {
        LOG.info("start analize group: {}", vkGroup.vkId);

        long startTime = System.currentTimeMillis();
        VkGroupMembersResponse resp;
        try {
            resp = VkGroupUtils.getVkGroupMembersFromVk(vkGroup.vkId);
            LOG.info("getVkGroupMembersFromVk \"{}\" time: {}ms, resp: {}", vkGroup.vkId,
                    System.currentTimeMillis() - startTime, resp.count);
        } catch (IOException e) {
            LOG.error("failed getVkGroupMembersFromVk for vkGroup: " + vkGroup.vkId, e);
            return;
        }


        Set<VkUser> actualVkUsers = new HashSet<>();
        for (Long vkUserId : resp.users) {
            actualVkUsers.add(VkUser.findOrCreate(vkUserId));
        }

        DateTime today = new DateTime();
        if (vkGroup.updateCount == 0) {
            LOG.info("start init histories for group: {}", vkGroup.vkId);
            for (VkUser vkUser : actualVkUsers) {
                VkEnterExitHistory history = new VkEnterExitHistory(today, vkGroup, vkUser, INIT);
                history.save();
            }
            vkGroup.vkUsers.addAll(actualVkUsers);
            vkGroup.save();
            LOG.info("finish init histories for group: {}", vkGroup.vkId);
        } else {
            LOG.info("start enter exit histories for group: {}", vkGroup.vkId);
            Set<VkUser> exitVkUsers = vkGroup.getExitVkUsers(actualVkUsers);
            vkGroup.vkUsers.removeAll(exitVkUsers);
            vkGroup.save();
            for (VkUser vkUser : exitVkUsers) {
                VkEnterExitHistory history = new VkEnterExitHistory(new DateTime(), vkGroup, vkUser, EXIT);
                history.save();
            }
            Set<VkUser> enterVkUsers = vkGroup.getEnterVkUsers(actualVkUsers);
            vkGroup.vkUsers.addAll(enterVkUsers);
            vkGroup.save();
            for (VkUser vkUser : enterVkUsers) {
                VkEnterExitHistory history = new VkEnterExitHistory(new DateTime(), vkGroup, vkUser, ENTER);
                history.save();
            }
            LOG.info("finish enter exit histories for group: {}", vkGroup.vkId);
        }

        vkGroup.updateCount++;
        vkGroup.save();
    }
}
