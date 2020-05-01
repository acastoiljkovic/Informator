package com.informator.data;

public class StoredData {

    private static  StoredData _instance = null;

    public UserWithPicture user;

    private StoredData(){

    }

    public static StoredData getInstance(){
        if(_instance == null)
            _instance = new StoredData();

        return _instance;
    }

    public UserWithPicture getUser() {
        return user;
    }

    public void setUser(UserWithPicture user) {
        this.user = user;
    }
}
