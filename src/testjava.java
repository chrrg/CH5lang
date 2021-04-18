import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class testjava {
    Integer a;

    static void a(){
        var s=100;
    }
}
class aa{
    private static class SingletonHolder {
        private static final aa INSTANCE = new aa();
    }

    public static aa getInstance(){
//        if(s==null){
//            synchronized (aa.class){
//                if(s==null)s=new aa();
//            }
//        }
        return SingletonHolder.INSTANCE;
    }

}
class a22{
    @Override
    public String toString(){
        return "1";
    }
}
class a23 extends a22{
    @Override
    public String toString(){
        return "2";
    }
}
//public static int testjava=100;
class asss{
    <T> String a(T value){
        return value.getClass().getSimpleName();
    }
    public static void main(String[] args){
        a22 b=new a23();
        System.out.println(b.toString());

        if(1==1)return;
        Object a=new Object();
        Thread t1=new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(t1.isAlive());

        }).start();

//        ConcurrentHashMap a=new ConcurrentHashMap<String,String>();
//        a.put("1","1");
        if(1==1)return;

        asss ss=new asss();
        ss.a.add(100);
//        Arrays.
//        ss.b=
//        Iterator<Integer> sss=ss.a.listIterator();
        Iterator<Integer> sss=ss.b;


        while(sss.hasNext()){
            System.out.println(sss.next());
//            sss.add(100);

        }



        for(var i : ss.a){
            System.out.println(i);
        }
    }

    ArrayList<Integer> a=new ArrayList<Integer>();
    ListIterator<Integer> b=a.listIterator();



//    public int a=100;
//    private int b=10;
//    public int c=10;
//    protected int d=100;

//    void a(Pair<Integer,Void> s){
//        s.getFirst()
//    }
//    Integer test(){
//        Integer aa= 1000;
//        a(aa);
//        return aa;
//    }
}