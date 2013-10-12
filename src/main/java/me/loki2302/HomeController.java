package me.loki2302;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    public String upload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        InputStream fileInputStream = file.getInputStream();
        int fileSize = (int)file.getSize();
        System.out.printf("got file: %s, %d\n", fileName, fileSize);
        
        int fileId = fileDao.insertFileFromStream(fileName, fileInputStream, fileSize);
        System.out.printf("Inserted file %s with id %d\n", fileName, fileId);
                
        return "redirect:/";
    }
    
    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET, produces = "application/octet-stream")
    public void download(@PathVariable int id, HttpServletResponse response) throws SQLException, IOException {
        try {
            FileRow fileRow = fileDao.getFileData(id);
            if(fileRow == null) {
                throw new RuntimeException("no such file");
            }
                               
            response.setContentLength((int)fileRow.Size);
            
            String fileName = fileRow.Name;            
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            
            OutputStream responseOutputStream = response.getOutputStream();            
            fileDao.writeFileDataToOutputStream(id, responseOutputStream);            
            responseOutputStream.flush();
        } catch(Exception e) {
            e.printStackTrace();            
        }
    }
}