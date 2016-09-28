package controllers;

import models.User;
import models.utils.VkGroupNotFoundException;
import models.vk.VkEnterExitHistory;
import models.vk.VkGroup;
import models.vk.VkUser;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.data.Form;
import play.data.validation.Constraints;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.index;
import vk.group.VkGroupMembersResponse;
import vk.group.VkGroupResponse;
import vk.group.VkGroupUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static play.data.Form.form;

/**
 * User: yesnault
 * Date: 22/01/12
 */
@Security.Authenticated(Secured.class)
public class Dashboard extends Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Dashboard.class);

    public Result index() {
        return ok(index.render(User.findByEmail(request().username()), Form.form(AddVkGroup.class)));
    }

    public Result addVkGroupAction() {
        LOG.info("hello");
        Form<AddVkGroup> addVkGroupForm = form(AddVkGroup.class).bindFromRequest();

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
                LOG.warn("not found group with vkGroupId: " + vkGroup.vkId, e);
                addVkGroupForm.reject(e.getMessage());
                return badRequest(index.render(User.findByEmail(request().username()), addVkGroupForm));
            }
        }
        vkGroup.users.add(User.findByEmail(ctx().session().get("email")));

        vkGroup.save();

        return index();
    }

    private void fillInfoFromVk(VkGroup vkGroup) throws VkGroupNotFoundException {
        try {
            VkGroupResponse vkGroupResp = VkGroupUtils.getVkGroupFromVk(vkGroup.vkId);
            if (vkGroupResp == null) {
                throw new VkGroupNotFoundException("Group " + vkGroup.vkId + " not found");
            }

            vkGroup.name = vkGroupResp.getName();
            vkGroup.isClosed = vkGroupResp.getIs_closed();
            vkGroup.type = vkGroupResp.getType();
            vkGroup.photo = vkGroupResp.getPhoto();
            vkGroup.photoMedium = vkGroupResp.getPhoto_medium();
            vkGroup.photoBig = vkGroupResp.getPhoto_big();
        } catch (IOException e) {
            throw new VkGroupNotFoundException("Exception when getting group with id " + vkGroup.vkId);
        }
    }

    public static class AddVkGroup {

        @Constraints.Required
        public String vkGroupId;

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
    }

    public Result removeVkGroup() {
        String vkGroupId = request().getQueryString("vkGroupId");
        VkGroup vkGroup = VkGroup.findBy(vkGroupId, request().username());

        if (vkGroup == null) {
            return badRequest("Group " + vkGroupId + " not found");
        }

        for (User user : new HashSet<>(vkGroup.users)) {
            if (user.email.equals(request().username())) {
                vkGroup.users.remove(user);
            }
        }

        vkGroup.save();

        return ok("Удалена группа " + vkGroupId + "(" + vkGroup.name + ")");
    }
}
