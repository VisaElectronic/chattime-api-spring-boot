package com.chattime.chattime_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenerationTime;

import javax.annotation.processing.Generated;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String key;
    private String email;
    private String password;
    private String avatar;
    private String firstname;
    private String lastname;
    private String phone;
    private String dob;
    private String bio;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "updated_at")
    private Date updatedAt;

    public User() {
    }

    public User(
            String username,
            String firstname,
            String lastname,
            String email,
            String phone,
            String password
    ) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.key = java.util.UUID.randomUUID().toString();
    }
}
