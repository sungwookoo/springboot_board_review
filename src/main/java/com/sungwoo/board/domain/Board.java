package com.sungwoo.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter

@Entity
@Table
public class Board implements Serializable {

    public Board() {
    }

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String title;

    @Column
    private String writer;

    @Column(updatable = false)
    private LocalDateTime createdDat;

    @Column
    private Long views;

    @Builder
    public Board(String title, String writer, LocalDateTime createdDat,Long views){
        this.title = title;
        this.writer = writer;
        this.createdDat = createdDat;
        this.views = views;
    }

}
