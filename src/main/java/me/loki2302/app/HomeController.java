package me.loki2302.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
    
    @RequestMapping(
            value = "/download/{id}", 
            method = RequestMethod.GET, 
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(
            @PathVariable int id, 
            HttpServletResponse response) throws IOException {
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
    }

    @RequestMapping(
            value = "/download-mc/{id}",
            method = RequestMethod.GET)
    @ResponseBody
    public DownloadableFile downloadWithMessageConverter(@PathVariable final int id) {
        FileRow fileRow = fileDao.getFileData(id);
        if(fileRow == null) {
            throw new RuntimeException("no such file");
        }

        return new DownloadableFile(fileRow.Name, fileRow.Size, new DownloadableFileContentProvider() {
            @Override
            public void writeContent(OutputStream outputStream) {
                fileDao.writeFileDataToOutputStream(id, outputStream);
            }
        });
    }
}
