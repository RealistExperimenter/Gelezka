package gelezka;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.*;



public class Initializer {
    private String userPassword, userName, userKey;
    private List<String> listOfLinks=new LinkedList<String>();
    private WebDriver driver;
    private ProgrammWindow print;
    private PostsStorage postStorage=new PostsStorage();



    public void reinitialize(){
        postStorage=new PostsStorage();
        listOfBoards();
    }

    public PostsStorage getPostStorage() {
        return postStorage;
    }

    public void setPostStorage(PostsStorage postStorage) {
        this.postStorage = postStorage;
    }

    String postsLocator="/html/body/div/center/table[1]/tbody/tr/td[3]/div[";
    
    Initializer(ProgrammWindow print){
        ReadXML data = new ReadXML();
        userName=data.getUserName();
        userPassword=data.getUserPassword();
        userKey=data.getUserKey();
        this.print=print;
        if (userPassword == null||userName==null||userKey==null) {
            print.addSting("");
            print.addSting("~~~~~~~~~~~~~~~~~~~~~~~~~~ОШИБКА~~~~~~~~~~~~~~~~~~~~~~~~~");
          print.addSting("Ошибка в файле настроек");
        }
    }




    public String getUserName(){
        return userName;
    }


    public String getUserPassword(){
        return userPassword;
    }


    public boolean isLicensedUser(){
        Key checkUser=new Key();

       if (userKey.length()!=128||userKey==null) { print.addSting("");
           print.addSting("Проблема с Вашим ключом, Вы будете использовать пробную версию. Чтобы использовать полную версию напишите на email: gelezo.refresh@gmail.com для получения ключа");
            return false;
        }
        return checkUser.decodeKey(userKey).matches(userName);
    }


    public List<String> getListOfLinks(WebDriver driver){  this.driver=driver;
        listOfLinks.addAll(listOfBoards());
        return listOfLinks;
    }


    private Set<String> listOfBoards(){
        Set<String> boards=new LinkedHashSet<String>();
        boolean haveElements=true;
        int count=1, count3=1;
        WebElement temp;


        driver.findElement(By.xpath("/html/body/div/table[1]/tbody/tr/td[2]/table/tbody/tr[1]/td[1]/a[1]/b")).click();
     //   driver.findElements(By.className("titlPost"));
        while (haveElements){
            try {
                temp=driver.findElement(By.xpath("/html/body/div/center/table[1]/tbody/tr/td[3]/a["+count+"]"));
                boards.add(temp.getAttribute("href"));
                postStorage.addPostToStorage(new PostInfo(driver.findElement(By.xpath(postsLocator+String.valueOf(count3)+"]")),driver.findElement(By.xpath(postsLocator+String.valueOf(count3+1)+"]")),driver.findElement(By.xpath(postsLocator+String.valueOf(count3+2)+"]")),temp.getAttribute("href")));

            } catch (Exception e) {haveElements=false;}
            count++;
            count3+=3;
        }

        if (boards.size() == 0) {
            return boards;
        }


        return boards;
        }




}

