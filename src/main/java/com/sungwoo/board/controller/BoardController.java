package com.sungwoo.board.controller;

import com.sungwoo.board.domain.Board;
import com.sungwoo.board.repository.BoardRepository;
import com.sungwoo.board.service.BoardService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BoardController {
    private BoardService boardService;
    private BoardRepository boardRepository;

    public BoardController(BoardService boardService, BoardRepository boardRepository){
        this.boardService = boardService;
        this.boardRepository = boardRepository;
    }

    @GetMapping("/")
    public String list(@PageableDefault Pageable pageable, Model model) {
        //@PageableDefault 어노테이션은 페이징의 size, sort 를 사용할 수 있게 해주고 page= 로 특정할수있게 해준다.
        model.addAttribute("boardList",boardService.findBoardList(pageable));
        return "index";
    }

    @GetMapping("/{idx}")
    public String read(@PathVariable Long idx, Model model){
        model.addAttribute("board", boardService.findBoardByIdx(idx));
        return "item";
    }

    @PostMapping("/add")
    public String add(Board board, Model model){
        board.setCreatedDat(LocalDateTime.now());
        Board saveBoard = boardService.saveBoard(board);
        model.addAttribute("board",boardService.findBoardByIdx(saveBoard.getIdx()));
        return "item";
    }

    @GetMapping("/new")
    public String form(Board board){
        return "new";
    }

    @GetMapping("/edit/{idx}")
    public String showUpdateForm(@PathVariable("idx") long idx, Model model){
        Board board = boardService.findBoardByIdx(idx);
            model.addAttribute("board", board);
            return "update";
    }

    @PostMapping("/update/{idx}")
    public String updateBoard(@PathVariable("idx") long idx, @Validated Board board, BindingResult result, Model model, HttpServletResponse response) throws IOException {
        //@Validated는 바인딩할 객체필드를 검증하기위해 사용
        //BindingResult는 Validator를 상속받는 클래스에서 객체값을 검증
        if(result.hasErrors()) {
            board.setIdx(idx);
            return "update";
        }

        int check = boardService.checkPassword(board,idx);
        if(check==0) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>");
            out.println("alert('비밀번호 불일치');");
            out.println("history.back();");
            out.println("</script>");
            out.close();
            return "null";
        }
            board.setCreatedDat(LocalDateTime.now());
            boardService.saveBoard(board);
            List<Board> boardList = boardService.findBoardList();
            model.addAttribute("boardList", boardList);
            return "item";


    }


    @GetMapping("/delete/{idx}")
    public String deleteBasic(@PathVariable("idx") long idx, Model model, @PageableDefault Pageable pageable){
        Board board = boardService.findBoardByIdx(idx);
        boardService.deleteBoard(board);
        model.addAttribute("boardList",boardService.findBoardList(pageable));
        return "index";
    }
}
