package dev.m2t.unlucky.repository;

import dev.m2t.unlucky.model.BetSlip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BetSlipPagingRepository extends PagingAndSortingRepository<BetSlip, String> {
    Page<BetSlip> findAllByIsPublic(boolean isPublic, Pageable pageable);

    Page<BetSlip> findAllByUsername(String username, Pageable pageable);
}
