package vk.group;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sbt-nikiforov-mo on 05.09.16.
 */
public class VkGroupUtils {

    private static final Logger LOG = LoggerFactory.getLogger(VkGroupUtils.class);
    public static final String VK_GROUPS = "https://api.vk.com/method/groups.getById?group_id=%s&fields=members_count";
    public static final String VK_GROUPS_MEMBERS = "https://api.vk.com/method/groups.getMembers?group_id=%s&offset=%s";

    private static String getVkGroupsQuery(String groupId) {
        return String.format(VK_GROUPS, groupId);
    }

    public static VkGroupResponse getVkGroupFromVk(String vkGroupId) throws IOException {
        String url = getVkGroupsQuery(vkGroupId);
        HttpGet request = new HttpGet(url);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(request);

        try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            StringBuffer resultStrBuf = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                resultStrBuf.append(line);
            }

            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(resultStrBuf.toString()).getAsJsonObject();

            //TODO handle
            if (obj.get("response") == null) return null;

            JsonArray jsonArray = obj.getAsJsonArray("response");
            if (jsonArray != null && jsonArray.get(0) != null) {
                String responseEl = jsonArray.get(0).toString();
                return VkGroupResponse.fromJson(responseEl);
            }
        }
        return null;
    }

    private static String getVkGroupMembersQuery(String groupId, int offset) {
        return String.format(VK_GROUPS_MEMBERS, groupId, offset);
    }

    public static VkGroupMembersResponse getVkGroupMembersFromVk(String vkGroupId) throws IOException {
        StringBuilder firstResponse = getVkGrMembersJson(vkGroupId, 0);
        VkGroupMembersResponse result = parseResponse(firstResponse.toString());

        int count = result.count;

        List<StringBuilder> allResponsesWithoutFirst = new ArrayList<>();
        for (int i = 1000; i < count; i += 1000) {
            StringBuilder vkGrMembersJson = getVkGrMembersJson(vkGroupId, i);
            allResponsesWithoutFirst.add(vkGrMembersJson);
        }

        for (StringBuilder sb : allResponsesWithoutFirst) {
            VkGroupMembersResponse resp = parseResponse(sb.toString());
            if (resp != null) {
                if (result.users != null) {
                    result.users.addAll(resp.users);
                }
            } else {
                LOG.error("error to parse response: [{}], vkGroupId: {}", resp, vkGroupId);
            }
        }

        return result;
    }

    private static VkGroupMembersResponse parseResponse(String jsonResponse) {
        JsonParser parser = new JsonParser();
        JsonObject obj = parser.parse(jsonResponse).getAsJsonObject();

        if (obj.get("response") == null) return null;

        JsonObject jsonObj = obj.getAsJsonObject("response");
        if (jsonObj != null) {
            return VkGroupMembersResponse.fromJson(jsonObj.toString());
        }

        return null;
    }

    private static StringBuilder getVkGrMembersJson(String vkGroupId, int offset) throws IOException {
        String url = getVkGroupMembersQuery(vkGroupId, offset);
        HttpGet request = new HttpGet(url);
        HttpClient client = HttpClientBuilder.create().build();

        long start = System.currentTimeMillis();
        HttpResponse response = client.execute(request);
        LOG.info("client.execute() get response: {}ms", System.currentTimeMillis() - start);

        try (BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            StringBuilder jsonResponse = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                jsonResponse.append(line);
            }
            return jsonResponse;
        }
    }
}
