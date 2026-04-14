package com.project3.DataTypes;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Patient {

    @Id
    @GeneratedValue
    private Integer id;

    private String fullName;

    private Date dateOfBirth;

    private String note;

    public Integer getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
