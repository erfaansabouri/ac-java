import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class TrieNode{
    TrieNode[] children = new TrieNode[26];
    boolean isEndOfWord;
    TrieNode(){
        isEndOfWord = false;
        for (int i = 0; i < 26; i++)
            children[i] = null;
    }

    public static void suggestionsRec(TrieNode root, String prefix, ArrayList<String> arr) {



        // found a string in Trie with the given prefix
        if (root.isEndOfWord) {
            arr.add(prefix);
        }

        // All children struct node pointers are NULL
        if (root.isLeaf())
            return;

        for (int i = 0; i < 26; i++) {

            if (root.children[i] != null) {
                // append current character to currPrefix string
                String new_prefix = prefix + (char)(i + 'a');

                // recur over the rest
               suggestionsRec(root.children[i], new_prefix, arr);

            }

        }


    }

    public boolean isLeaf() {
        for (int i = 0; i < 26; i++)
            if(children[i] != null) return false;
        return true;

    }
};


class Trie{
    TrieNode root = new TrieNode();

    Trie(String database) throws IOException {
        // read the database and construct the Trie
        FileInputStream fstream = new FileInputStream(database);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String word;
            while ((word = br.readLine()) != null)   {
                this.insert(word);
            }
            fstream.close();
    }

    private void insert(String key) {
        int level;
        int length = key.length();
        int index;

        TrieNode pCrawl = root;

        for (level = 0; level < length; level++)
        {
            index = key.charAt(level) - 'a';
            if (pCrawl.children[index] == null)
                pCrawl.children[index] = new TrieNode();

            pCrawl = pCrawl.children[index];
        }

        // mark last node as leaf
        pCrawl.isEndOfWord = true;
    }

     boolean search(String key) {
        int level;
        int length = key.length();
        int index;
        TrieNode pCrawl = root;
        key = key.replace(" " , "");
        key = key.replace("\t" , "");
        key = key.replace("\n" , "");
        for (level = 0; level < length; level++){
            index = key.charAt(level) - 'a';

            if (pCrawl.children[index] == null)
                return false;

            pCrawl = pCrawl.children[index];
        }

        return (pCrawl != null && pCrawl.isEndOfWord);
    }

    void display(TrieNode root , char[] str , int level){

        if(root.isEndOfWord)
            System.out.println(str);

        int i;
        for (i = 0; i < 26; i++) {
            if (root.children[i] != null) {
                str[level] = (char)(i + 'a');
                display(root.children[i], str, level + 1);
            }
        }
        str[level] = '\0';
    }

    void addWordFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()){
            String word = sc.nextLine();
            if (!this.search(word)){
                this.insert(word);
                //this.saveToDatabase(word);
            }

        }
    }

    private void saveToDatabase(String word) throws IOException {
        String database = "D:\\Source\\Java\\autocomplete\\src\\database.txt";
        FileWriter fw = new FileWriter(database,true);
        fw.write(word + "\r\n");
        fw.close();
    }

    void addSingleWord(String word) throws IOException {
        if (!this.search(word)){
            word = word.replace("\n" , "");
            word = word.replace("\t" , "");
            word = word.replace(" " , "");
            word = word.replace("null" , "");
            this.insert(word);
            System.out.println("New word added to Trie!");
        }
        else
            System.out.println("Word already exists!");
    }

    ArrayList<String> printSuggestions(TrieNode root , String word){


        TrieNode iter = root;
        int level;
        int n = word.length();
        for (level = 0; level < n; level++) {
            int index = word.charAt(level) - 'a';

            // no string in the Trie has this prefix
            if (iter.children[index] == null)
                return null;

            iter = iter.children[index];
        }




        // If prefix is present as a word.
        boolean isWord = iter.isEndOfWord;

        // If prefix is last node of tree (has no
        // children)
        boolean isLeaf = iter.isLeaf();

        // If prefix is present as a word, but
        // there is no subtree below the last
        // matching node.
        if (isWord && isLeaf) {
            System.out.println(word);
            ArrayList<String> arr = new ArrayList<>();
            arr.add(0,word);
            return arr;
        }

        if (!isLeaf) {
            String query = word;
            ArrayList<String> arr = new ArrayList<>();
            TrieNode.suggestionsRec(iter, query , arr);
            return arr;
        }

        return null;



    }

    TrieNode deleteWord(TrieNode root , String word , int depth){
        if (root == null)
            return null;

        if(depth == word.length()){
            // removal of given key

            if(root.isEndOfWord)
                root.isEndOfWord = false;

            if(root.isLeaf())
                root = null;

            return root;
        }

        int index = word.charAt(depth) - 'a';
        root.children[index] = deleteWord(root.children[index], word, depth + 1);

        // If root does not have any child (its only child got
        // deleted), and it is not end of another word.
        if (root.isLeaf() && !root.isEndOfWord) {
            root = null;
        }

        return root;
    }

    public void export(TrieNode root,String str , int level) throws IOException {

        if (root.isLeaf()) {
            str = str.substring(0,level);
            if(!alreadyExist(str))
                saveToDatabase(str);
        }

        int i;
        for (i = 0; i < 26; i++) {
            if (root.children[i] != null) {
                str = str.substring(0,level)+(char)(i+'a')+str.substring(level);
                export(root.children[i], str, level + 1);
            }
        }
    }

    boolean alreadyExist(String str) throws FileNotFoundException {
        File file = new File("D:\\Source\\Java\\autocomplete\\src\\database.txt");
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            String lineFromFile = scanner.nextLine();
            //System.out.println("### " + lineFromFile);
            if(lineFromFile.contains(str)) {

                return true;
            }
        }
        return false;
    }


}

/*
class KeyListenerTest implements KeyListener, ActionListener {

    JFrame frame;
    JTextField tf;
    JLabel lbl;
    JButton btn;

    public KeyListenerTest() {
        frame = new JFrame();
        lbl = new JLabel();
        tf = new JTextField(100);
        tf.addKeyListener(this);
        btn = new JButton("Clear");
        btn.addActionListener(this);
        JPanel panel = new JPanel();
        panel.add(tf);
        panel.add(btn);

        frame.setLayout(new BorderLayout());
        frame.add(lbl, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 100);
        frame.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        lbl.setText("You have typed "+ke.getKeyChar());
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        lbl.setText("You have pressed "+ke.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        lbl.setText("You have released "+ke.getKeyChar());
    }



    @Override
    public void actionPerformed(ActionEvent e) {

    }


    //new KeyListenerTest();
}
*/


class KeyListenerExample extends Frame implements KeyListener,ItemListener{
    Label counter;
    Label suggestions;
    TextArea area;
    String newWord;
    Trie myTrie;
    JButton close_button;
    JButton save_button;
    int caretPosition;
    JComboBox comboBox;
    KeyListenerExample(Trie thisTrie){
        myTrie = thisTrie;
        counter=new Label();
        suggestions=new Label();
        counter.setBounds(20,350,200,20);
        suggestions.setBounds(20,20,380,40);
        area=new TextArea();
        area.setBounds(20,100,300, 250);
        area.addKeyListener(this);
        comboBox = new JComboBox<String>();
        comboBox.addItemListener(this);



        close_button = new JButton("CLOSE");

        close_button.setBounds(20, 400, 100, 20);

        close_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });

        save_button = new JButton("SAVE");

        save_button.setBounds(130, 400, 100, 20);

        save_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    FileWriter myWriter = new FileWriter("saved.txt");
                    myWriter.write(area.getText());
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException ee) {
                    System.out.println("An error occurred.");
                    ee.printStackTrace();
                }
            }
        });


        comboBox.setBounds(350,100,120,40);
        add(counter);add(suggestions);add(area);add(close_button);add(save_button);add(comboBox);
        setSize(500,500);
        setLayout(null);
        setVisible(true);
    }
    public void keyPressed(KeyEvent e) {

        caretPosition = area.getCaretPosition();

    }

    public void itemStateChanged(ItemEvent e)
    {
        String selectedItem = comboBox.getSelectedItem().toString();
        // if the state combobox is changed
        if (e.getSource() == comboBox && selectedItem != null) {
            //String words[]=area.getText().split("\\s");
            area.setText(area.getText() + selectedItem);

        }
    }


    public void keyReleased(KeyEvent e) {
        String text=area.getText();
        String words[]=text.split("\\s");
        counter.setText("Words: "+words.length+" Characters:"+text.length());



        char c = e.getKeyChar();
        if(c != ' ')
            newWord= newWord + c;



        if (c == ' ' || c == '\n'){
            comboBox.removeAllItems();


            if(newWord.length() != 0 && newWord.length() != 1){
                try {
                    newWord = newWord.replace(" " , "");
                    newWord = newWord.replace("\t" , "");
                    newWord = newWord.replace("\n" , "");
                    myTrie.addSingleWord(newWord);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


          newWord = "";

        }
        if(c == '\t'){
            if(myTrie.printSuggestions(myTrie.root,words[words.length-1])!=null){
                int sugsSize = myTrie.printSuggestions(myTrie.root,words[words.length-1]).size();
                String allSugs[] = new String[sugsSize];

                // ArrayList to Array Conversion
                for (int j = 0; j < sugsSize; j++) {

                    // Assign each value to String array
                    allSugs[j] = myTrie.printSuggestions(myTrie.root,words[words.length-1]).get(j);
                }

                for(int i = 0 ; i<sugsSize ; i++)
                    comboBox.addItem(allSugs[i]);


                suggestions.setText("Suggestions : " + Arrays.toString(allSugs));
            }

            area.setText(area.getText().replaceAll("\t",""));
            area.setCaretPosition(caretPosition);
        }




    }
    public void keyTyped(KeyEvent e) {}


}


public class Main{




    public static void main(String args[]) throws IOException, InterruptedException {

        String database = "D:\\Source\\Java\\autocomplete\\src\\database.txt";
        Trie myTrie = new Trie(database);

        String main_menu_text = "Please select an option :\n" +
                                "1.Open Editor" + "\n" +
                                "2.Import File" + "\n" +
                                "3.Add Word" + "\n" +
                                "4.Delete Word" + "\n" +
                                "5.Dictionary" + "\n" +
                                "6.Export Dictionary" + "\n" +
                                ">>";


        ArrayList<String> arr;
        arr = myTrie.printSuggestions(myTrie.root , "s");
        System.out.println(arr);
        while (true) {
            System.out.println(main_menu_text);
            Scanner scanner = new Scanner(System.in);  // Create a Scanner object
            int choice = scanner.nextInt();

            switch (choice){
                case 1:
                    //new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    //new KeyListenerTester("Key Listener Tester");
                    new KeyListenerExample(myTrie);

                    break;
                case 2:
                    Scanner filePathScanner = new Scanner(System.in);
                    System.out.println("Enter File Path :");
                    String filePath = filePathScanner.nextLine();
                    myTrie.addWordFromFile(filePath);
                    System.out.println("Imported successfully!\n");
                    break;
                case 3:
                    Scanner wordScanner = new Scanner(System.in);
                    System.out.println("Enter a word :");
                    String word = wordScanner.nextLine();
                    myTrie.addSingleWord(word);
                    System.out.println("Word " + word +" added successfully!\n");
                    break;
                case 4:
                    Scanner wordToDeleteScanner = new Scanner(System.in);
                    System.out.println("Enter a word :");
                    String wordToDelete = wordToDeleteScanner.nextLine();
                    myTrie.deleteWord(myTrie.root,wordToDelete,0);
                    System.out.println("Word " + wordToDelete +" deleted successfully!\n");
                    break;
                case 5:
                    System.out.println("Dictionary Content : ");
                    char[] str = new char[30];
                    myTrie.display(myTrie.root , str , 0);
                    break;
                case 6:
                    String strWord = "";
                    myTrie.export(myTrie.root, strWord ,0);
                    break;





            }
        }
/*


        myTrie.addSingleWord("soosan");
        myTrie.addWordFromFile("D:\\Source\\Java\\autocomplete\\src\\data.txt");
        char[] str = new char[30];
        myTrie.display(myTrie.root ,str , 0);



   */



    }
}
