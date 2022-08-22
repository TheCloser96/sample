/**
 *  Account - Controller
 */

package com.board.sample.controller;

import com.board.sample.constant.SessionConst;
import com.board.sample.controller.dto.SigninDto;
import com.board.sample.controller.dto.SignupDto;
import com.board.sample.controller.dto.UserBoardDto;
import com.board.sample.controller.dto.UserCommentDto;
import com.board.sample.domain.Account;
import com.board.sample.domain.Board;
import com.board.sample.domain.Comment;
import com.board.sample.domain.status.AccountRole;
import com.board.sample.domain.status.BoardRole;
import com.board.sample.service.AccountService;
import com.board.sample.service.BoardService;
import com.board.sample.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final BoardService boardService;
    private final CommentService commentService;


    @GetMapping("/createaccount")
    public String signup(Model model) {
        model.addAttribute("signupDto", new SignupDto());
        return "account/signup";
    }   //회원가입 창으로 이동하는 메서드

    
    @PostMapping("/signupProcess")
    public String signupProcess(SignupDto dto, Model model) {
        
        if (!dto.getFirstPass().equals(dto.getSecondPass())) {
            model.addAttribute("msg", "비밀번호가 일치하지 않습니다");
            return "alert";
        }   //사용자가 입력한 2개의 비밀번호가 일치 않을 경우 특정페이지로 이동

        try {
            Account createAccount = Account.createAccount(dto.getMail(), dto.getFirstPass(), dto.getNick(), AccountRole.USER);
            accountService.save(createAccount);
            return "redirect:/main";
        } catch (Exception e) {
            model.addAttribute("msg", "이미 존재하는 메일 주소입니다");
            model.addAttribute("url", "/createaccount");
            return "alert";
        }   //사용자가 입력한 계정을 저장한다. 만약 입력한 메일주소가 이미 존재하는 경우 특정페이지로 이동

    }   //사용자가 회원가입란에 입력한 정보들이 해당 조건에 만족할 경우 저장하는 메서드


    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("signinDto", new SigninDto());
        return "account/signin";
    }   //로그인 창으로 이동하는 메서드

    
    @PostMapping("/loginProcess")
    public String loginProcess(HttpServletRequest request, SigninDto dto, Model model) {
        String inputMail = dto.getMail();   //사용자가 입력한 메일
        String inputPass = dto.getPass();   //사용자가 입력한 비밀번호
        Account account = accountService.findByMail(inputMail); //사용자가 입력한 메일을 기준으로 해당 계정 객체를 조회
        
        if (account == null || !(inputPass.equals(account.getPassword()))) {
            model.addAttribute("msg", "존재하지 않거나 올바르지 않은 입력입니다");
            return "alert";
        }   //해당하는 계정 객체가 존재하지 않거나 비밀번호가 다를 경우 해당 페이지로 이동


        //로그인 과정을 통과한 사용자만 세션을 생성
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, account);

        return "redirect:/main";
    }   //로그인 과정에서 입력한 값들이 맞는지 확인하고 맞을 경우 세션을 생성하고 특정페이지로 이동


    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);    //로그인된 사용자의 세션을 가져옴
        session.invalidate();   //해당 세션을 제거

        return "redirect:/main";
    }   //로그인한 사용자의 세션을 제거 후 메인페이지로 이동


    @GetMapping("/usercustom")
    public String userCustom(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Account account,
            @PageableDefault(page = 0, size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        
        Long accountId = account.getId();   //해당 사용자의 고유 id값(세션으로 부터 가져옴)
        Account findAccount = accountService.findById(accountId).orElseThrow(null); //id값으로 부터 가져온 해당 사용자 객체

        String nick = findAccount.getNick();    //해당 사용자의 닉네임

        model.addAttribute("nick", nick);


        
        //해당 사용자가 작성한 게시물 리스트(최신순 n개만)

        Page<Board> boardFromAccountId = boardService.findBoardFromAccountId(accountId, BoardRole.YES, pageable);   //페이징 처리된 게시물
        List<Board> boardList = boardFromAccountId.getContent();    //페이징 처리된 게시물 리스트 추출
        int boardIndexNumber = boardFromAccountId.getNumber();  //현재 게시물의 페이지 번호(0부터 시작)
        int boardTotalPages = boardFromAccountId.getTotalPages();   //게시물의 전체 페이지의 개수(1부터 시작)

        List<Integer> boardIndexBlock = new ArrayList<>();  //게시물 페이지 블록들의 번호들을 기입
        if (boardTotalPages == 1) {
            boardIndexBlock.add(1);
        } else if (boardTotalPages == 2) {
            boardIndexBlock.add(1);
            boardIndexBlock.add(2);
        } else {
            boardIndexBlock.add(1);
            boardIndexBlock.add(2);
            boardIndexBlock.add(3);
        }

        UserBoardDto userBoardDto = new UserBoardDto(boardList, boardIndexNumber, boardTotalPages, boardIndexBlock);
        model.addAttribute("userBoardDto", userBoardDto);



        //해당 사용자가 작성한 댓글 리스트(최신순 n개만)

        Page<Comment> getpagingComment = commentService.findCommentFromAccountId(accountId, pageable);   //페이징 처리된 댓글
        List<Comment> commentList = getpagingComment.getContent();  //페이징 처리된 댓글 리스트 추출
        int commentIndexNumber = getpagingComment.getNumber();  //현재 댓글 페이지 번호(0부터 시작)
        boolean commentHasNext = getpagingComment.hasNext();    //다음 페이지가 있는지 확인

        UserCommentDto userCommentDto = new UserCommentDto(commentList, commentIndexNumber, commentHasNext);
        model.addAttribute("userCommentDto", userCommentDto);

        return "account/user_custom_view";

    }   //사용자 자신의 프로필 변경(닉네임 게시물) 및 자신이 작성한 댓글들을 리스트로 조회가 가능한 페이지로 이동


    @PostMapping("/updateNick")
    public String updateNick(
            Model model,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Account account,
            @RequestParam("getNick") String getNick) {

        //해당 사용자의 객체를 가져오는 과정
        
        Long accountId = account.getId();
        Account getAccount = accountService.findById(accountId).orElseThrow(null);
        
        account.changeNick(getNick);    //세션에 존재하는 닉네임도 바꿔준다
        
        accountService.updateNick(getAccount, getNick); //해당 사용자의 닉네임을 변경

        model.addAttribute("nick", getNick);    //바뀐 닉네임을 해당 뷰에 전달

        return "account/user_custom_view :: #inputNick";
    }   //해당 사용자의 닉네임 변경 작업과 변경된 닉네임값을 해당 뷰로 전달(새로고침 X)


    @PostMapping("/updateUserBoardList")
    public String updateUserBoardList(
            Model model,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Account account,
            @RequestParam("boardListSize") String boardListSize,
            @RequestParam("clickedNum") String clickedNum) {

        //해당 사용자의 객체를 가져오는 과정

        Long accountId = account.getId();
        Account getAccount = accountService.findById(accountId).orElseThrow(null);


        //페이징 설정 과정

        int pageNumber = Integer.parseInt(clickedNum);  //사용자가 조회하려는 페이지 번호
        int size = Integer.parseInt(boardListSize); //게시판 리스트의 크기
        PageRequest pageRequest = PageRequest.of(pageNumber-1, 3, Sort.by(Sort.Direction.DESC, "id")); //페이징 설정된 변수


        //해당 뷰로 보낼 게시판 관련 정보 추출 과정

        Page<Board> boardFromAccountId = boardService.findBoardFromAccountId(accountId, BoardRole.YES, pageRequest);
        List<Board> boardList = boardFromAccountId.getContent();
        int boardIndexNumber = boardFromAccountId.getNumber();
        int boardTotalPages = boardFromAccountId.getTotalPages();
        List<Integer> boardIndexBlock = getBoardListBlock(boardFromAccountId);


        model.addAttribute("userBoardDto", new UserBoardDto(boardList, boardIndexNumber, boardTotalPages, boardIndexBlock));

        return "account/user_custom_view :: #aboutBoard";

    }   //해당 사용자가 작성했던 게시물들을 조회(새로고침 X)


    private List<Integer> getBoardListBlock(Page<Board> pagedBoard) {

        List<Integer> boardIndexBlock = new ArrayList<>();

        int totalPages = pagedBoard.getTotalPages();    //게시물 전체 페이지(1부터 카운팅)
        int number = pagedBoard.getNumber();    //현재 페이지
        int maxNumber = totalPages-1;   //최대 페이지

        if (totalPages <= 2) {
            //최대 페이지의 개수가 2개 안팎일 경우
            for (int i = 0; i < totalPages; i++) {
                boardIndexBlock.add(i+1);
            }
        } else {
            //최대 페이지 개수가 3개 이상일 경우

            if (maxNumber == number) {
                //조회하는 게시판 페이지가 최대페이지 와 같을 경우

                boardIndexBlock.add(number-1);
                boardIndexBlock.add(number);
                boardIndexBlock.add(number+1);
            } else if (number == 0){
                //조회하는 게시판 페이지가 최소일 경우
                
                boardIndexBlock.add(1);
                boardIndexBlock.add(2);
                boardIndexBlock.add(3);
            } else {
                boardIndexBlock.add(number);
                boardIndexBlock.add(number+1);
                boardIndexBlock.add(number+2);
            }
        }

        return boardIndexBlock;

    }   //게시판 조회 블록의 개수와 해당 블록의 번호를 채우기


    @PostMapping("/updateCommentPaging")
    public String updateCommentPaging(
            Model model,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Account account,
            @RequestParam Map<String, Object> userCommentInfoMap) {

        //이전 혹은 다음페이지의 댓글 리스트를 보기위한 변수 모음
        
        Long accountId = account.getId();   //로그인 한 사용자의 고유 id값
        int commentPageNumber = Integer.parseInt((String) userCommentInfoMap.get("commentPageNumber")); //현재 댓글 페이지
        boolean checkNext = Boolean.parseBoolean((String) userCommentInfoMap.get("checkNext")); //현재 댓글 페이지 기준으로 다음 페이지 존재 여부
        boolean toPage = Boolean.parseBoolean((String) userCommentInfoMap.get("toPage"));   //사용자가 클릭한 블록이 이전 혹은 다음 상태


        //

        int newPage;

        if (toPage) {
            newPage = commentPageNumber+1;
        } else {
            newPage = commentPageNumber-1;
        }
        System.out.println("==========중간 점검1==========");
        System.out.println("newPage: "+newPage);
        System.out.println("==========중간 점검1==========");


        PageRequest pageRequest = PageRequest.of(newPage, 3, Sort.by(Sort.Direction.DESC, "id")); //사용자가 조회하려는 댓글의 영역의 페이지를 지정

        Page<Comment> commentFromAccountId = commentService.findCommentFromAccountId(accountId, pageRequest);   //해당 사용자의 id값과 새로 페이징된 결과물
        List<Comment> content = commentFromAccountId.getContent();
        int number = commentFromAccountId.getNumber();
        boolean hasNext = commentFromAccountId.hasNext();

        System.out.println("==========중간 점검2==========");
        for (Comment c: content) {
            System.out.println(c.getId());
            System.out.println(c.getAccount().getNick());
            System.out.println(c.getComments());
        }
        System.out.println("number: "+number);
        System.out.println("hasNext: "+hasNext);
        System.out.println("==========중간 점검2==========");

        UserCommentDto userCommentDto = new UserCommentDto(content, number, hasNext);

        model.addAttribute("userCommentDto", userCommentDto);

        return "account/user_custom_view :: #aboutComment";
    }   //해당 사용자가 작성했던 댓글들을 조회(새로고침 X)
}
