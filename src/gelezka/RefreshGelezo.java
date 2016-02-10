package gelezka;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RefreshGelezo implements Runnable {

    private WebDriver driver;
    private String userPassword, userName, baseUrl;
    private List<String> listOfLinks;
    private WebElement temp;
    private boolean licensedUser;
    private int refreshRate = 14400; // 4 hours
    private int countToRefresh=0; //0 - all, 1- 1 board...
    private ProgrammWindow print;
    private PostsStorage postStorage;
    private boolean haveNewMessages = false;
    private int countOfNewMessages=0;
    private int countOfMessages=10000;
    private LinkedList<String> comments=new LinkedList<String>();




    RefreshGelezo(ProgrammWindow winPanel){
        print=winPanel;
    }

    private void initialize(){

        String temp="";
        baseUrl = "http://gelezo.com.ua/";
        Initializer init=new Initializer(print);
        postStorage=init.getPostStorage();
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

        int maxTime=postStorage.getMaxTime();
        if (7800-maxTime>0) {
            print.addSting("");
            print.addSting("Текущее время: "+getTime());
            print.addSting("Обновление начнется через: "+String.valueOf((7800-maxTime)/60)+" минут");
            driver.quit();
            driver=null;
            waitSec(7800-maxTime);
        }
    }


    private void createDriver(){
        // driver = new ChromeDriver();
       // driver = new FirefoxDriver();
        driver=new HtmlUnitDriver();
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

            if (haveNewMessages) new Thread(new  Message(print)).start();
            if (comments.size()!=0) for (String x:comments) new Thread(new  Message(print,x)).start();
            haveNewMessages=false;
            comments.clear();
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
        try {
        driver.get(baseUrl);

        uname = driver.findElement(By.name("UserName"));

        pass = driver.findElement(By.name("PassWord"));

        signIn = driver.findElement(By.name("submit"));

        uname.sendKeys(userName);
        pass.sendKeys(userPassword);
        signIn.click();

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
            print.addSting("   Обновлениe сообщений на \""+temp.getText()+"\" доске...");
            refreshPosts(boardsCount, listOfLinks.get(i));
        }

        String text;
        String[] result;
        text=driver.findElements(By.xpath("html/body/div[1]/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td[2]/table/tbody/tr/td/div")).get(0).getAttribute("title");
        result=text.split("\\D+");
        int count=Integer.parseInt(result[1]);
        if(((count>0)&&(count>countOfNewMessages)||Integer.parseInt(result[2])>countOfMessages))
            haveNewMessages=true; countOfNewMessages=count; countOfMessages=Integer.parseInt(result[2]);



        print.addSting("");
    }


    private void refreshPosts(int countOfPosts, String board){

        List<PostInfo> list = postStorage.getPosts(Identifiers.BOARD_ADDRESS,board);
        List<WebElement> refreshLinks = driver.findElements(By.linkText("Обновить"));
        List<PostInfo> sortedList=new LinkedList<PostInfo>();

        if (list.size()!=refreshLinks.size()){
            print.addSting("***********************************************************");
            print.addSting("~~~~~~~~~~~~~~~~~~~~~~~~~~Внимание~~~~~~~~~~~~~~~~~~~~~~~~~");
            print.addSting("   Изменилось количесво сообщений на досках. Запускается переинициализация программы.");
            initialize();
        }

        List<WebElement> views = driver.findElements(By.partialLinkText("просмотров:"));
        List<WebElement> messages = driver.findElements(By.partialLinkText("ответов:"));
        List<WebElement> id = driver.findElements(By.partialLinkText("подробно"));
        String[] temp3;
        String viewCount, messCount, messageId;


        for (int i = 0; i < refreshLinks.size(); i++) {
            viewCount=views.get(i).getText();
            temp3= viewCount.split(":");
            viewCount=temp3[temp3.length-1];

            messCount=messages.get(i).getText();
            temp3= messCount.split(":");
            messCount=temp3[temp3.length-1];

            messageId=id.get(i).getAttribute("href");
            temp3=messageId.split("/");
            messageId=temp3[temp3.length-1];

            try{
            postStorage.writeChanges(Identifiers.ID, String.valueOf(messageId), Identifiers.POST_VIEWS_COUNT, viewCount);
            postStorage.writeChanges(Identifiers.ID, String.valueOf(messageId), Identifiers.OLD_POST_MESSAGES_COUNT, String.valueOf(postStorage.getPosts(Identifiers.ID, String.valueOf(messageId)).get(0).getPostMessagesCount()));
            postStorage.writeChanges(Identifiers.ID, String.valueOf(messageId), Identifiers.POST_MESSAGES_COUNT, messCount);
            }
            catch (Exception e){print.addSting("В связи с удалением объявлений в процессе работы программы, показания счетчиков могут быть не верными. Рекомендуеся перезапустить программу.");}

            sortedList.add(postStorage.getPosts(Identifiers.ID, String.valueOf(messageId)).get(0));
        }

        list = sortedList;

          int count=refreshLinks.size();

        if (count == 0) {
            print.addSting("");
            print.addSting("~~~~~~~~~~~~~~~~~~~~~~~~~~ОШИБКА~~~~~~~~~~~~~~~~~~~~~~~~~");
            print.addSting("    Ошибка, нету сообщений на доске "+temp.getText()+"\". Пожалуйста добавьте сообщения на доску");
            return;
        }
        if (countOfPosts > 0) {count=countOfPosts;}

        for (int i = 0; i != count; i++) {
            final List<WebElement> names = driver.findElements(By.linkText("Обновить"));
            String postText;
            names.get(i).click();
            waitSec(1);
            if (driver.findElements(By.xpath("/html/body/div/table[3]/tbody/tr/td/font/h2")).size() == 0 && !driver.getTitle().matches("Gelezo ERRORS")) {
                list.get(i).setRefreshStatus(true);

            } else list.get(i).setRefreshStatus(false);

            temp3=list.get(i).getPostText().split("\\n");
            postText= temp3[0].toCharArray().length<50? temp3[0]:temp3[0].substring(0, 49)+"...";




            print.addSting("");
            print.addSting("      Объявление: "+postText);
            print.addSting("\t Статус: "+(list.get(i).getRefreshStatus() ? "Обновлено" : "Не обновлено"+(list.get(i).getPostCharacter() ? "\n Это оптовое сообщение, оно будет одновлено позже" : "")));
            print.addSting("\t Просмотров: "+list.get(i).getPostViewsCount()+"\n\t Сообщений под объявлением: "+list.get(i).getPostMessagesCount()+
                    ((list.get(i).getPostMessagesCount()- list.get(i).getOldPostMessagesCount())!=0 ?   " ("+(list.get(i).getPostMessagesCount()- list.get(i).getOldPostMessagesCount())+" новое сообщение)" : ""));

            driver.navigate().back();


            waitSec(1);
            if ((list.get(i).getPostMessagesCount()- list.get(i).getOldPostMessagesCount())!=0) comments.add(postText);
        }

            if (!licensedUser){
                print.addSting("+==================================================================+");
                print.addSting("|  Больше объявлений будет обновляться в полной версии программы.  |");
                print.addSting("|                Пишите на gelezo.refresh@gmail.com                |");
                print.addSting("+==================================================================+");
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
            try {
                driver.quit();
            }
            catch (UnreachableBrowserException e){System.out.println("Browser already closed");}
        }
        System.exit(0);
    }
    @Override
    public void run(){
        refresh();
    }
}
