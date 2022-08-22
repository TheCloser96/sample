/**
 *  Account - Repository
 */

package com.board.sample.repository;

import com.board.sample.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByMail (String mail);   //사용자의 메일주소를 기준으로 데이터를 찾아옴
}
