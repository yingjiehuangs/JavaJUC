package day9.blockQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class MyResource{
    private volatile boolean FLAGS=true;//默认并列
    private AtomicInteger atomicInteger=new AtomicInteger();
    BlockingQueue<String> blockingQueue=null;

    public MyResource(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println(blockingQueue.getClass().getName());
    }

    public void myProd() throws InterruptedException {
        String data=null;
        boolean retValue;
        while(FLAGS){
            data=atomicInteger.incrementAndGet()+"";
            retValue=blockingQueue.offer(data,2, TimeUnit.SECONDS);
            if(retValue){
                System.out.println(Thread.currentThread().getName()+"\t插入队列"+data +"成功");
            }
            else {
                System.out.println(Thread.currentThread().getName() + "\t插入队列" + data + "失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }

    }
    public void myConsumer() throws InterruptedException {
        String result=null;
        while(FLAGS){
            result=blockingQueue.poll(2,TimeUnit.SECONDS);
            if(result==null||result.equalsIgnoreCase("")){
                FLAGS=false;
                System.out.println(Thread.currentThread().getName()+"\t消费队列失败\t");
                return;
            }else if(result!=null){

            System.out.println(Thread.currentThread().getName()+"\t消费队列"+result+"成功");
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }
    public void stop(){
        this.FLAGS=false;
        System.out.println("大老板叫停");
    }
}

/*
*
* */
public class BlockQueueq1 {
    public static void main(String[] args) {
        MyResource myResource=new MyResource(new ArrayBlockingQueue<>(10));

        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"\t生产线程启动");
                myResource.myProd();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"Prod").start();

        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"\t消费线程启动");
                myResource.myConsumer();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"Consumer").start();


        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myResource.stop();
    }
}
