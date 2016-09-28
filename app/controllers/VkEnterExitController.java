package controllers;

import com.google.inject.Inject;
import java.util.List;
import models.User;
import models.vk.VkEnterExitHistory;
import models.vk.VkGroup;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import play.data.Form;
import play.data.FormFactory;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.mvc.Result;
import play.mvc.Security;
import views.html.dashboard.vkexenhistory;

import java.util.Date;

import static play.mvc.Http.Context.Implicit.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.ok;

/**
 * Created by sbt-nikiforov-mo on 19.09.16.
 */
@Security.Authenticated(Secured.class)
public class VkEnterExitController {

    @Inject
    FormFactory formFactory;

    public Result index() {
        return ok(vkexenhistory.render(
                User.findByEmail(request().username()),
                formFactory.form(Filter.class),
                null
        ));
    }

    public Result search() {
        Form<Filter> searchForm = formFactory.form(Filter.class).bindFromRequest();

        if (searchForm.hasErrors()) {
            return badRequest(vkexenhistory.render(
                    User.findByEmail(request().username()),
                    searchForm,
                    null));
        }

        Filter filter = searchForm.get();
        VkGroup vkGroup = VkGroup.findByVkId(filter.vkGroupId);

        DateTime searchFrom = filter.from != null ? new DateTime(filter.from) : null;
        DateTime searchTo = filter.to != null ? new DateTime(filter.to) : null;

        List<VkEnterExitHistory> histories = VkEnterExitHistory.find(searchFrom, searchTo, vkGroup, filter.status);

        return ok(vkexenhistory.render(
                User.findByEmail(request().username()),
                searchForm,
                histories
        ));
    }

    public static class Filter {

        public Date from;

        public Date to;

        @Constraints.Required
        public String vkGroupId;

        public String status;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {
            if (StringUtils.isBlank(vkGroupId)) {
                return "vk group is required";
            }

            return null;
        }
    }
}
