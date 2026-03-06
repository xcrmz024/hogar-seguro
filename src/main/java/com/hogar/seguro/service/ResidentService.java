package com.hogar.seguro.service;

import com.hogar.seguro.dto.ResidentDto;
import com.hogar.seguro.exception.ResourceNotFoundException;
import com.hogar.seguro.model.Resident;
import com.hogar.seguro.repository.ResidentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ResidentService {
    private final ResidentRepository residentRepository;

    public ResidentService(ResidentRepository residentRepository){
        this.residentRepository = residentRepository;
    }

//-------Mapping Logic:-----------

    //1. DTO -> Entity
    public Resident toEntity(ResidentDto dto) {
        Resident resident = new Resident();
        resident.setName(dto.getName());
        resident.setSpecies(dto.getSpecies());
        resident.setStory(dto.getStory());
        resident.setPhotoUrl(dto.getPhotoUrl());
        resident.setAvailable(dto.getAvailable());
        resident.setHelpType(dto.getHelpType());
        return resident;
    }

    //2. Entity -> DTO
    public ResidentDto toDto(Resident resident) {
        ResidentDto dto = new ResidentDto();
        dto.setId(resident.getId());
        dto.setName(resident.getName());
        dto.setSpecies(resident.getSpecies());
        dto.setStory(resident.getStory());
        dto.setPhotoUrl(resident.getPhotoUrl());
        dto.setAvailable(resident.isAvailable());
        dto.setHelpType(resident.getHelpType());
        return dto;
    }


//-----Resident cases (actions)---------

    //1. READ - Get All Residents
    public List<ResidentDto> getAll() {
        List<Resident> residents = residentRepository.findByActiveTrue();
        List<ResidentDto> dtoList = new ArrayList<>();

        for (Resident r : residents) {
            ResidentDto dto = toDto(r);
            dtoList.add(dto);
        }

        return dtoList;
    }


    //2. READ
    public ResidentDto getResidentDtoById(Long id) {
       Resident resident = residentRepository.findByIdAndActiveTrue(id).orElseThrow(()
               -> new ResourceNotFoundException("Resident with id " + id + " not found."));
       return toDto(resident);
    }


    //3. CREATE / UPDATE resident:
    @Transactional
    public void saveResident(ResidentDto residentDto) {
        Resident resident;
            //create:
        if (residentDto.getId() == null) {
            resident = new Resident();
        } else {
            //update.
            resident = residentRepository.findByIdAndActiveTrue(residentDto.getId()).orElseThrow(()
                    -> new ResourceNotFoundException("Resident with id " + residentDto.getId() + " not found."));
        }

        //manual mapping (toEntity):
        resident.setName(residentDto.getName());
        resident.setSpecies(residentDto.getSpecies());
        resident.setStory(residentDto.getStory());
        resident.setPhotoUrl(residentDto.getPhotoUrl());
        resident.setAvailable(residentDto.getAvailable());
        resident.setHelpType(residentDto.getHelpType());

        residentRepository.save(resident);
    }


    //4. DELETE (soft delete):
    @Transactional
    public void deleteResidentById(Long id) {
        Resident resident = residentRepository.findByIdAndActiveTrue(id).orElseThrow(()
                -> new ResourceNotFoundException("Resident with id " + id + " not found."));
        //soft delete
        resident.setActive(false);
        residentRepository.save(resident);
    }


}




