package com.luv2code.AutogateFiles.services;

import com.luv2code.AutogateFiles.Domain.CSVRow;
import com.luv2code.AutogateFiles.Exception.MyFileNotFoundException;
import com.luv2code.AutogateFiles.Utility.Constants;
import com.luv2code.AutogateFiles.Utility.SecureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
public class AutogateService {

    @Autowired
    private ServletContext context;

    public String generateAutogateFile(CSVRow csvRow,ServletContext context) throws Exception {
        HashMap<String,String>  macData = new HashMap<>();
        macData.put(Constants.terminalID,"3PSI0001");
        macData.put(Constants.BeneficiaryCode,"12345232");
        macData.put(Constants.AMOUNT,csvRow.getAmount());
        macData.put(Constants.Beneficiary_Account,"0156426274");

        System.out.println("MacData " + macData);

        String publicKeyExponent = "010001";
        String publicKeyModulus = "009c7b3ba621a26c4b02f48cfc07ef6ee0aed8e12b4bd11c5cc0abf80d5206be69e1891e60fc88e2d565e2fabe4d0cf630e318a6c721c3ded718d0c530cdf050387ad0a30a336899bbda877d0ec7c7c3ffe693988bfae0ffbab71b25468c7814924f022cb5fda36e0d2c30a7161fa1c6fb5fbd7d05adbef7e68d48f8b6c5f511827c4b1c5ed15b6f20555affc4d0857ef7ab2b5c18ba22bea5d3a79bd1834badb5878d8c7a4b19da20c1f62340b1f7fbf01d2f2e97c9714a9df376ac0ea58072b2b77aeb7872b54a89667519de44d0fc73540beeaec4cb778a45eebfbefe2d817a8a8319b2bc6d9fa714f5289ec7c0dbc43496d71cf2a642cb679b0fc4072fd2cf";

        String expiryDate = csvRow.getExpiryYear()+csvRow.getExpiryMonth();
       // HashMap<String,String> secureMacPinData = SecureUtil.getSecureDataForAutoGate(publicKeyExponent,publicKeyModulus,csvRow.getInputPAN(),expiryDate,"",csvRow.getInputPIN(),macData,12);


        String secureMacPinData = SecureUtil.getSecureData(publicKeyModulus,publicKeyExponent,csvRow.getInputPAN(),expiryDate,"",csvRow.getInputPIN(),12);
        String input="3PSI0001"+"12345232"+csvRow.getAmount()+"0156426274";
        String macDataUpdated = SecureUtil.MacData(input);

        byte[] keybyte = SecureUtil.generateKey();
        String pinBlock = SecureUtil.getPINBlock(csvRow.getInputPIN(),"",expiryDate,keybyte);

        long timeStamp = System.currentTimeMillis();


        //Calculate the first line row
        String batchReference = "PSI|" + SecureUtil.RandomKeyGenerator() + "|" + timeStamp;
        String batchDescription = "SheriffFiles" + SecureUtil.RandomKeyGenerator();
        Boolean isSingleDebit = true;


        //Add the generated row to an list
        List<String> firstRow = new ArrayList<>();
        firstRow.add(batchReference);
        firstRow.add(batchDescription);
        firstRow.add(isSingleDebit.toString());
        firstRow.add(macData.get(Constants.terminalID));

        for(String rowFirst : firstRow){
            System.out.println("First Row" + rowFirst);
        }

        //Calculate the second line row
        String EncrptedData = secureMacPinData;
        String SourceAccountNumber = csvRow.getSourceAccountNumber();
        String SourceAccountType = csvRow.getSourceAccountType();
        String BankCbnCode =  csvRow.getSourceBankCbnCode();
        String EncrptedPIN = pinBlock;
        String mac_Data = macDataUpdated;

        List<String> secondRow = new ArrayList<>();
        secondRow.add(EncrptedData);
        secondRow.add(SourceAccountNumber);
        secondRow.add(SourceAccountType);
        secondRow.add(BankCbnCode);
        secondRow.add(EncrptedPIN);
        secondRow.add(mac_Data);

        for (String rowSecond : secondRow){
            System.out.println("Second Row " +  rowSecond);
        }

        //You can connect to a database to get the records to upload to the SFTP. For the purpose of illustration, The data will be hosted in code using an array.
        String totalBeneficiaryCodes = macData.get(Constants.BeneficiaryCode);
        String totalAmount = csvRow.getAmount();
        String TotalbeneficiaryAccounts = macData.get(Constants.Beneficiary_Account);


        String[] paymentList = {
                SecureUtil.RandomKeyGenerator(),
                totalAmount,
                Constants.Narration,
                totalBeneficiaryCodes,
                Constants.Beneficiary_Email_Address,
                Constants.Beneficiary_Bank_Cbn_Code,
                TotalbeneficiaryAccounts,
                Constants.Beneficiary_Account_Type,
                Constants.IS_PrepaidLoad,
                Constants.Currency_Code,
                Constants.Beneficiary_Name
        };


        /*String filePath = context.getRealPath("/resources/report");
        System.out.println("This is the file Path " + filePath);

        boolean exists = new File(filePath).exists();
        if(!exists){
            new File(filePath).mkdirs();
        }

        File file = new File(filePath + "/"+ File.separator+ SecureUtil.RandomFilenameGenerator() + ".csv");
        System.out.println("This is the File " + file);*/

        String nameofFile = SecureUtil.RandomFilenameGenerator() + ".csv";

        String fileName = "src/main/resources/"+ nameofFile;
        Path myPaths = Paths.get(fileName);



        try{

            BufferedWriter writer = Files.newBufferedWriter(myPaths, StandardCharsets.UTF_8);

            List<List<String>> records = Arrays.asList(firstRow,secondRow,Arrays.asList(paymentList));

            for(List<String> record : records){
                writer.write(String.join(",",record));
                writer.newLine();
            }
            writer.flush();
            writer.close();

        }catch (Exception e){
            throw new MyFileNotFoundException("File cannot be created " + fileName);
        }

       // myPaths.
       return nameofFile;
      //return fileName.endsWith(".csv");

    }


    public boolean getCSVFile(String file, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
        String filePath=null;
        try {
              filePath = "src/main/resources/"+file;
            //filePath = context.getRealPath(file);

            fileDownLoad(filePath,response,file);

        }catch (Exception ex){
            throw new MyFileNotFoundException("Url Exception " + filePath);

        }
        return true;
    }

    private void fileDownLoad(String filePath, HttpServletResponse response,String fileName) throws FileNotFoundException {
        File file = new File(filePath);
        final  int BUFFER_SIZE = 4096;
        if(file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                 String mimeType = context.getMimeType(filePath);
                 response.setContentType(mimeType);
                 response.setHeader("content-disposition", "attachment; fileName="+fileName);
                 OutputStream outputStream = response.getOutputStream();
                 byte[] bytes = new byte[BUFFER_SIZE];
                 int byteReaders = -1;
                 while((byteReaders =fileInputStream.read(bytes)) != -1){
                     outputStream.write(bytes,0,byteReaders);
                 }

                   fileInputStream.close();
                   outputStream.flush();
                   outputStream.close();

                   //file.delete();


            }catch (Exception ex){
              throw  new FileNotFoundException("File Not Found " + fileName);
            }
        }else{
            throw new MyFileNotFoundException("File Not Found " + fileName);
        }


    }
}
