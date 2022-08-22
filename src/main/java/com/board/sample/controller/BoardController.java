/**
 *  Board - Controller
 */

package com.board.sample.controller;

import com.board.sample.constant.SessionConst;
import com.board.sample.controller.dto.BeforeBoardInfo;
import com.board.sample.controller.dto.BoardInfoDto;
import com.board.sample.controller.dto.BoardViewDto;
import com.board.sample.controller.dto.CommentsDto;
import com.board.sample.domain.Account;
import com.board.sample.domain.Board;
import com.board.sample.domain.Comment;
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
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final AccountService accountService;
    private final CommentService commentService;



    @GetMapping("/main")
    public String main(Model model,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @PageableDefault(page = 0, size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        //게시물 검색 관련

        model.addAttribute("keyword", keyword); //검색정보를 가져오기 위한 객체 주입


        
        //페이징 관련 로직

        Page<Board> list;   //해당하는 페이지와 검색옵션에 맞춰진 게시물들이 저장되는 변수


        if (keyword == null || keyword.equals("")) {
            //입력한 검색어가 없는 경우 및 처음으로 메인페이지에 방문할 경우
            list = boardService.findByBoardRole(BoardRole.YES, pageable);
        } else {
            //검색옵션이 제목인 경우
            list = boardService.findByBoardRoleAndTitle(BoardRole.YES, keyword, pageable);
        }

        List<Board> contents = list.getContent();   //현재 페이지와 설정된 검색옵션을 바탕으로 맞춰진 게시물 목록들
        int number = list.getNumber();  //현재 페이지 번호
        int totalPages = list.getTotalPages();  //전체 페이지 번호
        int first;  //(페이지가 2보다 같거나 크다는 조건)좌측버튼에 전달될 값
        int second; //(페이지가 2보다 같거나 크다는 조건)중앙버튼에 전달될 값
        int third;  //(페이지가 2보다 같거나 크다는 조건)우측버튼에 전달될 값


        if (number == 0 || number == 1) {
            //(전체페이지가 2보다 같거나 크다는 조건)현재 페이지가 0인경우
            first = 0;
            second = 1;
            third = 2;
        } else if (number == totalPages-1 || number == totalPages-2) {
            //(전체페이지가 2보다 같거나 크다는 조건)현재 페이지가 최대페이지번호와 같은 경우
            third = totalPages-1;
            second = totalPages-2;
            first = totalPages-3;
        } else {
            first = number-1;
            second = number;
            third = number+1;
        }


        BoardInfoDto boardInfo = new BoardInfoDto(contents, keyword, number, totalPages, first, second, third);    //현재 페이지의 게시물들과 그와 관련한 정보들을 객체화
        model.addAttribute("boardInfo", boardInfo); //해당 정보들을 해당 뷰에 전달


        
        return "main";
    }   //메인 페이지로 이동하는 메서드


    @GetMapping("/remain")
    public String redirectMain() {
        return "redirect:/main";
    }   //리다이렉트 방식으로 메인 페이지로 이동하는 메서드


    @GetMapping("/createcontent")
    public String createContent() {
        return "/board/board_create";
    }   //게시물 작성하는 페이지로 이동하는 메서드


    @ResponseBody
    @PostMapping("/uploadContent")
    public String uploadContent(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Account account,
            @RequestParam("title") String title,
            @RequestParam("contents") String contents
    ) {

        LocalDateTime recordTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));  //게시물 작성한 시간(한국시간)


        //파일 관련한 로직

        String filename = recordTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));  //작성한 게시물의 파일명  //ex)20210101000000000으로 기록됨
        String filepath = "./src/main/resources/contents/"+filename+".md";    //resources/contents 폴더에 해당 파일명으로 저장될 경로(확장자는 md)

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath)); //(이어서 작성을 하고 싶다면 true값을 추가로 작성하면됨)

            bw.write(contents);
            bw.flush();

            bw.close();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return "업로드 문제1";
        }   //사용자가 작성한 게시물을 특정한 해당 경로에 파일로 저장하는 과정


        
        //DB 관련 로직

        try {
            boardService.save(Board.createBoard(account, recordTime, recordTime, title, filepath, BoardRole.YES));
        } catch (Exception e) {
            return "업로드 문제2";
        }


        return "성공적으로 업로드 하였습니다";
    }   //작성한 게시물의 내용을 받아서 처리하는 메서드


    @GetMapping("/board")
    public String boardView(Model model,
                            @RequestParam("number") Long boardId,
                            @PageableDefault(page = 0, size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Board board = boardService.findById(boardId).orElseThrow(null); //해당 게시물과 관련한 정보

        
        //게시물 정보 관련

        Long boardNumber = board.getId();
        String boardTitle = board.getTitle();   //게시물 제목
        LocalDateTime firstRecordTime = board.getFirstRecordTime(); //게시물 기록 최초시간
        LocalDateTime modifyRecordTime = board.getModifyRecordTime();   //게시물 기록 수정시간
        String fileLocation = board.getFileLocation();  //게시물이 저장된 경로
        String boardContents = null;   //해당 경로로부터 가져온 게시물 내용(초기값 null)

        try {
            //게시물이 저장된 경로에서 해당 게시물을 추출하는 과정
            
            BufferedReader br = new BufferedReader(new FileReader(fileLocation));

            StringBuffer sbf = new StringBuffer();
            String str;
            while ((str = br.readLine()) != null) {
                sbf.append(str).append("\n");
            }

            boardContents = sbf.toString(); //최종적으로 추출된 게시물
            
        } catch (FileNotFoundException e) {
            //throw new RuntimeException(e);
            System.out.println("파일 오류");
        } catch (IOException e) {
            //throw new RuntimeException(e);
            System.out.println("입출력 오류");
        }

        BoardViewDto boardViewDto = new BoardViewDto(boardNumber, boardTitle, firstRecordTime, modifyRecordTime, boardContents);
        model.addAttribute("boardViewDto", boardViewDto);



        //해당 게시물의 댓글 정보 관련

        Page<Comment> commentList = commentService.findCommentFromBoard(boardId, pageable); //해당 게시판 댓글 페이지에 맞춰진 댓글들이 저장되는 변수
        List<Comment> list = commentList.getContent();  //페이지화 된 댓글을 리스트 형으로 가져오기
        int size = list.size(); //한 페이지에 보여지는 댓글의 개수
        int pageNumber = commentList.getNumber();//현재 댓글 페이지
        Boolean hasNext = commentList.hasNext();//다음 페이지가 존재하면 true 아닐경우 false

        CommentsDto commentsDto = new CommentsDto(list, size, pageNumber, hasNext);

        model.addAttribute("commentsDto", commentsDto);


        return "board/board_view";
    }   //사용자가 게시한 내용물 및 댓글 조회 및 로그인된 사용자만 댓글 작성이 가능한 페이지로 이동하는 메서드


    @ResponseBody
    @PostMapping("/uploadComments")
    public String uploadComments(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Account account,
            @RequestParam("boardNumber") Long boardNumber,
            @RequestParam("comments") String comments) {

        //DB에 해당 게시판의 댓글을 올리는 과정

        LocalDateTime recordTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));  //게시물 작성한 시간(한국시간)
        Board findBoard = boardService.findById(boardNumber).orElseThrow(null); //해당 게시판 객체
        Account findAccount = accountService.findById(account.getId()).orElseThrow(null);//댓글 작성한 사용자 객체

        Comment comment = Comment.createComment(findAccount, findBoard, recordTime, comments);  //댓글객체 생성
        commentService.save(comment);   //댓글 DB에 저장

        return "댓글 작성완료";

    }   //사용자가 해당 게시물에 작성한 댓글을 처리하는 메서드

    
    @PostMapping("/updateCommentsIndex")
    public String updateCommentsIndex (Model model, @RequestParam Map<String, Object> pageInfoMap) {

        //사용자가 댓글을 조회하려고 하는 페이지 정보를 각 해당 변수로 저장
        
        Long boardId = Long.valueOf((String) pageInfoMap.get("boardId"));  //해당 게시판의 인덱스 값(Id값)
        int pageNumber = Integer.parseInt((String) pageInfoMap.get("pageNumber"));  //조회 버튼 액션 이전의 현재 페이지 인덱스 번호
        int size = Integer.parseInt((String) pageInfoMap.get("size"));  //화면에 비춰지는 댓글의 최대 개수
        Boolean toPage = Boolean.parseBoolean((String) pageInfoMap.get("toPage"));  //사용자가 이전 혹은 다음 조회 버튼 조작여부 시그널
        

        if(toPage) {
            //다음 댓글 조회 시 페이지 번호 1증가
            pageNumber += 1;
        } else {
            //이전 댓글 조회 시 페이지 번호 1증가
            pageNumber -= 1;
        }

        
        //위의 정보를 토대로 해당하는 댓글들을 페이징 및 그와 관련한 정보를 객체화 해서 해당 화면에 객체를 전달
        
        PageRequest pageRequest = PageRequest.of(pageNumber, size, Sort.by(Sort.Direction.DESC, "id")); //사용자가 조회하려는 댓글의 영역의 페이지를 지정
        Page<Comment> commentList = commentService.findCommentFromBoard(boardId, pageRequest); //조회한 게시판 댓글 페이지에 맞춰진 댓글들이 저장되는 변수

        List<Comment> list = commentList.getContent();
        Boolean hasNext = commentList.hasNext();
        CommentsDto commentsDto = new CommentsDto(list, size, pageNumber, hasNext);

        model.addAttribute("commentsDto", commentsDto);
        
        return "board/board_view :: #commentsList";

    }   //사용자가 댓글을 조회할 경우 실행되는 메서드


    @GetMapping("/boardmodify")
    public String boardModify(
            Model model,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Account account,
            @RequestParam("number") Long boardId
    ) {
        //로그인 상태 체크
        
        if (account == null) {return "redirect:/main";} //로그인이 되어있지 않은 상태일 경우 자동으로 나가게 한다


        //수정 및 삭제하기 이전의 작성된 데이터와 그와 관련한 정보들을 모으는 과정
        
        Board board = boardService.findById(boardId).orElseThrow(null); //해당 게시물과 관련한 정보
        String title = board.getTitle();   //게시물 제목
        String fileLocation = board.getFileLocation();  //게시물이 저장된 경로
        String contents = null;   //해당 경로로부터 가져온 게시물 내용(초기값 null)

        try {
            //게시물이 저장된 경로에서 해당 게시물을 추출하는 과정

            BufferedReader br = new BufferedReader(new FileReader(fileLocation));

            StringBuffer sbf = new StringBuffer();
            String str;
            while ((str = br.readLine()) != null) {
                sbf.append(str).append("\n");
            }

            contents = sbf.toString(); //최종적으로 추출된 게시물

        } catch (FileNotFoundException e) {
            //throw new RuntimeException(e);
            System.out.println("파일 오류");
        } catch (IOException e) {
            //throw new RuntimeException(e);
            System.out.println("입출력 오류");
        }

        BeforeBoardInfo beforeBoardInfo = new BeforeBoardInfo(boardId, title, fileLocation, contents);//작성된 게시물 변경하기 이전의 정보들
        model.addAttribute("beforeBoardInfo", beforeBoardInfo);

        return "/board/board_modify";
        
    }   //사용자가 작성한 게시물을 수정하는 목적 및 관련 데이터들을 가지고 이동하는 메서드


    @ResponseBody
    @PostMapping("/updateContents")
    public String updateContents(@RequestParam Map<String, Object> userCommentInfoMap) {

        LocalDateTime recordTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));  //게시물 작성한 시간(한국시간)

        Long boardId = Long.valueOf((String) userCommentInfoMap.get("boardId"));    //수정한 게시물 고유 Id값
        String title = (String) userCommentInfoMap.get("title");    //사용자가 수정한 제목
        String contents = (String) userCommentInfoMap.get("contents");  //사용자가 수정한 내용물

        Board getBoard = boardService.findById(boardId).orElseThrow(null);  //수정하기 전의 게시물 관련 DB
        String filepath = getBoard.getFileLocation();   //기존에 존재하는 파일의 경로


        //기존에 작성한 파일의 내용을 수정한 내용으로 덮어쓰기

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath)); //(이어서 작성을 하고 싶다면 true값을 추가로 작성하면됨)

            bw.write(contents);
            bw.flush();

            bw.close();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            return "업로드 문제1";
        }   //사용자가 수정한 게시물을 기존 경로에 파일로 저장하는 과정


        boardService.modifyBoardInfo(getBoard, title, recordTime);    //기존 게시물의 내용과 시간을 수정

        return "/usercustom";
    }   //사용자가 게시한 게시물 수정 버튼 클릭시 발생하는 이벤트


    @ResponseBody
    @PostMapping("/softDeleteContents")
    public String softDeleteContents(@RequestParam Map<String, Object> userCommentInfoMap) {

        Long boardId = Long.valueOf((String) userCommentInfoMap.get("boardId"));

        Board board = boardService.findById(boardId).orElseThrow(null);
        boardService.softDeleteBoard(board);

        return "/usercustom";
    }   //사용자가 게시한 게시물 수정 버튼 클릭시 발생하는 이벤트
}
