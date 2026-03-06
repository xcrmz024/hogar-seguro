package com.hogar.seguro.repository;

import com.hogar.seguro.model.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    @Query("SELECT SUM(d.amount) FROM Donation d")
    BigDecimal sumAllDonations();

}




