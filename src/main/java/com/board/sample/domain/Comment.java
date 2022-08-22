/**
 *  Comment - Entity
 *  댓글과 관련한 정보를 다루는 Entity
 */

package com.board.sample.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")    //데이터베이스 저장시 column명 지정
    private Long id;    //디폴트 기본키 생성

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")    //연관관계 주인
    private Account account;    //해당 댓글을 작성한 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")  //연관관계 주인
    private Board board;    //댓글 작성한 해당 게시물

    private LocalDateTime recordTime;   //댓글 작성 시간

    private String comments;    //댓글 내용








    private Comment(Account account, Board board, LocalDateTime recordTime, String comments) {
        this.account = account;
        this.board = board;
        this.recordTime = recordTime;
        this.comments = comments;
    }







    //생성 메서드 목록


    public static Comment createComment(Account account, Board board, LocalDateTime recordTime, String comments) {
        return new Comment(account, board, recordTime, comments);
    }
}
