package com.myhome.test;

import java.util.concurrent.TimeUnit;

public class ThreadTest {
    private static boolean stopped = true;
    public static void main( String[] args ) throws InterruptedException {

        new Thread( new D() ).start();
        new Thread( new C() ).start();
        TimeUnit.SECONDS.sleep( 5 );

        System.out.println( "Done" );
        stopped = false;
    }
    public static class C implements Runnable {

        @Override
        public void run() {
            while( !stopped ) {
            }
            System.out.println( "Out" );
        }
    }

    static class D implements Runnable {
        @Override
        public void run() {
            while( true ) {
                if( stopped ) {
                    break;
                }
            }
            System.out.println( "Out" );
        }
    }
}
