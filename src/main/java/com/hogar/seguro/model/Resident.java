package com.hogar.seguro.model;

import com.hogar.seguro.model.enums.HelpType;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Table(name= "residents")
public class Resident {

    public Resident() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resident_name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(nullable = false, length = 50)
    private String species;

    @Column(nullable = false, length = 320)
    private String story;

    @Column(name = "photo_url", nullable = false, length = 500)
    private String photoUrl;

    @Column(nullable = false)
    private boolean available;

    //ADOPTAR - AMADRINAR
    @Enumerated(EnumType.STRING)
    @Column(name = "help_type", nullable = false)
    private HelpType helpType;

    //Resident soft delete
    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean active = true;


//----- getters & setters:

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getStory() { return story; }
    public void setStory(String story) { this.story = story; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public HelpType getHelpType() { return helpType; }
    public void setHelpType(HelpType helpType) { this.helpType = helpType; }

    public boolean getActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

}
