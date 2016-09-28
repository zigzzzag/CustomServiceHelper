package vk.group;

import com.google.gson.Gson;

/**
 * Created by sbt-nikiforov-mo on 05.09.16.
 */
public class VkGroupResponse {

    public long gid;
    public String name;
    public String screen_name;
    public Integer is_closed;
    public String type;
    public String photo;
    public String photo_medium;
    public String photo_big;
    public int members_count;

    public VkGroupResponse() {
    }

    public static VkGroupResponse fromJson(String json) {
        Gson g = new Gson();
        VkGroupResponse result = g.fromJson(json, VkGroupResponse.class);
        return result;
    }

    @Override
    public String toString() {
        return "VkGroupResponse{" +
                "gid=" + gid +
                ", name='" + name + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", is_closed=" + is_closed +
                ", type='" + type + '\'' +
                ", photo='" + photo + '\'' +
                ", photo_medium='" + photo_medium + '\'' +
                ", photo_big='" + photo_big + '\'' +
                ", members_count=" + members_count +
                '}';
    }
}
