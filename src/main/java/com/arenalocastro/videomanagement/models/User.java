package com.arenalocastro.videomanagement.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

@Document
public class User {
    @Id
    private ObjectId _id;
    @Indexed(unique=true)
    @NotNull(message = "Username is required")
    private String username;
    @NotNull(message = "Password is required")
    private String password;
    private List<String> roles;

    @JsonGetter("_id")
    public String get_id_string(){
        return _id.toHexString();
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
