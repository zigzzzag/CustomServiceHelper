package vk.group;

import com.google.gson.Gson;

/**
 * Created by sbt-nikiforov-mo on 05.09.16.
 */
public class VkGroupResponse {

    private long gid;
    private String name;
    private String screen_name;
    private Integer is_closed;
    private String type;
    private String photo;
    private String photo_medium;
    private String photo_big;

    public VkGroupResponse() {
    }

    public static VkGroupResponse fromJson(String json) {
        Gson g = new Gson();
        VkGroupResponse result = g.fromJson(json, VkGroupResponse.class);
        return result;
    }

    @Override
    public String toString() {
        return "gid:" + gid + ", name:" + name + ", screen_name:" + screen_name + ", is_closed:" + is_closed
                + ", type:" + type + ", photo:" + photo + ", photo_medium:" + photo_medium + ", photo_big:" + photo_big;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public Integer getIs_closed() {
        return is_closed;
    }

    public void setIs_closed(Integer is_closed) {
        this.is_closed = is_closed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto_medium() {
        return photo_medium;
    }

    public void setPhoto_medium(String photo_medium) {
        this.photo_medium = photo_medium;
    }

    public String getPhoto_big() {
        return photo_big;
    }

    public void setPhoto_big(String photo_big) {
        this.photo_big = photo_big;
    }
}
