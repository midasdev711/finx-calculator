package com.finx.persistence;

import com.finx.domain.PPSAFee;
import com.finx.domain.enums.Province;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PpsaFeesRepository extends CrudRepository<PPSAFee, Long> {

    @Query("""
            SELECT ppsa FROM PPSAFee ppsa
            WHERE ppsa.financingTerm = :term
            AND ppsa.province = :province
            """)
    Optional<PPSAFee> findPpsaFeeForTermAndProvince(@Param("term") int term, @Param("province") Province province);
}
