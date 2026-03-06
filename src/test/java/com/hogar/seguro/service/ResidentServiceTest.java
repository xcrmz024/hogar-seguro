package com.hogar.seguro.service;


import com.hogar.seguro.dto.ResidentDto;
import com.hogar.seguro.exception.ResourceNotFoundException;
import com.hogar.seguro.model.Resident;
import com.hogar.seguro.model.enums.HelpType;
import com.hogar.seguro.repository.ResidentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("ResidentService Tests")
public class ResidentServiceTest {

    @Mock
    private ResidentRepository residentRepository;

    @InjectMocks
    private ResidentService residentService;


// ========================================================================
// 1. Mapping tests - toEntity() & toDto()
// ========================================================================

    // 1.1 toEntity():
    @Test
    @DisplayName("Should correctly convert from DTO to Entity")
    void shouldConvertDtoToEntity() {
        //arrange
        ResidentDto dto = new ResidentDto();
        dto.setName("Rex");
        dto.setSpecies("Perro");
        dto.setStory("Un perro muy valiente.");
        dto.setPhotoUrl("imagenes/rex.jpg");
        dto.setAvailable(false);
        dto.setHelpType(HelpType.AMADRINAR);

        //act
        Resident entity = residentService.toEntity(dto);

        //asssert
        assertAll("Verification of mapped entity fields",
                () -> assertNotNull(entity, "The generated entity should not be null"),
                () -> assertEquals(dto.getName(), entity.getName(), "Names do not match"),
                () -> assertEquals(dto.getSpecies(), entity.getSpecies(), "Species values do not match"),
                () -> assertEquals(dto.getStory(), entity.getStory(), "Stories do not match"),
                () -> assertEquals(dto.getPhotoUrl(), entity.getPhotoUrl(), "Photo URLs do not match"),
                () -> assertFalse(entity.isAvailable(), "Availability status should be false"),
                () -> assertEquals(HelpType.AMADRINAR, entity.getHelpType(), "Help types do not match")
        );
    }


    // 1.2 toDto():
    @Test
    @DisplayName("Should correctly convert from Entity to DTO")
    void shouldConvertEntityToDto() {
        //arrange
        Resident resident = new Resident();
        resident.setName("Luna");
        resident.setSpecies("Gato");
        resident.setStory("Rescatada de un tejado.");
        resident.setPhotoUrl("imagenes/luna.jpg");
        resident.setAvailable(true);
        resident.setHelpType(HelpType.ADOPTAR);


        //act
        ResidentDto dto = residentService.toDto(resident);

        //assert
        assertAll("Verification of mapped DTO fields",
                () -> assertNotNull(dto, "The generated DTO should not be null"),
                () -> assertEquals(resident.getName(), dto.getName(), "Names do not match"),
                () -> assertEquals(resident.getSpecies(), dto.getSpecies(), "Species values do not match"),
                () -> assertEquals(resident.getStory(), dto.getStory(), "Stories do not match"),
                () -> assertEquals(resident.getPhotoUrl(), dto.getPhotoUrl(), "Photo URLs do not match"),
                () -> assertTrue(dto.getAvailable(), "Availability status should be true"),
                () -> assertEquals(HelpType.ADOPTAR, dto.getHelpType(), "Help types do not match")
        );

    }

// ========================================================================
// 2. CRUD Tests - (read) - getAll()
// ========================================================================

    @Test
    @DisplayName("Should return all active residents")
    void shouldReturnAllActiveResidents() {
        // Arrange
        Resident r1 = new Resident();
        r1.setName("Rex");
        r1.setSpecies("Perro");
        r1.setStory("Rescatado de un acantilado.");
        r1.setPhotoUrl("imagenes/rex.jpg");
        r1.setAvailable(true);
        r1.setHelpType(HelpType.ADOPTAR);

        Resident r2 = new Resident();
        r2.setName("Luna");
        r2.setSpecies("Oveja");
        r2.setStory("Rescatada de una granja.");
        r2.setPhotoUrl("imagenes/luna.jpg");
        r2.setAvailable(false);
        r2.setHelpType(HelpType.AMADRINAR);

        List<Resident> residents = List.of(r1, r2);

        when(residentRepository.findByActiveTrue()).thenReturn(residents);

        // Act
        List<ResidentDto> dtos = residentService.getAll();

        // Assert
        assertEquals(2, dtos.size(), "Should return two residents");
        assertEquals(r1.getName(), dtos.get(0).getName());
        assertEquals(r2.getName(), dtos.get(1).getName());
    }


// ========================================================================
// 3. CRUD Tests - (read) - getResidentDtoById() + exceptions
// ========================================================================

    //getResidentDtoById()
    @Test
    @DisplayName("Should return an active resident by ID")
    void shouldReturnResidentById() {
        //arrange:
        Long id = 1L;

        Resident resident = new Resident();
        resident.setName("Rex");
        resident.setSpecies("Perro");
        resident.setStory("Rescatado de una perrera.");
        resident.setPhotoUrl("imagenes/rex.jpg");
        resident.setAvailable(true);
        resident.setHelpType(HelpType.ADOPTAR);

        when(residentRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(resident));

        //act:
        ResidentDto dto = residentService.getResidentDtoById(id);

        //verify:
        assertNotNull(dto, "The returned DTO should not be null");
        verify(residentRepository).findByIdAndActiveTrue(id);

    }

    //exception:
    @Test
    @DisplayName("Should throw ResourceNotFoundException when the resident does not exist")
    void shouldThrowExceptionWhenResidentNotFound() {
        //arrange
        Long id = 99L;
        when(residentRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.empty());

        //act & assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            residentService.getResidentDtoById(id);
        }, "Expected ResourceNotFoundException to be thrown, but it was not");

        //extra assert:
        assertEquals("Resident with id " + id + " not found.", ex.getMessage(),  "Exception messages do not match");
    }



// ========================================================================
// 4. Tests CRUD - (create/update) - saveResident() + exception
// ========================================================================

    // saveResdient() - (create) -> id == null
    @Test
    @DisplayName("Should create and save a new resident when the ID is null")
    void shouldCreateNewResidentWhenIdIsNull() {
        //arrange
        ResidentDto dto = new ResidentDto();
        dto.setName("Bobby");
        dto.setSpecies("Perro");
        dto.setStory("Rescatado de la calle.");
        dto.setPhotoUrl("imagenes/bobby.jpg");
        dto.setAvailable(true);
        dto.setHelpType(HelpType.ADOPTAR);

        //act
        residentService.saveResident(dto);

        //assert & verify
        verify(residentRepository, times(1)).save(any(Resident.class));

    }



    //saveResident() - manual mapping dto -> entity test:
    @Test
    @DisplayName("Should correctly map fields when saving a new resident")
    void shouldMapFieldsCorrectlyWhenSaving() {
        //arrange
        ResidentDto dto = new ResidentDto();
        dto.setName("Milo");
        dto.setSpecies("Gato");
        dto.setStory("Rescatado de la avenida.");
        dto.setPhotoUrl("imagenes/Milo.jpg");
        dto.setAvailable(true);
        dto.setHelpType(HelpType.ADOPTAR);

        //act
        residentService.saveResident(dto);

        //assert with ArgumentCaptor:
        ArgumentCaptor<Resident> residentCaptor = ArgumentCaptor.forClass(Resident.class);
        verify(residentRepository).save(residentCaptor.capture());
        Resident capturedResident = residentCaptor.getValue();

        assertAll("Verification of mapped fields",
                () -> assertEquals(dto.getName(), capturedResident.getName(), "Names do not match"),
                () -> assertEquals(dto.getSpecies(), capturedResident.getSpecies(), "Species values do not match"),
                () -> assertEquals(dto.getStory(), capturedResident.getStory(), "Stories do not match"),
                () -> assertEquals(dto.getPhotoUrl(), capturedResident.getPhotoUrl(), "Photo URLs do not match"),
                () -> assertTrue(capturedResident.isAvailable(), "Availability status should be true"),
                () -> assertEquals(dto.getHelpType(), capturedResident.getHelpType(), "Help types do not match"),
                () -> assertTrue(capturedResident.getActive(), "Active status should be true") // default true
        );
    }


    // saveResdient() - (update) -> id != null
    @Test
    @DisplayName("Should update an existing resident when the ID is not null")
    void shouldUpdateExistingResidentWhenIdIsNotNull() {
        //arrange
        Long existingId = 1L;

        ResidentDto updateDto = new ResidentDto();
        updateDto.setId(existingId);
        updateDto.setName("Milo Editado");//changed field
        updateDto.setSpecies("Gato");
        updateDto.setStory("Rescatado de la avenida.");
        updateDto.setPhotoUrl("imagenes/Milo.jpg");
        updateDto.setAvailable(false);
        updateDto.setHelpType(HelpType.ADOPTAR);

        Resident existingResident = new Resident();
        existingResident.setName("Milo Original");

        when(residentRepository.findByIdAndActiveTrue(existingId)).thenReturn(Optional.of(existingResident));

        //act
        residentService.saveResident(updateDto);

        //assert
        assertEquals(updateDto.getName(), existingResident.getName(), "Names do not match");
        verify(residentRepository).save(existingResident);
    }


    // saveResdient() - (update) -> exception
    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating a non-existent resident")
    void shouldThrowExceptionWhenResidentNotFoundInUpdate() {
        //arrange:
        Long id = 99L;

        ResidentDto updateDto = new ResidentDto();
        updateDto.setId(id);
        updateDto.setName("No Existe");

        when(residentRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.empty());

        //act & assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            residentService.saveResident(updateDto);
        }, "Expected ResourceNotFoundException to be thrown, but it was not");

        //extra assert
        assertEquals("Resident with id " + updateDto.getId() + " not found.", ex.getMessage(), "Exception messages do not match");
    }



// ========================================================================
// 5. Tests CRUD - (soft delete) - deleteResidentById() + exception
// ========================================================================

    //deleteResidentById()
    @Test
    @DisplayName("Should perform a soft delete by setting active to false")
    void shouldPerformSoftDelete() {
        //arrange
        Long exisingId = 10L;

        Resident existingResident = new Resident();
        existingResident.setName("Rex");
        existingResident.setActive(true);

        when(residentRepository.findByIdAndActiveTrue(exisingId)).thenReturn(Optional.of(existingResident));

        //act
        residentService.deleteResidentById(exisingId);

        //assert & verify:
        assertFalse(existingResident.getActive(), "Active status should be false after the deletion");
        verify(residentRepository).save(existingResident);
    }


    //exception
    @Test
    @DisplayName("Should throw ResourceNotFoundException when trying to delete a non-existent resident")
    void shouldThrowWhenDeletingNonExistingResident() {
        Long id = 99L;
        when(residentRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.empty());

        //act and Assert:
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            residentService.deleteResidentById(id);
        }, "Expected ResourceNotFoundException to be thrown, but it was not");

        //extra assert:
        assertEquals("Resident with id " + id + " not found.", ex.getMessage(), "Exception messages do not match");

    }


}
