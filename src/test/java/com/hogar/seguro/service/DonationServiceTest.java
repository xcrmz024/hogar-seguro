package com.hogar.seguro.service;

import com.hogar.seguro.dto.DonationDto;
import com.hogar.seguro.model.Donation;
import com.hogar.seguro.repository.DonationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("DonationService Tests")
public class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonationService donationService;


// ========================================================================
// 1. Mapping tests - toEntity() & toDto()
// ========================================================================

    //1.1 toEntity()
    @Test
    @DisplayName("Should correctly convert from DTO to Entity")
    void shouldConvertDtoToEntity() {
        //arrange
        DonationDto dto = new DonationDto();
        dto.setName("Juan Perez");
        dto.setEmail("juan@example.com");
        dto.setAmount(new BigDecimal("50.00"));
        dto.setMessage("¡Sigan haciendo un excelente trabajo!");

        //act
        Donation entity = donationService.toEntity(dto);

        //assert
        assertAll("Verification of mapped entity fields",
                () -> assertNotNull(entity, "The generated entity should not be null"),
                () -> assertEquals(dto.getName(), entity.getName(), "Names do not match"),
                () -> assertEquals(dto.getEmail(), entity.getEmail(), "Emails do not match"),
                () -> assertEquals(dto.getAmount(), entity.getAmount(), "Amounts do not match"),
                () -> assertEquals(dto.getMessage(), entity.getMessage(), "Messages do not match"),
                () -> assertNotNull(entity.getDate(), "The system should generate a date automatically")
        );
    }

    //1.2 toDto():
    @Test
    @DisplayName("Should correctly convert from Entity to DTO")
    void shouldConvertEntityToDto() {
        //arrange
        Donation donation = new Donation();
        donation.setName("Juana María");
        donation.setEmail("juana@example.com");
        donation.setAmount(new BigDecimal("100.00"));
        donation.setMessage("Love from Canada");
        donation.setDate(LocalDateTime.now());

        //act
        DonationDto dto = donationService.toDto(donation);

        //assert
        assertAll("Verification of mapped DTO fields",
                () -> assertNotNull(dto, "The generated DTO should not be null"),
                () -> assertEquals(donation.getName(), dto.getName(), "Names do not match"),
                () -> assertEquals(donation.getEmail(), dto.getEmail(), "Emails do not match"),
                () -> assertEquals(donation.getAmount(), dto.getAmount(), "Amounts do not match"),
                () -> assertEquals(donation.getMessage(), dto.getMessage(), "Messages do not match"),
                () -> assertEquals(donation.getDate(), dto.getDate(), "Dates do not match")
        );

    }

// ========================================================================
// 1. CRUD Tests - (create) - saveDonation()
// ========================================================================

    @Test
    @DisplayName("Should save a donation by calling the repository")
    void shouldSaveDonationCorrectly() {
        //arrange
        DonationDto dto = new DonationDto();
        dto.setName("Alice");
        dto.setEmail("alice@example.com");
        dto.setAmount(new BigDecimal("25.00"));

        //act:
        donationService.saveDonation(dto);

        //assert & verify
        verify(donationRepository, times(1)).save(any(Donation.class));

    }


// ========================================================================
// 2.  Business Logic Tests - getTotalDonations()
// ========================================================================

    @Test
    @DisplayName("Should return the sum of all donations when data exists")
    void shouldReturnTotalDonationsWhenDataExists() {
        BigDecimal expectedTotal = new BigDecimal("1500.50");

        when(donationRepository.sumAllDonations()).thenReturn(expectedTotal);

        //act
        BigDecimal actualTotal = donationService.getTotalDonations();

        //assert
        assertEquals(expectedTotal, actualTotal, "The total sum does not match");

        verify(donationRepository).sumAllDonations();

    }


    @Test
    @DisplayName("Should return ZERO when no donations are found (Repository returns null)")
    void shouldReturnZeroWhenRepositoryReturnsNull() {
        //Arrange
        when(donationRepository.sumAllDonations()).thenReturn(null);

        //Act
        BigDecimal total = donationService.getTotalDonations();

        //Assert
        assertEquals(BigDecimal.ZERO, total, "Should return BigDecimal.ZERO instead of null");
    }


// ========================================================================
// 3. CRUD Tests - (read) - getAll()
// ========================================================================

    @Test
    @DisplayName("Should return all donations ordered by date field")
    void shouldReturnAllDonationsOrdered() {
        //Arrange
        Donation d1 = new Donation();
        d1.setName("Donor 1");
        d1.setAmount(new BigDecimal("10.00"));
        d1.setDate(LocalDateTime.now().minusDays(1));

        Donation d2 = new Donation();
        d2.setName("Donor 2");
        d2.setAmount(new BigDecimal("20.00"));
        d2.setDate(LocalDateTime.now());

        List<Donation> donationList = List.of(d2, d1);

        when(donationRepository.findAll(any(Sort.class))).thenReturn(donationList);

        //Act
        List<DonationDto> dtoList = donationService.getAll();

        //Assert
        assertAll("Verification of list",
                () -> assertEquals(2, dtoList.size(), "List size should be 2"),

                () -> assertEquals("Donor 2", dtoList.get(0).getName(), "First element should be the most recent"),
                () -> verify(donationRepository).findAll(any(Sort.class))
        );

    }

}
