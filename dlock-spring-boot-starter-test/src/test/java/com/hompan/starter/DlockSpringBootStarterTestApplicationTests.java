package com.hompan.starter;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DlockSpringBootStarterTestApplication.class)
class DlockSpringBootStarterTestApplicationTests {
    @Autowired
    TestService testService;

    @Test
    public void reOrderArray() {
        //in-place
        int [] array = new int[]{1,3,5,7,2,4,6};
        int pEven = -1;
        int pOdd = -1;
        boolean isFirst = true; //1 3 5 7 2 4 6
        for(int i=0;i<array.length;i++){
            if(isFirst && array[i]%2==0){
                pEven = i;
                isFirst = false;
            }
            if(pEven>=0 && i>pEven && array[i]%2==1){
                pOdd = i;
                System.out.println(pEven);
                System.out.println(pOdd);
                int temp = array[pOdd];
                for(int j=pOdd-1; j>=pEven; j--){
                    array[j+1] = array[j];
                }
                array[pEven++] = temp;
            }
            for (int j = 0; j < array.length; j++) {
                System.out.print(array[j]);
            }
            System.out.println("=================");
        }
    }

    @Test
    public void testLock() throws NoSuchMethodException {
        System.out.println("*****************************");
        try{
            System.out.println(testService.myTurn("balabala",0));
        }catch (Exception e){
            System.out.println(e.getMessage() + " 锁失败");
        }

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}