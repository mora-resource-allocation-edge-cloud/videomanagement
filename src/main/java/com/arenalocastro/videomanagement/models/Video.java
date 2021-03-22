package com.arenalocastro.videomanagement.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "video")
public class Video {
    @Id
    private ObjectId _id;
    private String name;
    private String author;
    private VideoStatus status;
    private ObjectId user;

    @JsonCreator
    public Video(String name, String author){
        this.name = name;
        this.author = author;
    }

    @JsonGetter("_id")
    public String get_id_string(){
        return _id.toHexString();
    }

    @JsonGetter("user")
    public String get_user_string(){
        return user.toHexString();
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public ObjectId getUser() {
        return user;
    }

    public void setUser(ObjectId user) {
        this.user = user;
    }

    public VideoStatus getStatus() {
        return status;
    }

    public void setStatus(VideoStatus status) {
        this.status = status;
    }
}
