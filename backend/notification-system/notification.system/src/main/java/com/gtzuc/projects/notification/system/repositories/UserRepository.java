package com.gtzuc.projects.notification.system.repositories;

import com.gtzuc.projects.notification.system.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
