package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoggerRepository extends JpaRepository<Logger, Long> {




}
