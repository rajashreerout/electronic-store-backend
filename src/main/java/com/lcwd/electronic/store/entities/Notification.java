package com.lcwd.electronic.store.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    
    @Column(length = 1000)
    private String message;
    
    private String type; // STOCK, ORDER, SYSTEM, etc.
    
    @Column(name = "is_read")
    private boolean read;
    
    private Date createdDate;
    
    @PrePersist
    public void prePersist() {
        this.createdDate = new Date();
    }
}