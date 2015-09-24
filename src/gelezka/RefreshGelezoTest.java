package gelezka;
/**
 * Created by Coder on 21-Sep-15.
 */

import org.junit.*;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RefreshGelezoTest {


    private WebDriver driver;
    private StringBuffer verificationErrors = new StringBuffer();
    private String userPassword, userName, baseUrl;
    private List<String> listOfLinks;
    private WebElement temp;

    //  private final Wait<WebDriver> wait = new WebDriverWait(driver, 5).withMessage("Element was not found");

    // @FindBy(id="password-shown")







    @Before
    public void setUp() throws Exception {
        driver = new ChromeDriver();
     //   driver = new FirefoxDriver();
        baseUrl = "http://gelezo.com.ua/";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        ReadXML data = new ReadXML();
        userName=data.getUserName();
        userPassword=data.getUserPassword();
        listOfLinks=data.getListOfLinks();
        System.out.println("Site will be used with user: "+userName);
    }



    @Test
    public void refresh(){
        login();
        while(true) {

        refreshAllPosts(listOfLinks);

           try {
               Thread.sleep(7500 * 1000);
           } catch (InterruptedException e) {
           }
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

        if (driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td[1]/a[1]/b")).getText().matches(userName)) {System.out.println("Site will be used with user: "+userName);}
        else  System.out.println("Can't login with "+userName+" user");
        //String s = (char)27 + "[36mbla-bla-bla"
    }

    private void refreshAllPosts(List<String> listOfLinks){

        for (int i = 0; i < listOfLinks.size(); i++) {
            driver.get(listOfLinks.get(i));
            temp = driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[2]/td[1]/table/tbody/tr/td[2]/a"));
            System.out.println("Refreshing posts on "+temp.getText()+" board");
            refreshPosts();
        }
    }




    private void refreshPosts(){

              List<WebElement> uname = driver.findElements(By.linkText("Обновить"));

        for (int i = 0; i != uname.size(); i++) {
            List<WebElement> names = driver.findElements(By.linkText("Обновить"));
            names.get(i).click();
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
            }
            driver.navigate().back();

        }
        System.out.println(uname.size()+"posts successfully refreshed");
    }


    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

}
