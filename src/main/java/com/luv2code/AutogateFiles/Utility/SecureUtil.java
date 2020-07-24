package com.luv2code.AutogateFiles.Utility;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.encoders.Hex;


public class SecureUtil {

    public static String RandomKeyGenerator() {
        /*int maxSize = 10;
        char[] chars = new char[10];
        String a;
        a = "123456";
        chars = a.toCharArray();
        int size = maxSize;
        byte[] data = new byte[1];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(data);
        size = maxSize;
        data = new byte[size];
         secureRandom.nextBytes(data);

         StringBuilder stringBuilder = new StringBuilder(size);
         for(byte b :data){
             System.out.println("This is the Data " +data.toString());

           stringBuilder.append(chars[b%(chars.length-1)]);
             //stringBuilder.append(chars[b/(chars.length-1)]);
         }

        System.out.println("STring Builder :  " + stringBuilder.toString());

         return stringBuilder.toString();*/

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        System.out.println("Salt String " + saltStr);
        return saltStr;



    }

    public static String RandomFilenameGenerator() {

       /* int maxSize = 5;
        char[] chars = new char[4];
        String a = "ABCDEFGHIJKLMNOPQRSTVWYXZ";
        chars = a.toCharArray();

        int size = maxSize;
        byte[] data = new byte[1];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(data);
        size= maxSize;
        data = new byte[size];
        secureRandom.nextBytes(data);

        StringBuilder stringBuilder = new StringBuilder(size);
        for(byte b :data){
            stringBuilder.append(chars[b%(chars.length-1)]);
        }

        System.out.println("STring Builder :  " + stringBuilder.toString());

        return stringBuilder.toString();*/

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        System.out.println("Salt String " + saltStr);
        return saltStr;
    }

    public String getPinBlock(String pin, String cvv2, String expiryDate, byte[] pinKey) {
        if (pin == null || pin.equals("")) {
            pin = "0000";
        }
        if (cvv2 == null || cvv2.equals("")) {
            cvv2 = "000";
        }
        if (expiryDate == null || expiryDate.equals("")) {
            expiryDate = "0000";
        }

        String pinBlockString = pin + cvv2 + expiryDate;
        int pinBlockStringLenth = pinBlockString.length();
        int pinBlockStringLength_Length = String.valueOf(pinBlockStringLenth).length();
        String clearPinBlock = String.valueOf(pinBlockStringLength_Length) + String.valueOf(pinBlockStringLenth) + pinBlockString;

        Random random = new SecureRandom();
        int randomDigit = random.nextInt(10);

        int pinPadLength = 16 - clearPinBlock.length();
        for (int i = 0; i < pinPadLength; i++) {
            clearPinBlock += String.valueOf(randomDigit);
        }

        DESedeEngine engine = new DESedeEngine();
        DESedeParameters keyParameters = new DESedeParameters(pinKey);
        engine.init(true, keyParameters);

        byte[] clearPINBlockBytes = Hex.decode(clearPinBlock);
        byte[] encryptedPINBlockBytes = new byte[8];

        engine.processBlock(clearPINBlockBytes, 0, encryptedPINBlockBytes, 0);

        byte[] encodedEncryptedPINBlockBytes = Hex.encode(encryptedPINBlockBytes);
        String pinBlock = new String(encodedEncryptedPINBlockBytes);

        return pinBlock;
    }

    public static String getPINBlock(String pin, String cvv2, String expiryDate, byte[] keyBytes) {
        pin = null == pin || pin.equals("") ? "0000" : pin;
        cvv2 = null == cvv2 || cvv2.equals("") ? "000" : cvv2;
        expiryDate = null == expiryDate || expiryDate.equals("") ? "0000" : expiryDate;

        String pinBlockString = pin + cvv2 + expiryDate;
        System.out.println("Card details :  "  + pinBlockString.toString());
        
        int pinBlockStringLen = pinBlockString.length();
        String pinBlickLenLenString = String.valueOf(pinBlockStringLen);
        int pinBlickLenLen = pinBlickLenLenString.length();
        String clearPINBlock = String.valueOf(pinBlickLenLen) + pinBlockStringLen + pinBlockString;


        byte randomBytes = 0x0;
        int randomDigit = (int) ((randomBytes * 10) / 128);
        randomDigit = Math.abs(randomDigit);
        int pinpadlen = 16 - clearPINBlock.length();
        for (int i = 0; i < pinpadlen; i++) {
            clearPINBlock = clearPINBlock + randomDigit;
        }

        DESedeEngine engine = new DESedeEngine();
        DESedeParameters keyParameters = new DESedeParameters(keyBytes);
        engine.init(true, keyParameters);
        byte[] clearPINBlockBytes = Hex.decode(clearPINBlock);
        byte[] encryptedPINBlockBytes = new byte[8];
        engine.processBlock(clearPINBlockBytes, 0, encryptedPINBlockBytes, 0);
        byte[] encodedEncryptedPINBlockBytes = Hex.encode(encryptedPINBlockBytes);
        String pinBlock = new String(encodedEncryptedPINBlockBytes);

        pin = "0000000000000000";
        clearPINBlock = "0000000000000000";

        zeroise(clearPINBlockBytes);
        zeroise(encryptedPINBlockBytes);
        zeroise(encodedEncryptedPINBlockBytes);

        return pinBlock;
    }

    public String getPinData(String pin, byte[] pinKey) {
        String clearPINBlock = "1" + pin.length() + pin;
        Random random = new SecureRandom();
        int randomDigit = random.nextInt(10);

        int pinPadLength = 14 - pin.length();
        for (int i = 0; i < pinPadLength; i++) {
            clearPINBlock += String.valueOf(randomDigit);
        }

        DESedeEngine engine = new DESedeEngine();
        DESedeParameters keyParameters = new DESedeParameters(pinKey);
        engine.init(true, keyParameters);

        byte[] clearPINBlockBytes = Hex.decode(clearPINBlock);
        byte[] encryptedPINBlockBytes = new byte[8];
        engine.processBlock(clearPINBlockBytes, 0, encryptedPINBlockBytes, 0);

        byte[] encodedEncryptedPINBlockBytes = Hex.encode(encryptedPINBlockBytes);
        String pinBlock = new String(encodedEncryptedPINBlockBytes);

        return pinBlock;
    }

  
    
    public static String  getSecureData(String publicModulus, String publicExponent,String pan, String expDate, String cvv, String pin, int macVer) throws UnsupportedEncodingException{
    	 byte[] secureBytes = new byte[64];
         byte[] headerBytes = new byte[1];
         byte[] formatVersionBytes = new byte[1];
         byte[] macVersionBytes = new byte[1];
         byte[] pinDesKey = new byte[16];
         byte[] macDesKey = new byte[16];
         byte[] macBytes = new byte[4];
         byte[] customerIdBytes = new byte[10];
         byte[] footerBytes = new byte[1];
         byte[] otherBytes = new byte[14];
         byte[] keyBytes = generateKey();

         System.arraycopy(customerIdBytes, 0, secureBytes, 35, 10);
         System.arraycopy(macBytes, 0, secureBytes, 45, 4);
         System.arraycopy(otherBytes, 0, secureBytes, 49, 14);
         System.arraycopy(footerBytes, 0, secureBytes, 63, 1);

         headerBytes = HexConverter("4D");
         formatVersionBytes = HexConverter("10");
         macVersionBytes = HexConverter("10");

         pinDesKey = keyBytes;

         if (pan != null && pan != "") {
             int panDiff = 20 - pan.length();
             String panString = panDiff + pan;
             int panlen = 20 - panString.length();
             for (int i = 0; i < panlen; i++) {
                 panString += "F";
             }

             customerIdBytes = HexConverter(padRight(panString, 20));

         }

         String macData = "";
//         macBytes = Hex.decode(getMAC(macData, macDesKey, 11));
         String mac = getMAC(macData, macDesKey, macVer);
         macBytes = Hex.decode(mac);
         footerBytes = HexConverter("5A");

         System.arraycopy(headerBytes, 0, secureBytes, 0, 1);
         System.arraycopy(formatVersionBytes, 0, secureBytes, 1, 1);
         System.arraycopy(macVersionBytes, 0, secureBytes, 2, 1);
         System.arraycopy(pinDesKey, 0, secureBytes, 3, 16);
         System.arraycopy(macDesKey, 0, secureBytes, 19, 16);
         System.arraycopy(customerIdBytes, 0, secureBytes, 35, 10);
         System.arraycopy(macBytes, 0, secureBytes, 45, 4);
         System.arraycopy(otherBytes, 0, secureBytes, 49, 14);
         System.arraycopy(footerBytes, 0, secureBytes, 63, 1);

         RSAEngine engine = new RSAEngine();
         RSAKeyParameters publicKeyParameters = getPublicKey(publicModulus, publicExponent);
         engine.init(true, publicKeyParameters);
         byte[] encryptedSecureBytes = engine.processBlock(secureBytes, 0, secureBytes.length);
         byte[] encodedEncryptedSecureBytes = Hex.encode(encryptedSecureBytes);
         String encrytedSecure = new String(encodedEncryptedSecureBytes);
         zeroise(secureBytes);
  	   	
    	return encrytedSecure;
    }
    


    public  static HashMap<String, String> getSecureDataForAutoGate(String publicExponent, String publicModulus, String pan, String expDate, String cvv, String pin, HashMap<String,String>macData, int macVer) throws Exception {

        HashMap<String, String> secureData = new HashMap<String, String>();

        byte[] secureBytes = new byte[64];
        byte[] headerBytes = new byte[1];
        byte[] formatVersionBytes = new byte[1];
        byte[] macVersionBytes = new byte[1];
        byte[] pinDesKey = new byte[16];
        byte[] macDesKey = new byte[16];
        byte[] macBytes = new byte[4];
        byte[] customerIdBytes = new byte[10];
        byte[] footerBytes = new byte[1];
        byte[] otherBytes = new byte[14];
        byte[] keyBytes = generateKey();

        System.arraycopy(customerIdBytes, 0, secureBytes, 35, 10);
        System.arraycopy(macBytes, 0, secureBytes, 45, 4);
        System.arraycopy(otherBytes, 0, secureBytes, 49, 14);
        System.arraycopy(footerBytes, 0, secureBytes, 63, 1);

        headerBytes = HexConverter("4D");
        formatVersionBytes = HexConverter("10");
        macVersionBytes = HexConverter("10");

        pinDesKey = keyBytes;

        if (pan != null && pan != "") {
            int panDiff = 20 - pan.length();
            String panString = panDiff + pan;
            int panlen = 20 - panString.length();
            for (int i = 0; i < panlen; i++) {
                panString += "F";
            }

            customerIdBytes = HexConverter(padRight(panString, 20));

        }

        String macDataToCompute= getMACDataVersionForAutogate(macData);

//        macBytes = Hex.decode(getMAC(macData, macDesKey, 11));
        String mac = getMAC(macDataToCompute, macDesKey, macVer);
        macBytes = Hex.decode(mac);
        footerBytes = HexConverter("5A");

        System.arraycopy(headerBytes, 0, secureBytes, 0, 1);
        System.arraycopy(formatVersionBytes, 0, secureBytes, 1, 1);
        System.arraycopy(macVersionBytes, 0, secureBytes, 2, 1);
        System.arraycopy(pinDesKey, 0, secureBytes, 3, 16);
        System.arraycopy(macDesKey, 0, secureBytes, 19, 16);
        System.arraycopy(customerIdBytes, 0, secureBytes, 35, 10);
        System.arraycopy(macBytes, 0, secureBytes, 45, 4);
        System.arraycopy(otherBytes, 0, secureBytes, 49, 14);
        System.arraycopy(footerBytes, 0, secureBytes, 63, 1);

        RSAEngine engine = new RSAEngine();
        RSAKeyParameters publicKeyParameters = getPublicKey(publicModulus, publicExponent);
        engine.init(true, publicKeyParameters);
        byte[] encryptedSecureBytes = engine.processBlock(secureBytes, 0, secureBytes.length);
        byte[] encodedEncryptedSecureBytes = Hex.encode(encryptedSecureBytes);
        String encrytedSecure = new String(encodedEncryptedSecureBytes);

        zeroise(secureBytes);

        String pinBlock = getPINBlock(pin, cvv, expDate, keyBytes);

        secureData.put(Constants.SECURE, encrytedSecure);
        secureData.put(Constants.PINBLOCK, pinBlock);
        secureData.put(Constants.MACDATA, mac);

        return secureData;
    }

    private static String getMACDataVersionForAutogate(HashMap<String,String> mac) {

        //String terminalId, String totalBeneficiaryCodes, String totalAmount, String TotalbeneficiaryAccounts
        String macData = "";
        if(mac.get(Constants.terminalID) !=null) {
            macData +=mac.get(Constants.terminalID);
        }
        if(mac.get(Constants.BeneficiaryCode) != null ) {
            macData +=mac.get(Constants.BeneficiaryCode);
        }
        if(mac.get(Constants.AMOUNT) != null) {
            macData += mac.get(Constants.AMOUNT);
        }
        if(mac.get(Constants.Beneficiary_Account) != null) {
            macData += mac.get(Constants.Beneficiary_Account);
        }

        System.out.println("This is macData " + macData);
        return macData;
    }



    private static RSAKeyParameters getPublicKey(String modulus, String exponent)
    {
        BigInteger modulusByte = new BigInteger(Hex.decode(modulus));
        BigInteger exponentByte = new BigInteger(Hex.decode(exponent));
        RSAKeyParameters pkParameters = new RSAKeyParameters(false, modulusByte, exponentByte);
        return pkParameters;
    }

    private static byte[] HexConverter(String str)
    {
        try {
            str = new String(str.getBytes(),StandardCharsets.UTF_8);
            byte[] myBytes = Hex.decode(str);
            return myBytes;
        } catch (Exception ex) {
            Logger.getLogger(SecureUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static String padRight(String data, int maxLen)
    {

        if(data == null || data.length() >= maxLen)
            return data;

        int len = data.length();
        int deficitLen = maxLen - len;
        for(int i=0; i<deficitLen; i++)
            data += "0";

        return data;
    }

    public byte[] GenerateKey(){
      SecureRandom sr = new SecureRandom();
        KeyGenerationParameters kgp = new KeyGenerationParameters(sr, DESedeParameters.DES_KEY_LENGTH * 16);
       DESedeKeyGenerator kg = new DESedeKeyGenerator();
       kg.init(kgp);


       byte[] desKeyBytes = kg.generateKey();
        DESedeParameters.setOddParity(desKeyBytes);

       return desKeyBytes;
   }

    public static byte[] generateKey() {
        SecureRandom sr = new SecureRandom();
        KeyGenerationParameters kgp = new KeyGenerationParameters(sr, 128);
        DESedeKeyGenerator kg = new DESedeKeyGenerator();
        kg.init(kgp);

        byte[] desKeyBytes = {16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16};
        return desKeyBytes;
    }

    private static void zeroise(byte[] data) {
        int len = data.length;

        for (int i = 0; i < len; i++)
            data[i] = 0;
    }



    public static String MacData(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            System.out.println("HashText " + hashtext);
            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMAC(String macData, byte[] macKey, int macVersion) throws UnsupportedEncodingException {
       
    	
    	byte[] macBytes = new byte[4];
        byte[] macDataBytes = macData.getBytes();
        byte[] encodedMacBytes;
        String macCipher1;
        SecretKeySpec keyParameters1;
        Mac engine1;
        if(macVersion == 8) {
            macCipher1 = "";
            
            System.out.println("This is a boy");

            try {
                keyParameters1 = new SecretKeySpec(macKey, "HmacSHA1");
                engine1 = Mac.getInstance(keyParameters1.getAlgorithm());
                engine1.init(keyParameters1);
                encodedMacBytes = macData.getBytes();
                macBytes = engine1.doFinal(encodedMacBytes);
                macCipher1 = new String(Hex.encode(macBytes), "UTF-8");
            } catch (InvalidKeyException var11) {
                ;
            } catch (NoSuchAlgorithmException var12) {
                ;
            } catch (UnsupportedEncodingException var13) {
                ;
            }

            return macCipher1;
        } else if(macVersion == 12) {
            macCipher1 = "";
            System.out.println("This is a girl");
            try {
               /* keyParameters1 = new SecretKeySpec(macKey, "HmacSHA256");
                engine1 = Mac.getInstance(keyParameters1.getAlgorithm());
                engine1.init(keyParameters1);
                encodedMacBytes = macData.getBytes();
                macBytes = engine1.doFinal(encodedMacBytes);
                macCipher1 = new String(Hex.encode(macBytes), "UTF-8");*/
            
                	SecretKey macHmacSHA512Key = new SecretKeySpec(macKey,"HmacSHA512");
                	Mac mac = Mac.getInstance("HmacSHA512");
                	mac.init(macHmacSHA512Key);
                	byte[] result = mac.doFinal(macData.trim().getBytes());
                	macCipher1 = HexConverter.fromBinary2Hex(result);
               
                	//macVersion = 0;
                	//return newMac; 


  
                
                
            } catch (InvalidKeyException var14) {
                ;
            } catch (NoSuchAlgorithmException var15) {
                ;
            }

          return macCipher1;
        } else {
        	
        	System.out.println("This is a computer");
            CBCBlockCipherMac macCipher = new CBCBlockCipherMac(new DESedeEngine());
            DESedeParameters keyParameters = new DESedeParameters(macKey);
            DESedeEngine engine = new DESedeEngine();
            engine.init(true, keyParameters);
            macCipher.init(keyParameters);
            macCipher.update(macDataBytes, 0, macData.length());
            macCipher.doFinal(macBytes, 0);
            encodedMacBytes = Hex.encode(macBytes);
            String mac = new String(encodedMacBytes);
            return mac;
        }
    }
}
