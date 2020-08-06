package com.sungwoo.board.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String writer;

    @Column(nullable = false)
    private String content;

    @Column
    private LocalDateTime createdDat;

    @Column
    @ColumnDefault("0")
    private Long views;

    @Column
    private String password;

    @Column
    private Long fileId;

    @Builder
    public Board(String title, String writer, String content, LocalDateTime createdDat, Long views, String password, Long fileId){
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.createdDat = createdDat;
        this.views = views;
        this.password = password;
        this.fileId = fileId;
    }

    @PrePersist
    public void prePersist(){
        this.views=this.views==null?0:this.views;
    }

}
