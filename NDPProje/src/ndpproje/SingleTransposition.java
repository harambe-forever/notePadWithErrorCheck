package ndpproje;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SingleTransposition {
    public String string1;
    public String string2;
    public boolean display;
    
    public SingleTransposition(){
    }
    
    public SingleTransposition(String string1, String string2){
        this.string1 = string1;
        this.string2 = string2;
    }
    
    public void setDisplay(boolean display){
        this.display = display;
    }
    
    public String getDisplay(){
        return ("Single transposition occurs: " + display);
    }
       
    public boolean singleTransposition(String strA, String strB){
        // İki string alan bunlarıın single position olup olmadığnı bakan fonksiyon
        // bu fonksiyonu kullanmadan önce kelime uzunluklarının aynı olduğu, kelimelerin birbirinden farklı olduğu kontrol ediliyor
        int errorCount = 0;
        boolean transposition = false;
        for (int i = 0; i < strA.length(); i++){
            if (strA.charAt(i) != strB.charAt(i)){
                errorCount++;

                if (i + 1 != strA.length()) {
                    if (strA.charAt(i + 1) == strB.charAt(i) && strA.charAt(i) == strB.charAt(i + 1)) {
                        transposition = true;
                        setDisplay(transposition);
                    }
                }
            }
        }
        setDisplay(transposition);
        return transposition && errorCount <= 2;
    }
    
    
    public boolean controlWords(String word) throws FileNotFoundException{
        try(Scanner s = new Scanner(new File("words.txt"))){
            int length = word.length();
            boolean shouldPaint = false, alternativeExists = false;
            String alternativeStr = null;
            while(s.hasNext()){
                String tempString = s.nextLine();
                if (tempString.length() == word.length()) {
                    //aynı harfle başlıyorsa true değerini tutar
                    boolean startsWithSame = tempString.charAt(0) == word.toLowerCase().charAt(0);
                    //aynı harfle başlıyorsa veya ilk iki harfinin yeri hatalıysa devreye girer
                    if ((startsWithSame || tempString.charAt(0) == word.toLowerCase().charAt(1)) && !alternativeExists) {
                        if (singleTransposition(word, tempString)) { // doğruysa, single transposition hatasından kaynaklanan bir hata olduğu görmüş oluyoruz. kelimeyi düzeltmemiz gerekiyor
                            alternativeExists = true;
                            alternativeStr = tempString;
                            System.out.println(tempString);
                            return false;
                        }
                    }
                    //kelimenin kendisinin varlığı kontrol edilir
                    if (startsWithSame) {

                        if (word.equalsIgnoreCase(tempString)) {
                            return true;
                        }
                    }
                    else if (tempString.charAt(0) > word.toLowerCase().charAt(0)){
                        if (alternativeExists){
                            return false;
                        }
                        shouldPaint = true;
                        return false;
                    }
                }
            }
        }   
        return false;
    }
}
