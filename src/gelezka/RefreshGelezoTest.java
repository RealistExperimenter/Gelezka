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

import java.util.List;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RefreshGelezoTest {

    private WebDriver driver;
    private String userPassword, userName, baseUrl;
    private List<String> listOfLinks;
    private WebElement temp;


    @Before
    public void setUp() throws Exception {
        baseUrl = "http://gelezo.com.ua/";
        ReadXML data = new ReadXML();
        userName=data.getUserName();
        userPassword=data.getUserPassword();
        listOfLinks=data.getListOfLinks();
    }

    private void createDriver(){
        driver = new ChromeDriver();
        System.out.println();
        System.out.println("Site will be used with the user: "+userName);
        //   driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }


    @Test
    public void refresh(){
        while(true) {
              createDriver();
              login();
              refreshAllPosts(listOfLinks);
              driver.quit();
           try {
               Thread.sleep(7800 * 1000);
           } catch (InterruptedException e) {System.out.println("Error under sleep");}
       }
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
         //String s = (char)27 + "[36mbla-bla-bla"
    }

    private void refreshAllPosts(List<String> listOfLinks){
        System.out.println("Action time: "+ new Date());
        for (int i = 0; i < listOfLinks.size(); i++) {
            driver.get(listOfLinks.get(i));
            temp = driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[2]/td[1]/table/tbody/tr/td[2]/a"));
            System.out.println("");
            System.out.println("   Refreshing posts on \""+temp.getText()+"\" board");
            refreshPosts();
        }
        System.out.println();
    }




    private void refreshPosts(){

        List<WebElement> uname = driver.findElements(By.linkText("Обновить"));
        int errCount=uname.size();

        if (uname.size() == 0) {
            System.out.println("!!!!!!There is no any post on the "+temp.getText()+"\" board. Please add some or delete a board link from the my_settings.xml file.");
            return;
        }

        for (int i = 0; i != uname.size(); i++) {
          //  List<WebElement> names = driver.findElements(By.linkText("Обновить"));
            uname.get(i).click();
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {System.out.println("Error under sleep");}
            try {
                temp = driver.findElement(By.xpath("/html/body/div/table[3]/tbody/tr/td/font/h2"));
            } catch (Exception e) {errCount--;  System.out.println("Some Error with err message element");}
            driver.navigate().back();
        }

        System.out.println("     "+(uname.size()-errCount)+" posts successfully refreshed");
        if (errCount != 0)  System.out.println("     "+errCount+" posts not refreshed. Maybe available refresh time larger than 2 hours and 10 minutes.");
    }


    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

}
