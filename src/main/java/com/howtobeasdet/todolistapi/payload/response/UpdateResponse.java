package com.howtobeasdet.todolistapi.payload.response;

import com.howtobeasdet.todolistapi.model.User;

public class UpdateResponse {

    private User data;

    private Boolean success;

    public UpdateResponse(User data, Boolean success) {
        this.data = data;
        this.success = success;
    }

    public User getUser() {
        return data;
    }

    public void setUser(User user) {
        this.data = user;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(String token) {
        this.success = success;
    }
}
