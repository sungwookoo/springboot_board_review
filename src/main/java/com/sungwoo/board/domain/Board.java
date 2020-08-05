package com.sungwoo.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor

@Getter
@Setter

@Entity
@Table
public class Board implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    private String title;

    @Column
    private String writer;

    @Column
    private String content;

    @Column
    private LocalDateTime createdDat;

    @Column
    private Long views;

    @Column
    private String password;

    @Builder
    public Board(String title, String writer, String content, LocalDateTime createdDat,Long views,String password){
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.createdDat = createdDat;
        this.views = views;
        this.password = password;
    }

}
