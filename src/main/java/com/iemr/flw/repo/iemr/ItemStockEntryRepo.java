package com.iemr.flw.repo.iemr;

import com.iemr.flw.domain.iemr.ItemStockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ItemStockEntryRepo extends JpaRepository<ItemStockEntry, Long> {

    List<ItemStockEntry> findByFacilityIDAndItemIDAndDeletedFalseAndQuantityInHandGreaterThan(
            Integer facilityID, Integer itemID, Integer quantity);

    @Transactional
    @Modifying
    @Query("UPDATE ItemStockEntry e SET e.quantityInHand = e.quantityInHand - :qty WHERE e.itemStockEntryID = :id")
    Integer subtractStock(@Param("id") Long itemStockEntryID, @Param("qty") Integer qty);
}
