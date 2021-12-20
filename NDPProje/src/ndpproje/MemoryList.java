package ndpproje;

public class MemoryList {
    // bu veri yapısı, stack ve linked list mantığını barındırıyor. eleman sayısı maxSize dan
    //fazla olduğunda en alttaki eleman silinecek, geri alma işlemini uyguladığımızda ise
    // en üstten eleman silecek. Bu veri yapısı yapı itibariyle stack ve linked liste benzeridir.

    private final int maxSize;
    private Node top;
    private Node bottom;
    private int count;

    public MemoryList(int maxSize){

        this.maxSize = maxSize;
        top = null;
        bottom = null;
        count = 0;
    }

    // eleman ekleme metodu
    public void push(ChangedText data){
        if(data == null){
            return;
        }
        Node node = new Node(data);
        Node temp;

        if (isFull()){
            // eleman sınırını doldurduk. En alttaki elemanı atıp en üste eleman ekleyeceğiz
            bottom = bottom.next;
            bottom.previous = null;
            count--;
        }
        // eleman ekleme

        if(top == null){
            top = node;
            bottom = top;
            return;
        }

        temp = top;
        top = node;
        temp.next = top;
        top.previous = temp;
        count++;
    }

    // en üstten eleman silme metodu (geri alma)
    public ChangedText pop(){

        if (isEmpty()){
            return null;
        }
        Node temp = top;
        if(top.previous != null) { // 1 eleman kaldığı durumlar harici
            top = top.previous;
            top.next = null;
        } else{
            top = null;
        }

        return temp.data;
    }

    public boolean isEmpty(){
        return top == null;
    }

    public boolean isFull(){
        return count == maxSize;
    }


    class Node{

        public ChangedText data;
        public Node previous;
        public Node next;

        public Node(ChangedText data){
            this.data = data;
            next = null;
            previous = null;
        }

    }

}