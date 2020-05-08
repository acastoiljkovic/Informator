package com.informator.data;

public class Post {
    private String username; //username korisnika ciji je post komentar ili poruka
    private String post; //sadrzaj posta komentara poruke

    public Post(){

    }

    public Post(String username, String post) {
        this.username = username;
        this.post = post;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}
