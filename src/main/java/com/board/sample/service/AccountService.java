/**
 *  Account - Service
 */

package com.board.sample.service;

import com.board.sample.domain.Account;
import com.board.sample.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;


    @Transactional
    public Long save(Account account) {
        Account save = accountRepository.save(account);
        return save.getId();
    }


    public Account findByMail(String mail) {
        return accountRepository.findByMail(mail);
    }


    public Optional<Account> findById(Long AccountId) {
        return accountRepository.findById(AccountId);
    }


    @Transactional
    public void updateNick(Account account, String nick) {
        account.changeNick(nick);
    }
}
