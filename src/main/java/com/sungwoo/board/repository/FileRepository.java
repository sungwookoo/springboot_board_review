package com.sungwoo.board.repository;

import com.sungwoo.board.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FileRepository extends JpaRepository<File, Long> {
}
