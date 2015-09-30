package gelezka;
/**
 * Created by Coder on 21-Sep-15.
 */

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RefreshGelezoTest {

    private WebDriver driver;
    private String userPassword, userName, baseUrl;
    private List<String> listOfLinks;
    private WebElement temp;
    private boolean licensedUser;
    private int refreshRate = 7800;
    private int countToRefresh=0; //0 - all, 1- 1 board...


    @Before
    public void setUp() throws Exception {
        String temp="";
        baseUrl = "http://gelezo.com.ua/";
        Initializer init=new Initializer();

        userName=init.getUserName();
        userPassword=init.getUserPassword();
        licensedUser=init.isLicensedUser();
        temp=licensedUser ? "Full version":"Trial version. Only 1 post on 1 board will be refreshed every 10 hour";
        if (!licensedUser) {
            refreshRate=36000;
            countToRefresh=1;
        }
        System.out.println();
        System.out.println("Site will be used with the user: "+userName);
        System.out.println("This user uses: " + temp);
        createDriver();
        login();
        listOfLinks=init.getListOfLinks(driver);

        System.out.println();

        System.out.println("User have posts on "+listOfLinks.size()+" boards");

        if (7800-init.getlastTime()>0) {
            System.out.println("refresh starts in: "+String.valueOf((7800-init.getlastTime())/60)+" minutes");
            driver.quit();
            waitSec(7800-init.getlastTime());
        }
    }


    private void createDriver(){
        driver = new ChromeDriver();
        //   driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
  //  @Test


    
    @Test
    public void refresh(){
        while(true) {
            if (driver == null) {
                createDriver();
                System.out.println("create driver");
                login();
            }

              refreshAllPosts(listOfLinks,countToRefresh);
              driver.quit();
              System.out.println("Next refresh will be on "+LocalTime.now().plusMinutes(130).getHour()+":"+LocalTime.now().plusMinutes(130).getMinute());
              waitSec(refreshRate);
       }
    }


    private void waitSec(int sec){
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {System.out.println("Error under sleep");}
    }


    private void login(){

        WebElement pass,uname,signIn;

        driver.get(baseUrl);

        uname = driver.findElement(By.name("UserName"));

        pass = driver.findElement(By.name("PassWord"));

        signIn = driver.findElement(By.name("submit"));

        uname.sendKeys(userName);
        pass.sendKeys(userPassword);
        signIn.click();

        if (!(driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td[1]/a[1]/b")).getText().matches(userName))) {System.out.println("Can't login with the " + userName + " user");}
    }


    private void refreshAllPosts(List<String> listOfLinks,int boardsCount){
        int boards = listOfLinks.size();
        if (boardsCount > 0) {boards=boardsCount;}

        System.out.println("Action time: "+ LocalDate.now().toString()+" "+LocalTime.now().getHour()+":"+LocalTime.now().getMinute()+":"+LocalTime.now().getSecond());
        for (int i = 0; i < boards; i++) {
            driver.get(listOfLinks.get(i));
            temp = driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[2]/td[1]/table/tbody/tr/td[2]/a"));
            System.out.println("");
            System.out.println("   Refreshing posts on \""+temp.getText()+"\" board");
            refreshPosts(boardsCount);
        }
        System.out.println();
    }


    private void refreshPosts(int countOfPosts){

        List<WebElement> uname = driver.findElements(By.linkText("Обновить"));
        int errCount=uname.size();
        int count=errCount;

        if (errCount == 0) {
            System.out.println("    Something wrong. There is no any post on the "+temp.getText()+"\" board. Please add some or delete a board link from the my_settings.xml file.");
            return;
        }
        if (countOfPosts > 0) {count=countOfPosts;}

        for (int i = 0; i != count; i++) {
           final List<WebElement> names = driver.findElements(By.linkText("Обновить"));
            names.get(i).click();
            waitSec(1);
            if (driver.findElements(By.xpath("/html/body/div/table[3]/tbody/tr/td/font/h2")).size() == 0) errCount--;
            driver.navigate().back();
            waitSec(1);
        }
        if (uname.size()-errCount != 0) {
            System.out.println("     "+(uname.size()-errCount)+" posts successfully refreshed");
        }

        if (errCount != 0) {
            count=0;
            uname=driver.findElements(By.className("TextPost"));
            for (int i = 0; i != uname.size(); i++) {
                if (uname.get(i).getText().startsWith("[o]")) {
                    count++;
                }
            }
            if (count !=0) {
                System.out.println("     "+count+" wholesale posts will be refreshed under the next refresh session");
            }else  System.out.println("     "+errCount+" posts will be refreshed later");
        }
    }


    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

}
