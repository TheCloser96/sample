/**
 *  Account - Entity
 *  사용자의 계정과 관련한 정보를 다루는 Entity
 */

package com.board.sample.domain;

import com.board.sample.domain.status.AccountRole;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    
    @Id @GeneratedValue
    @Column(name = "account_id")    //데이터베이스 저장시 column명 지정
    private Long id;    //디폴트 기본키 생성

    @NotNull
    @Column(nullable = false, unique=true)  //null값 방지, 유일한 값들만 허용하게 설정
    private String mail;    //사용자 로그인 메일(로그인시 아이디 역할)
    
    @NotNull
    @Column(nullable = false)   //null값 방지
    private String password;    //사용자 로그인 비밀번호(로그인시 비밀번호)

    @NotNull
    @Column(nullable = false)   //null값 방지
    private String nick;    //사용자의 별명

    @NotNull
    @Column(nullable = false)   //null값 방지
    @Enumerated(EnumType.STRING)
    private AccountRole accountRole;    //사용자의 권한

    
    


    
    
    private Account(String mail, String password, String nick, AccountRole accountRole) {
        this.mail = mail;
        this.password = password;
        this.nick = nick;
        this.accountRole = accountRole;
    }   //생성메서드를 위한 생성자


    
    public static Account createAccount(String mail, String password, String nick, AccountRole accountRole) {
        return new Account(mail, password, nick, accountRole);
    }   //생성메서드 (계정 생성용)





    
    
    
    
    
    public void changeNick(String nick) {
        this.nick = nick;
    }   //사용자 닉네임 변경시 사용됨
}
