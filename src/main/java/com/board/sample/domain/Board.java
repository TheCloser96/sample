/**
 *  Board - Entity
 *  게시물과 관련한 정보를 다루는 Entity
 */

package com.board.sample.domain;

import com.board.sample.domain.status.BoardRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue
    @Column(name = "board_id")    //데이터베이스 저장시 column명 지정
    private Long id;    //디폴트 기본키 생성

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")    //연관관계 주인
    private Account account;    //해당 게시물을 작성한 사용자

    private LocalDateTime firstRecordTime;  //처음 기록한 시간

    private LocalDateTime modifyRecordTime; //수정한 시간
    
    private String title;   //해당 게시물의 제목
    
    private String fileLocation;    //게시물 파일의 경로

    @Enumerated(EnumType.STRING)
    private BoardRole boardRole;    //게시물 공개 여부


    @OneToMany(mappedBy = "board")  //연관관계 서브
    private List<Comment> commentList = new ArrayList<>();  //해당 게시물에 작성한 댓글 목록들




    public void modifyBoardInfo(String title, LocalDateTime modifyRecordTime) {
        this.title = title;
        this.modifyRecordTime = modifyRecordTime;
    }   //기존의 게시물을 수정할 경우 사용되는 메서드


    public void softDeleteBoard() {
        this.boardRole = BoardRole.NO;
    }   //논리적 삭제를 진행하는 메서드





    private Board(Account account, LocalDateTime firstRecordTime, LocalDateTime modifyRecordTime, String title, String fileLocation, BoardRole boardRole) {
        this.account = account;
        this.firstRecordTime = firstRecordTime;
        this.modifyRecordTime = modifyRecordTime;
        this.title = title;
        this.fileLocation = fileLocation;
        this.boardRole = boardRole;
    }   //생성메서드를 위한 생성자



    //생성 메서드 목록


    public static Board createBoard(Account account, LocalDateTime firstRecordTime, LocalDateTime modifyRecordTime, String title, String fileLocation, BoardRole boardRole) {
        return new Board(account, firstRecordTime, modifyRecordTime, title, fileLocation, boardRole);
    }   //생성메서드 (게시물 생성용)

}
