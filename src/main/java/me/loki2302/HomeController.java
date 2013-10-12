package me.loki2302;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {
    @Autowired
    private FileDao fileDao;
    
    @RequestMapping(value = "/", method = RequestMethod.GET)    
    public String index(Model model) {
        List<FileRow> files = fileDao.getFiles();
        model.addAttribute("files", files);
        return "index";
    }
    
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(CustomFile customFile) throws IOException {
        MultipartFile file = customFile.getFile(); 
        String fileName = file.getOriginalFilename();
        long fileSize = file.getSize();        
        System.out.printf("got file: %s, %d\n", fileName, fileSize);
        
        fileDao.insertFile(fileName, file.getBytes());
                
        return "redirect:/";
    }
    
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET, produces = "application/octet-stream")
    @ResponseBody
    public Resource download(@PathVariable int id, HttpServletResponse response) throws SQLException {
        final FileDataRow fileDataRow = fileDao.getFileData(id);
        
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileDataRow.Name));
        
        InputStream inputStream = new ByteArrayInputStream(fileDataRow.Data);
        return new InputStreamResource(inputStream) {
            @Override
            public long contentLength() throws IOException {
                return fileDataRow.Data.length;
            }            
        };        
    }
}