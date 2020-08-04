package com.sungwoo.board.service;

import com.sungwoo.board.domain.Board;
import com.sungwoo.board.repository.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository){
        this.boardRepository=boardRepository;
    }

    public Page<Board> findBoardList(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() -1, pageable.getPageSize());
        return boardRepository.findAll(pageable);
    }

    public List<Board> findBoardList() {
        return boardRepository.findAll();
    }

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    public Board findBoardByIdx(Long idx) {
        return boardRepository.findById(idx).orElse(new Board());
    }

    public void deleteBoard(Board board){
        boardRepository.delete(board);
    }

}
