package com.levi9.internship.social.network.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.levi9.internship.social.network.model.Location;

public interface LocationDao extends JpaRepository<Location, Long>
{
}
