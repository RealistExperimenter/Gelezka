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
        temp=licensedUser ? "Полную версию программы":"Пробную версию программы. Доступно только 1 объявление на 1 доске с периодом 10 часов";
        if (!licensedUser) {
            refreshRate=36000;
            countToRefresh=1;
        }
        System.out.println();
        System.out.println("Сайт будет использоваться пользователем: "+userName);
        System.out.println("Этот пользователь использует: " + temp);
        createDriver();
        login();
        listOfLinks=init.getListOfLinks(driver);

        System.out.println();

        System.out.println("У пользователя сообщения на "+listOfLinks.size()+" досках");

        if (7800-init.getlastTime()>0) {
            System.out.println("Обновление начнется через: "+String.valueOf((7800-init.getlastTime())/60)+" минут");
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
                login();
            }

              refreshAllPosts(listOfLinks,countToRefresh);
              driver.quit();
              System.out.println("Следующее обновление будет в "+LocalTime.now().plusMinutes(130).getHour()+":"+LocalTime.now().plusMinutes(130).getMinute());
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

        if (!(driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td[1]/a[2]")).getText().matches("выйти"))) {System.out.println("Не могу залогиниться пользователем " + userName + ". Пожалуйста проверьте правильность пароля и имени пользователя в файле настроек.");}
    }


    private void refreshAllPosts(List<String> listOfLinks,int boardsCount){
        int boards = listOfLinks.size();
        if (boardsCount > 0) {boards=boardsCount;}

        System.out.println("Время обновления: "+ LocalDate.now().toString()+" "+LocalTime.now().getHour()+":"+LocalTime.now().getMinute()+":"+LocalTime.now().getSecond());
        for (int i = 0; i < boards; i++) {
            driver.get(listOfLinks.get(i));
            temp = driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[2]/td[1]/table/tbody/tr/td[2]/a"));
            System.out.println("");
            System.out.println("   Обновления сообщений на \""+temp.getText()+"\" доске");
            refreshPosts(boardsCount);
        }
        System.out.println();
    }


    private void refreshPosts(int countOfPosts){

        List<WebElement> uname = driver.findElements(By.linkText("Обновить"));
        int errCount=uname.size();
        int count=errCount;

        if (errCount == 0) {
            System.out.println("    Ошибка, нету сообщений на доске "+temp.getText()+"\". Пожалуйста добавьте сообщения на доску");
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
            System.out.println("     "+(uname.size()-errCount)+" сообщений успешно обновлены");
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
                System.out.println("     "+count+" оптовых сообщений будет обновлено позже");
            }else  System.out.println("     "+errCount+" сообщений будет обновлено позже");
        }
    }


    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

}
