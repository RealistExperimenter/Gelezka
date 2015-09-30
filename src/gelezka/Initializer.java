package gelezka;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.time.LocalTime;

/**
 * Created by Coder on 28-Sep-15.
 */
public class Initializer {
    private String userPassword, userName, userKey;
    private List<String> listOfLinks=new LinkedList<String>();
    private WebDriver driver;
    private int lastTime;

    Initializer(){
        ReadXML data = new ReadXML();
        userName=data.getUserName();
        userPassword=data.getUserPassword();
        userKey=data.getUserKey();
    }


    public String getUserName(){
        return userName;
    }


    public String getUserPassword(){
        return userPassword;
    }


    public boolean isLicensedUser(){
        Key checkUser=new Key();

       if (userKey.length()!=128||userKey==null) { System.out.println();
            System.out.println("Problem with your UserKey: you will use Trial version");
            return false;
        }
        return checkUser.decodeKey(userKey).matches(userName);
    }


    public List<String> getListOfLinks(WebDriver driver){  this.driver=driver;
        listOfLinks.addAll(listOfBoards());
        return listOfLinks;
    }


    public int getlastTime(){
        return lastTime;
    }


    private Set<String> listOfBoards(){
        Set<String> boards=new LinkedHashSet<String>();
        boolean haveElements=true;
        int count=1;
        WebElement temp;

        driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td[1]/a[1]/b")).click();
     //   driver.findElements(By.className("titlPost"));
        while (haveElements){
            try {
                temp=driver.findElement(By.xpath("/html/body/div/center/table[1]/tbody/tr/td[3]/a["+count+"]"));
                boards.add(temp.getAttribute("href"));
            } catch (Exception e) {haveElements=false;}
            count++;
        }
        lastTime= lastTimeDiff();
        return boards;
        }


    private int lastTimeDiff(){
        List<WebElement> time = driver.findElements(By.className("titlPost"));
        List<String> times= new LinkedList<String>();

        for(WebElement el:time){
            times.add(getTime(el.getText()));
        }

        int[] seconds=new int[times.size()];

        for (int i = 0; i < times.size(); i++) {
           seconds[i]=LocalTime.now().toSecondOfDay()-LocalTime.parse(times.get(i)).toSecondOfDay();
            if (LocalTime.now().toSecondOfDay()-LocalTime.parse(times.get(i)).toSecondOfDay()<-78600)  seconds[i]=LocalTime.now().toSecondOfDay()-LocalTime.parse(times.get(i)).toSecondOfDay()+86400;
            if (LocalTime.now().toSecondOfDay()-LocalTime.parse(times.get(i)).toSecondOfDay()>-78600&&LocalTime.now().toSecondOfDay()-LocalTime.parse(times.get(i)).toSecondOfDay()<0)  seconds[i]=7800;
        }

        int max=seconds[0];

        for (int i = 1; i < times.size(); i++) {
            if (max > seconds[i]) {
                max = seconds[i];
            }
        }
        return max;
    }


    private String getTime(String text){
        char[] string = text.toCharArray();
        int index=0;

        for (int i = 0; string[i]!=':'&&i<string.length-1; i++) {
            index=i+1;
        }

        char[] temp={string[index-2],string[index-1],string[index],string[index+1],string[index+2]};
        return String.valueOf(temp);
    }
}

