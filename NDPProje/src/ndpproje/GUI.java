package ndpproje;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Locale;
import java.util.Scanner;

/*
Okan Tanrıverdi - 05190000042
Çağatay Çetinkol - 05190000026
Mehmet Gökberk Ayhan - 05190000098
*/

public class GUI implements ActionListener, KeyListener, WindowListener, DocumentListener {

    /*
    Menu bar, menu bar elemanları, yazım yapacagimiz text area, ana frameimiz,
    yazim yanlislarini gostermesi icin highlighter ve kaydirma islemi icin scrollpane
    nesneleri olusturuldu
    */
    private JMenuBar menuBar;
    private JMenu menu, menuAra;
    private JMenuItem menuItemAc, menuItemKaydet, menuItemKapat, menuItemDegistir, menuItemAra;
    private JTextArea textArea;
    private JFrame frame;
    private Highlighter highlighter;
    private JScrollPane scrollPane;
    private JTextField textFieldSearch, textFieldChange;
    private Font font;
    private JLabel label1, label2;
    private JMenuItem menuButtonGeri;
    private MemoryList memoryList;
    private String lastWord, importedStrings, documentName;

    /*GUI sinifinda arayuz icin kullanacagimiz bilesenlerin gercekletirilmesi yapilmistir*/

    GUI(){

        initializeComponents();
        /*
        Tepedeki menu barının olusturulmasi
        */
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription("Dosya aç, kapat, kaydet.");
        menuBar.add(menu);

        /*Dosya menusu altinda yeni dosya acmamizi saglayan dosya ac menusu olusturuldu ve menuye eklendi*/
        menuItemAc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_DOWN_MASK));
        menuItemAc.addActionListener(this);
        menu.add(menuItemAc);

        /*Kullanıcının text dosyasini depolaması için dosya kaydet menusu olusturuldu ve menuye eklendi*/
        menuItemKaydet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));    //kısayol ctrl+s
        menuItemKaydet.addActionListener(this);
        menu.add(menuItemKaydet);

        /*
        Kendimiz yazarak olusturdugumuz text dosyasini kapatmamizi ve kapatirken
        kaydedip kaydetmek istemedigimizi soran ActionListener destekli menu
        olusturuldu ve menuye eklendi
        */
        menuItemKapat.setMnemonic(KeyEvent.VK_D);
        menuItemKapat.addActionListener(this);
        menu.add(menuItemKapat);

        menuItemDegistir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
        menuItemDegistir.addActionListener(this);

        menuItemAra.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        menuItemAra.addActionListener(this);

        //Build second menu in the menu bar.
        menuAra.setMnemonic(KeyEvent.VK_K);

        menuAra.add(menuItemAra);
        menuAra.add(menuItemDegistir);

        menuButtonGeri.addActionListener(this);
        menuButtonGeri.setFocusable(false);
        menuButtonGeri.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK)); // kısayol CTRL+Z

        menuBar.add(menuAra);
        menuBar.add(menuButtonGeri);

        textArea.setFont(font); //font değiştirildi.
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addKeyListener(this);
        textArea.getDocument().addDocumentListener(this);
        textArea.setMargin(new Insets(5, 5, 0, 5));                     //Insets sınıfı margin belirlerken kenarlara olan uzaklığı
                                                                        // belirtmemiz için önemli. setMargin() kullanılmadan da
                                                                        // yapılabilirdi ancak uygulama açıldığında nereye yazı yazılacağı zor anlaşılıyordu.

        highlighter = textArea.getHighlighter();                        //hatalı kelimeleri belirtmek için highlighter interface ini kullanıyoruz

        frame.setSize(1240, 720);
        frame.setVisible(true);
        frame.setTitle(documentName);
        frame.add(scrollPane);
        frame.setJMenuBar(menuBar);


        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(this);


    }
    
    
    /*
    Yukarida olusturdugumuz gui elemanlarinin uygulama icinde yaratilmak uzere
    cagirilmasi
    */
    private void initializeComponents(){

        lastWord = null;
        documentName = "New Document";

        menuBar = new JMenuBar();

        menu = new JMenu("Dosya");

        menuItemAc = new JMenuItem("Dosya Aç", KeyEvent.VK_T);
        menuItemKaydet = new JMenuItem("Dosya Kaydet");
        menuItemKapat = new JMenuItem("Kapat");
        menuItemDegistir = new JMenuItem("Kelime ara ve değiştir");
        menuItemAra = new JMenuItem("Kelime ara");        //Kelime aramadan sorumlu JMenuItem nesne
        menuAra = new JMenu("Kelime Ara");
        menuButtonGeri = new JMenuItem("Geri Al");

        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea); // uzun metinlerde aşağı kaydırabilmek için
        frame = new JFrame();

        textFieldSearch = new JTextField(10);
        textFieldChange = new JTextField(10);

        font = new Font("Calibri", Font.PLAIN, 20);

        label1 = new JLabel("Bul:");
        label2 = new JLabel("Değiştir:");

        memoryList = new MemoryList(150);

        importedStrings = "";
    }

    private String getLastWord(){
        String word = null;
        try {
            int start = Utilities.getWordStart(textArea, textArea.getCaretPosition());
            int end = Utilities.getWordEnd(textArea, textArea.getCaretPosition());
            word = textArea.getDocument().getText(start, end - start);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return word;
    }
    
    public boolean singleTransposition(String strA, String strB){
        // İki string alan bunlarıın single position olup olmadığnı bakan fonksiyon
        // bu fonksiyonu kullanmadan önce kelime uzunluklarının aynı olduğu, kelimelerin birbirinden farklı olduğu kontrol ediliyor
        int errorCount = 0;
        boolean transposition = false;
        for (int i = 0; i < strA.length(); i++){

            if (strA.charAt(i) != strB.charAt(i)){
                errorCount++;               //i'nci indexteki harf de farkli ise hata sayisini arttiriyoruz

                if (i + 1 != strA.length()) {
                    if (strA.charAt(i + 1) == strB.charAt(i) && strA.charAt(i) == strB.charAt(i + 1)) {
                        transposition = true;
                    }
                }
            }
        }
        return transposition && errorCount <= 2;
    }

    // bu metodda bir önceki kelime kontrol ediliyor ve kelime listemizde yoksa hatalı kelimeyi belirtiyor
    private void controlWords(String word){

        try(Scanner s = new Scanner(new File("words.txt"))){

            int length = word.length();
            boolean shouldPaint = false, alternativeExists = false;
            String alternativeStr = null;
            while(s.hasNext()){
                String tempString = s.nextLine();

                if (tempString.length() == word.length()) {
                    //zamandan kazanmak icin ayni uzunluktaki kelimeleri kontrol ediyoruz
                    //aynı harfle başlıyorsa true değerini tutar
                    boolean startsWithSame = tempString.charAt(0) == word.toLowerCase().charAt(0);
                    //zamandan kazanmak icin ayni harfle baslayan ve ayni uzunluktakileri kontrol ediyoruz
                    //aynı harfle başlıyorsa veya ilk iki harfinin yeri hatalıysa devreye girer
                    if ((startsWithSame || tempString.charAt(0) == word.toLowerCase().charAt(1)) && !alternativeExists) {
                        if (singleTransposition(word, tempString)) { // doğruysa, single transposition hatasından kaynaklanan bir hata olduğu görmüş oluyoruz. kelimeyi düzeltmemiz gerekiyor
                            alternativeExists = true;
                            alternativeStr = tempString;
                        }
                    }

                    //kelimenin kendisinin varlığı kontrol edilir
                    if (startsWithSame) {

                        if (word.equalsIgnoreCase(tempString)) {
                            return;
                        }
                    }
                    else if (tempString.charAt(0) > word.toLowerCase().charAt(0)){
                        if (alternativeExists){
                            int caretPosition = textArea.getCaretPosition();
                            textArea.replaceRange(alternativeStr, caretPosition - length, caretPosition);

                            //kelime değiştirdiği listeye bildiriliyor. Böylece geri al seçeneğinde kelimeyi eski haline çevirebiliyoruz
                            memoryList.push(new ChangedText(caretPosition - length, alternativeStr + word, ChangedText.SWITCH_OPERATION));
                            return;
                        }
                        shouldPaint = true;
                    }
                }
            }

            // eğer kelimemiz bütün karşılaştırmalardan ve hata kontrolünden sonra halen sözlükte bulunamıyorsa altı çizilmeli
            if (shouldPaint){
                DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
                highlighter.addHighlight(textArea.getCaretPosition() - length, textArea.getCaretPosition() - 1, painter);
            }

        } catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(frame, "File Not Found.", "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (BadLocationException ex){
            JOptionPane.showMessageDialog(frame, "Bad Location Exception at " + ex.offsetRequested(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void openFile(){ // dosya açma işlemi

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(".")); //projenin bulunduğu klasörü açması için

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // sadece dosyaları text dosyası olarak okuyabileceğimizden
        int response = fileChooser.showOpenDialog(frame);

        if (response == JFileChooser.APPROVE_OPTION){
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());

            try {
                Scanner s = new Scanner(file);
                
                textArea.setText("");   // yeni dosya açtığımız için ekranı temizliyoruz.
                StringBuilder strFile = new StringBuilder();
                while(s.hasNext()){
                    strFile.append(s.nextLine()).append("\n");
                }

                textArea.setText(String.valueOf(strFile));
                importedStrings = textArea.getText();
                documentName = file.getName();
                frame.setTitle(documentName);

            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }

    private void saveFile(){ // dosya kaydetme işlemi

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(".")); //projenin bulunduğu klasörü açması için

        int response = fileChooser.showSaveDialog(frame);

        if (response == JFileChooser.APPROVE_OPTION){

            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());

            try {
                BufferedWriter write = new BufferedWriter(new FileWriter(file));
                write.write(textArea.getText());
                write.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void closeOption(){ // pencere kapatma işlemi

        //eğer bir dokuman açılmışsa ve üzerinde bir değişiklik yapılmışsa kapanmadan önce uyarı gösterir
        if (!importedStrings.equals(textArea.getText())){

            Object[] options = {"Kaydet", "Kaydetmeden Çık", "İptal"}; // butonların isimlerini değiştirdik
            int result = JOptionPane.showOptionDialog(null,"Kaydetmeden çıkmak istediğinize emin misiniz?", null, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);

            if (result == 0){ //kaydetme işlemi için
                saveFile();
            }else if (result == 1){ //kaydetmeden çıkma işlemi için
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSED)); // windowClosed() metodunu çağırıyor
            }
        }
        //eğer bir doküman açılmışsa ancak bir değişiklik yapılmamışsa, kapatıldığında uygulama uyarı çıkarmadan kapanır
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSED));

    }

    private void searchWordInTextArea(){
        String word = JOptionPane.showInputDialog(frame,"Please enter a word to search","Search for a word", JOptionPane.PLAIN_MESSAGE);
        
        if (word == null){          //kullanıcı bir kelime girmeden kapatırsa, NullPointerException vermemesi için kontrol ediliyor
            return;
        }
        String textAreaText = textArea.getText();

        int index = textAreaText.toUpperCase(Locale.US).indexOf(word.toUpperCase(Locale.US));   //küçük ve büyük harflerin duyarlılığı gözardı ediliyor
        if (index >= 0){  // eğer bulunduysa konumunu, bulunmadıysa -1 döndürecek
            JOptionPane.showMessageDialog(frame, "Found the word");
            textArea.setCaretPosition(index);       // bu metod, eğer kelime bulunursa, kelimenin bulunduğu yeri gösterecek
            return;
        }

        JOptionPane.showMessageDialog(frame, "Could not find the word", "Word Does Not Exist", JOptionPane.WARNING_MESSAGE);
    }
  
    //text area icinden kelime bulup degistirme metodu
    private void searchAndChange(){
        Object[] options = {"Bul ve değiştir", "İptal"};
        label1.setBounds(10, 0, 80, 20);
        label2.setBounds(10, 20, 80, 20);

        textFieldSearch.setBounds(80, 0, 100, 20);
        textFieldChange.setBounds(80, 20, 100, 20);

        UIManager.put("OptionPane.minimumSize",new Dimension(300,110)); // arayüzümüzdekki TextFieldların görünmesi için

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.add(label1);
        panel.add(textFieldSearch);
        panel.add(label2);
        panel.add(textFieldChange);

        int option = JOptionPane.showOptionDialog(frame,panel,"Bul ve Değiştir", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (option == 0){
            String stringToFind = textFieldSearch.getText();
            String stringToChange = textFieldChange.getText();

            int found = findAndReplace(stringToFind, stringToChange);

            Object[] options2 = {"Tamam"};

            if (found != 0){
                JOptionPane.showOptionDialog(null,"Found and replaced " + found, "Found", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options2, options2[0]);
            } else{
                JOptionPane.showConfirmDialog(null,"Could not find " + stringToFind, "Could not find", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        }

        UIManager.put("OptionPane.minimumSize", null);
    }

    private int findAndReplace(String strSearch, String strChange){
        //bul ve degistir metodu. istedigimiz kelimeyi istedigimiz kelimeye ceviriyor
        int index = 0;
        String text;
        int foundCount = 0;

        if (strSearch.equals(strChange)){

            JOptionPane.showConfirmDialog(frame,"Aradığınız kelime, değiştirmek istediğiniz kelimeyle aynı", "Hata", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
        }

        while(index != -1){
            text = textArea.getText();
            index = text.indexOf(strSearch, index);

            if (index != -1){
                foundCount++;
                textArea.replaceRange(strChange,index,index + strSearch.length());
            }
        }

        return foundCount;
    }

    //ctrl+Z kombinasyonuyla çalışan yazılanları veya silinenleri geri alan metod
    private void ctrlZAction() {

        ChangedText text = memoryList.pop();
        if(text == null){
            return;
        }

        if(text.getOperation() == ChangedText.ADD_OPERATION) {
            textArea.replaceRange("", text.getPosition(), text.getPosition() + 1);
        }
        else if (text.getOperation() == ChangedText.REMOVE_OPERATION){
            //textArea.append(text.getText());
            textArea.insert(text.getText(), text.getPosition());
            //textArea.replaceRange(text.getText(), text.getPosition(), text.getPosition() + text.getText().length());
        } else if(text.getOperation() == ChangedText.SWITCH_OPERATION){
            textArea.replaceRange(text.getText().substring(text.getText().length() / 2), text.getPosition(), text.getPosition() + text.getText().length() / 2);
        }
        // TODO: kullanıcı toplu mesaj sildiğinde ctrl+z çalışıyor ancak ondan önceki işleme ulaşılamıyoruz
    }
   
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == menuItemAra){
            searchWordInTextArea();
        }
        else if (e.getSource() == menuItemAc){
            openFile();
        }
        else if (e.getSource() == menuItemKaydet){
            saveFile();
        }
        else if (e.getSource() == menuItemKapat){
            closeOption();
        } else if (e.getSource() == menuItemDegistir){
            searchAndChange();
        } else if (e.getSource() == menuButtonGeri){
            ctrlZAction();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

        if(e.getKeyChar() == KeyEvent.VK_SPACE){
            controlWords(lastWord);
        }

        char chars = e.getKeyChar();

        //özel tuşlara basıldığında onları kaydetmeyerek hata vermesini önlüyoruz ayrıca kısayolları kullanabiliyoruz
        if (!e.isControlDown() && !e.isAltDown() && !e.isAltGraphDown()) {
            if (chars == '\b'){ // backspace karakteri için özel kontrol mekanizması. Çünkü if içinde kontrol edilmesine rağmen hata veriyordu
                return;
            }

            //diğer durumlarda harfleri listemize kaydediyoruz
            memoryList.push(new ChangedText(textArea.getCaretPosition(), String.valueOf(chars), ChangedText.ADD_OPERATION));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int position = textArea.getCaretPosition() - 1;

        String text;
        if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE){
            if (textArea.getSelectedText() != null){
                    text = textArea.getSelectedText();
                    int posStart = textArea.getSelectionStart(); // işaretçi metin seçili olduğunda güncellenmiyor. sona ekliyor. bunu önleyen kod
                    memoryList.push(new ChangedText(posStart, text, ChangedText.REMOVE_OPERATION));
            }
            else if(!textArea.getText().equals("")) {
                text = textArea.getText().substring(position, position + 1);
                memoryList.push(new ChangedText(position, text, ChangedText.REMOVE_OPERATION));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {    }
    @Override
    public void insertUpdate(DocumentEvent e) {
        lastWord = getLastWord();           //kelime yazıldığında buradaki kod çalışacak

    }
    @Override
    public void removeUpdate(DocumentEvent e) {}
    @Override
    public void changedUpdate(DocumentEvent e) {
        lastWord = getLastWord();
    }
    // WindowListener arayüzünü implement ettik. Bu arayüz X e bastığımızda programın bize onay mesajını
    // gösterip kullanıcıya sormamızı sağlıyor
    @Override
    public void windowOpened(WindowEvent e) {    }
    @Override
    public void windowClosing(WindowEvent e) {
        closeOption(); //onay mesajı
    }
    @Override
    public void windowClosed(WindowEvent e) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // X e basınca veya kapatma işleminde pencerenin kapanmasını sağlıyor
    }
    @Override
    public void windowIconified(WindowEvent e) {    }
    @Override
    public void windowDeiconified(WindowEvent e) {    }
    @Override
    public void windowActivated(WindowEvent e) {    }
    @Override
    public void windowDeactivated(WindowEvent e) {    }

    public static void main(String[] args) {
        new GUI();
    }
}