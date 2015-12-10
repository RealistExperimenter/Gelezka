package gelezka;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RefreshGelezo {

    private WebDriver driver;
    private String userPassword, userName, baseUrl;
    private List<String> listOfLinks;
    private WebElement temp;
    private boolean licensedUser;
    private int refreshRate = 7800;
    private int countToRefresh=0; //0 - all, 1- 1 board...
    private ProgrammWindow print;



   RefreshGelezo(ProgrammWindow winPanel){
        print=winPanel;
    }

    private void initialize(){
        print.addSting("========================================================");
        print.addSting("ВАЖНО!!! Не закрывайте сами окно браузера открытое программой, не кликайте ни на какие ссылки в нём, это может привести к зависанию, неправильной работе программы и непредвиденным последствиям. Программа всё делает автоматически!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        print.addSting("========================================================");
        String temp="";
        baseUrl = "http://gelezo.com.ua/";
        Initializer init=new Initializer(print);

        userName=init.getUserName();
        if (userName.length() <= 2) {
            print.addSting("");
            print.addSting("ВНИМАНИЕ!!!Имя пользователя слишком короткое, возможно оно указано не правильно. Проверьте настройки в my_settings.xml.");
            print.addSting("");
        }
        userPassword=init.getUserPassword();
        if (userPassword.length() <= 2) {
            print.addSting("");
            print.addSting("ВНИМАНИЕ!!!Длина пароля слишком короткая, возможно он указан не правильно. Проверьте настройки в my_settings.xml.");
            print.addSting("");
        }
        licensedUser=init.isLicensedUser();
        temp=licensedUser ? "полную версию программы":"пробную версию программы. Доступно только 1 объявление на 1 доске с периодом 10 часов. Чтобы использовать полную версию программы напишите письмо на gelezo.refresh@gmail.com и получите ключ";
        if (!licensedUser) {
            refreshRate=36000;
            countToRefresh=1;
        }
        print.addSting("");
        print.addSting("Сайт будет использоваться пользователем: "+userName);
        print.addSting("");
        print.addSting("Этот пользователь использует " + temp);

        createDriver();
        login();
        listOfLinks = init.getListOfLinks(driver);

        if (listOfLinks.size() == 0) {
            print.addSting("\n~~~~~~~~~~~~~~~~~~~~~~~~~~ОШИБКА~~~~~~~~~~~~~~~~~~~~~~~~~");
            print.addSting("У пользователя нету объявлений ни на одной из досок, пожалуйста добавьте хоть одно объявление и запустите приложение снова");
            driver.quit();
            waitSec(300);
            System.exit(100);
        }

        print.addSting("");

        print.addSting("У пользователя сообщения на "+listOfLinks.size()+" досках");

        if (7800-init.getlastTime()>0) {
            print.addSting("");
            print.addSting("Текущее время: "+getTime());
            print.addSting("Обновление начнется через: "+String.valueOf((7800-init.getlastTime())/60)+" минут");
            driver.quit();
            driver=null;
            waitSec(7800-init.getlastTime());
        }
    }


    private void createDriver(){
       // driver = new ChromeDriver();
          driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }




    public void refresh(){
        initialize();
        while(true) {
            if (driver == null) {
                createDriver();
                login();
            }

            refreshAllPosts(listOfLinks,countToRefresh);
            driver.quit();
            driver=null;
           print.addSting("Следующее обновление будет в "+correctingDateTime(LocalTime.now().plusMinutes(130).getHour())+":"+correctingDateTime(LocalTime.now().plusMinutes(130).getMinute()));
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
        try {
            if (!(driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td[1]/a[2]")).getText().matches("выйти"))) {
                print.addSting("");
                print.addSting("~~~~~~~~~~~~~~~~~~~~~~~~~~ОШИБКА~~~~~~~~~~~~~~~~~~~~~~~~~");
                print.addSting("Не могу залогиниться пользователем " + userName + ". Пожалуйста проверьте правильность пароля и имени пользователя в файле настроек и перезапустите приложение.");
                driver.quit();
                waitSec(300);
                System.exit(100);
            }
        } catch (Exception e){
            print.addSting("");
            print.addSting("~~~~~~~~~~~~~~~~~~~~~~~~~~ОШИБКА~~~~~~~~~~~~~~~~~~~~~~~~~");
            print.addSting("Не могу залогиниться пользователем " + userName + ". Пожалуйста проверьте правильность пароля и имени пользователя в файле настроек и перезапустите приложение.");
            driver.quit();
            waitSec(300);
            System.exit(100);
        }
    }


    private void refreshAllPosts(List<String> listOfLinks,int boardsCount){
        int boards = listOfLinks.size();
        if (boardsCount > 0) {boards=boardsCount;}
        print.addSting("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        print.addSting("");
       print.addSting("Время обновления: "+ getDate()+" "+getTime());
        for (int i = 0; i < boards; i++) {
            driver.get(listOfLinks.get(i));
            temp = driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[2]/td[1]/table/tbody/tr/td[2]/a"));
           print.addSting("");
           print.addSting("   Обновления сообщений на \""+temp.getText()+"\" доске");
            refreshPosts(boardsCount);
        }
       print.addSting("");
    }


    private void refreshPosts(int countOfPosts){

        List<WebElement> uname = driver.findElements(By.linkText("Обновить"));
        int errCount=uname.size();
        int count=errCount;

        if (errCount == 0) {
            print.addSting("");
           print.addSting("~~~~~~~~~~~~~~~~~~~~~~~~~~ОШИБКА~~~~~~~~~~~~~~~~~~~~~~~~~");
           print.addSting("    Ошибка, нету сообщений на доске "+temp.getText()+"\". Пожалуйста добавьте сообщения на доску");
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
           print.addSting("     "+(uname.size()-errCount)+" сообщений успешно обновлены");
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
               print.addSting("     "+count+" оптовых сообщений будет обновлено позже.");
            }else if (licensedUser == false) {
                print.addSting("Больше объявлений будет обновляться в полной версии программы.");
            } else print.addSting("     "+errCount+" сообщений будет обновлено позже.");
        }
    }

    private String getTime(){
        LocalTime currentTime= LocalTime.now();
        return  correctingDateTime(currentTime.getHour())+":"+correctingDateTime(currentTime.getMinute())+":"+correctingDateTime(currentTime.getSecond());
    }

    private String getDate(){
        LocalDate currentDate= LocalDate.now();
        return correctingDateTime(currentDate.getDayOfMonth())+"-"+correctingDateTime(currentDate.getMonth().getValue())+"-"+correctingDateTime(currentDate.getYear());
    }

    private String correctingDateTime(int input){
        if (input < 10) {
            return  "0"+String.valueOf(input);
        }
       return String.valueOf(input);
    }


    public void closeDriverStopProgramm(){
        if (driver != null) {
            driver.quit();
        }
        System.exit(0);
    }
}
