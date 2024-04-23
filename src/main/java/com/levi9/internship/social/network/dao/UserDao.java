package com.levi9.internship.social.network.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.levi9.internship.social.network.model.User;

public interface UserDao extends JpaRepository<User, String>
{

	Optional<User> findByUsername(String username);
}
