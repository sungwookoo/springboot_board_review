package com.sungwoo.board.controller;

import com.sungwoo.board.domain.Board;
import com.sungwoo.board.dto.FileDto;
import com.sungwoo.board.service.BoardService;
import com.sungwoo.board.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import util.MD5Generator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class BoardController {
    private BoardService boardService;
    private FileService fileService;

    public BoardController(BoardService boardService, FileService fileService){
        this.boardService = boardService;
        this.fileService = fileService;

    }

    @GetMapping("/")
    public String list(@PageableDefault Pageable pageable, Model model) {
        //@PageableDefault 어노테이션은 페이징의 size, sort 를 사용할 수 있게 해주고 page= 로 특정할수있게 해준다.
        model.addAttribute("boardList",boardService.findBoardList(pageable));
        return "index";
    }



    @PostMapping("/add")
    public String add(Board board, Model model, @RequestParam("file")MultipartFile files){
        try {
            String origFilename = files.getOriginalFilename();
            String filename = new MD5Generator(origFilename).toString();
            model.addAttribute("filename",origFilename);
            /* 실행되는 위치의 'files' 폴더에 파일이 저장됩니다. */
            String savePath = System.getProperty("user.dir") + "\\files";
            /* 파일이 저장되는 폴더가 없으면 폴더를 생성합니다. */

            if (!new java.io.File(savePath).exists()) {
                try {
                    new java.io.File(savePath).mkdir();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }

            String filePath = savePath + "\\" + filename;
            files.transferTo(new File(filePath));
            FileDto fileDto = new FileDto();
            fileDto.setOrigFilename(origFilename);
            fileDto.setFilename(filename);
            fileDto.setFilePath(filePath);
            Long fileId = fileService.saveFile(fileDto);
            board.setFileId(fileId);
        }catch(Exception e){
            e.printStackTrace();
        }
        board.setCreatedDat(LocalDateTime.now());
        Board saveBoard = boardService.saveBoard(board);
        model.addAttribute("board",boardService.findBoardByIdx(saveBoard.getIdx()));
        return "item";
    }

    @GetMapping("/new")
    public String form(Board board){
        return "new";
    }

    @GetMapping("/{idx}")
    public String read(@PathVariable Long idx, Model model){
        String origFilename = fileService.getFile(boardService.findBoardByIdx(idx).getFileId()).getOrigFilename();
        long views = boardService.findBoardByIdx(idx).getViews();
        boardService.findBoardByIdx(idx).setViews(views+1);
        boardService.saveBoard(boardService.findBoardByIdx(idx));
        model.addAttribute("filename",origFilename);
        model.addAttribute("board", boardService.findBoardByIdx(idx));
        return "item";
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
    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> fileDownload(@PathVariable("fileId") Long fileId, HttpServletRequest req, HttpServletResponse res) throws IOException {
        FileDto fileDto = fileService.getFile(fileId);
        Path path = Paths.get(fileDto.getFilePath());
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));

        String browser=getBrowser(req);
        res.setContentType("application/octet-stream; charset=UTF-8");
        String encodedFilename = getFileNm(browser,fileDto.getOrigFilename());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                .body(resource);
    }

    @GetMapping("/delete/{idx}")
    public String deleteBasic(@PathVariable("idx") long idx, Model model, @PageableDefault Pageable pageable){
        Board board = boardService.findBoardByIdx(idx);
        boardService.deleteBoard(board);
        model.addAttribute("boardList",boardService.findBoardList(pageable));
        return "index";
    }

//----------인코딩-------------
    public String getBrowser(HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent");
        if(userAgent.indexOf("MSIE") > -1
                || userAgent.indexOf("Trident") > -1 //IE11
                || userAgent.indexOf("Edge") > -1)
        {
            return "MSIE";
        }
        else if(userAgent.indexOf("Chrome") > -1)
        {
            return "Chrome";
        }
        else if(userAgent.indexOf("Opera") > -1)
        {
            return "Opera";
        }
        else if(userAgent.indexOf("Safari") > -1)
        {
            return "Safari";
        }
        else if(userAgent.indexOf("Firefox") > -1)
        {
            return "Firefox";
        }
        else {
            return null;
        }
    }

    public String getFileNm(String browser, String fileNm){
        String reFileNm = null;
        try {
            if (browser.equals("MSIE") || browser.equals("Trident") || browser.equals("Edge")) {
                reFileNm = URLEncoder.encode(fileNm, "UTF-8").replaceAll("\\+", "%20");
            }
            else {
                if(browser.equals("Chrome")){
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < fileNm.length(); i++) {
                        char c = fileNm.charAt(i);
                        if (c > '~') {
                            sb.append(URLEncoder.encode(Character.toString(c), "UTF-8"));
                        }
                        else {
                            sb.append(c);
                        }
                    }
                    reFileNm = sb.toString();
                }
                else{
                    reFileNm = new String(fileNm.getBytes("UTF-8"), "ISO-8859-1");
                }
                if(browser.equals("Safari") || browser.equals("Firefox")) reFileNm = URLDecoder.decode(reFileNm);
            }
        } catch(Exception e){}
        return reFileNm;
    }


}
