package com.lcwd.electronic.store.repositories;

import com.lcwd.electronic.store.entities.SaveForLater;
import com.lcwd.electronic.store.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SaveForLaterRepository extends JpaRepository<SaveForLater, String> {
    List<SaveForLater> findByUser(User user);
}