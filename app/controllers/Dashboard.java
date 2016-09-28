package controllers;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import models.User;
import models.utils.VkGroupMembersMaxException;
import models.utils.VkGroupNotFoundException;
import models.vk.VkGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.index;
import vk.group.VkGroupResponse;
import vk.group.VkGroupUtils;

/**
 * User: yesnault
 * Date: 22/01/12
 */
@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Dashboard.class);
    private static final int MAX_COUNT_VK_GROUP_MEMBERS = 100_000;

    @Inject
    private FormFactory formFactory;

    public Result index() {
        User currentUser = User.findByEmail(request().username());
        return ok(index.render(currentUser, formFactory.form(AddVkGroup.class)));
    }

    public Result addVkGroupAction() {
        Form<AddVkGroup> addVkGroupForm = formFactory.form(AddVkGroup.class).bindFromRequest();

        if (addVkGroupForm.hasErrors()) {
            return badRequest(index.render(User.findByEmail(request().username()), addVkGroupForm));
        }

        AddVkGroup addVkGroup = addVkGroupForm.get();

        VkGroup vkGroup = VkGroup.findByVkId(addVkGroup.vkGroupId);
        if (vkGroup == null) {
            vkGroup = new VkGroup();
            vkGroup.vkId = addVkGroup.vkGroupId;
            try {
                fillInfoFromVk(vkGroup);
            } catch (VkGroupNotFoundException e) {
                LOG.warn("add vkGroup: not found group with vkGroupId: " + vkGroup.vkId);
                addVkGroupForm.reject(e.getMessage());
                return badRequest(index.render(User.findByEmail(request().username()), addVkGroupForm));
            } catch (VkGroupMembersMaxException e) {
                LOG.warn("add vkGroup: more than max " + MAX_COUNT_VK_GROUP_MEMBERS);
                addVkGroupForm.reject(e.getMessage());
                return badRequest(index.render(User.findByEmail(request().username()), addVkGroupForm));
            }
        }
        vkGroup.users.add(User.findByEmail(ctx().session().get("email")));

        vkGroup.save();

        return index();
    }

    private void fillInfoFromVk(VkGroup vkGroup) {
        try {
            VkGroupResponse vkGroupResp = VkGroupUtils.getVkGroupFromVk(vkGroup.vkId);
            if (vkGroupResp == null) {
                throw new VkGroupNotFoundException("Group " + vkGroup.vkId + " not found");
            } else if (vkGroupResp.members_count > MAX_COUNT_VK_GROUP_MEMBERS) {
                throw new VkGroupMembersMaxException("Group have more than " + MAX_COUNT_VK_GROUP_MEMBERS + " members");
            }

            vkGroup.name = vkGroupResp.name;
            vkGroup.isClosed = vkGroupResp.is_closed;
            vkGroup.type = vkGroupResp.type;
            vkGroup.photo = vkGroupResp.photo;
            vkGroup.photoMedium = vkGroupResp.photo_medium;
            vkGroup.photoBig = vkGroupResp.photo_big;
        } catch (IOException e) {
            throw new VkGroupNotFoundException("Exception when getting group with id " + vkGroup.vkId);
        }
    }

    public static class AddVkGroup {

        @Constraints.Required
        private String vkGroupId;

        public String validate() {
            if (StringUtils.isBlank(vkGroupId)) {
                return Messages.get("Dashboard.not_empty");
            }

            String currentUserEmail = ctx().session().get("email");
            if (VkGroup.findBy(vkGroupId, currentUserEmail) != null) {
                return Messages.get("Dashboard.vk_id_already_exists");
            }

            return null;
        }

        public String getVkGroupId() {
            return vkGroupId;
        }

        public void setVkGroupId(String vkGroupId) {
            this.vkGroupId = vkGroupId;
        }
    }

    public Result removeVkGroup() {
        String vkGroupId = request().getQueryString("vkGroupId");
        VkGroup vkGroup = VkGroup.findBy(vkGroupId, request().username());

        if (vkGroup == null) {
            return badRequest("Group " + vkGroupId + " not found");
        }

        for (User user : new HashSet<>(vkGroup.users)) {
            if (user.getEmail().equals(request().username())) {
                vkGroup.users.remove(user);
            }
        }

        vkGroup.save();

        return ok("Удалена группа " + vkGroupId + "(" + vkGroup.name + ")");
    }
}
