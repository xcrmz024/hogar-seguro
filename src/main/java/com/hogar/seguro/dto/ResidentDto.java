package com.hogar.seguro.dto;

import com.hogar.seguro.model.enums.HelpType;
import jakarta.validation.constraints.*;

public class ResidentDto {

    public ResidentDto() {}

    private Long id;

    @NotBlank(message = "El nombre del habitante es obligatorio")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 50)
    private String species;

    @NotBlank(message = "La historia es obligatoria")
    @Size(max = 320)
    private String story;

    @NotBlank(message = "La URL de la foto es obligatoria")
    @Size(max = 500)
    private String photoUrl;

    @NotNull
    private Boolean available;

    @NotNull(message = "El tipo de ayuda requerida es obligatorio")
    private HelpType helpType;


//---Getters & Setters:
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getStory() { return story; }
    public void setStory(String story) { this.story = story; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public HelpType getHelpType() { return helpType; }
    public void setHelpType(HelpType helpType) { this.helpType = helpType; }


    @Override public String toString() {
        return "Resident{name='" + name + "', species='" + species + "', available="
                + available + ", helpType=" + helpType + "}";
    }


}
