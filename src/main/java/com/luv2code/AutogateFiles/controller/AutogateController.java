package com.luv2code.AutogateFiles.controller;

import com.luv2code.AutogateFiles.Domain.CSVRow;
import com.luv2code.AutogateFiles.services.AutogateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

@Controller
public class AutogateController {

    @Autowired
    private AutogateService autogateService;

    @Autowired
    private ServletContext context;

    @GetMapping("/")
    public String ShowAutogateFile(Model model){
        model.addAttribute("csvRow", new CSVRow());
        return "Autogate";
    }

    @PostMapping("/files")
    public String createCSVFiles(@ModelAttribute("csvRow") CSVRow csvRow,Model model) throws Exception {

        String csvFileResponse= autogateService.generateAutogateFile(csvRow,context);
        model.addAttribute("csvFileResponse",csvFileResponse);

       return  "AutogatePost";
    }

    @GetMapping("/createCSV/{fileName}")
    public void downloadCSVFile(HttpServletRequest request, HttpServletResponse response,@PathVariable String  fileName) throws FileNotFoundException {

      boolean downloadFile = autogateService.getCSVFile(fileName,request,response);
      return;
    }
}
