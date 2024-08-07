package dev.m2t.unlucky.repository;

import dev.m2t.unlucky.model.GuessPaper;
import dev.m2t.unlucky.model.User;
import dev.m2t.unlucky.model.enums.GuessPaperStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuessPaperRepository extends JpaRepository<GuessPaper, Long> {
    List<GuessPaper> findAllByStatus(GuessPaperStatusEnum guessPaperStatusEnum);
}
