package vk.group;

import com.google.gson.Gson;

import java.util.Set;

/**
 * Created by Zigzag on 11.09.2016.
 */
public class VkGroupMembersResponse {

    public Integer count;
    public Set<Long> users;

    public static VkGroupMembersResponse fromJson(String json) {
        Gson g = new Gson();
        VkGroupMembersResponse result = g.fromJson(json, VkGroupMembersResponse.class);
        return result;
    }

    @Override
    public String toString() {
        return "VkGroupMembersResponse{" +
                "count=" + count +
                ", users=" + users +
                '}';
    }
}
