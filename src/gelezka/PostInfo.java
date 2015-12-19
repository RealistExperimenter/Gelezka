package gelezka;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


class PostInfo {
    private String postText, postTime, postDate, postLink, boardName, boardAddress, forumLink, postType, refreshLink;
    private int id, postViewsCount, postMessagesCount, oldPostMessagesCount;
    private boolean refreshStatus, postCharacter;

    private String[] temp;



    PostInfo(WebElement head, WebElement body, WebElement footer, String board){
   try {
       temp = head.getText().split("\\s+");
       postType = temp[1];
       postDate = temp[temp.length - 3]+" " +temp[temp.length - 2];
       postTime = temp[temp.length - 1];

       postText = body.findElement(By.tagName("td")).getText();
       postCharacter = postText.startsWith("[o]");
       forumLink = footer.findElement(By.tagName("a")).getAttribute("href");

       temp = forumLink.split("topic|\\.");
       id = Integer.valueOf(temp[temp.length - 2]);
       postLink = "http://gelezo.com.ua/posts/" + id + "/";
       temp = footer.getText().split("\\:|\\s");
       postViewsCount = Integer.valueOf(temp[7]);
       postMessagesCount = Integer.valueOf(temp[9]);
       oldPostMessagesCount=postMessagesCount;

       boardAddress = board;
       temp =board.split("\\/");
       boardName = temp[temp.length - 2];

   }
   catch (Exception e){System.out.println("Exception in Post constructor \n");e.printStackTrace();}

    }



    public String toString(){

        return "\n id: " + id + "\n postText: "+ postText + "\n postTime: "+ postTime + "\n postLink: " +
                postLink +"\n postType: "+ postType+ "\n postViewsCount: " + postViewsCount + "\n postMessagesCount: " +
                postMessagesCount+ "\n boardName: " + boardName + "\n boardAddress: " + boardAddress
                +"\n forumLink: " + forumLink;
    }

    public int getOldPostMessagesCount() {
        return oldPostMessagesCount;
    }

    public void setOldPostMessagesCount(int oldPostMessagesCount) {
        this.oldPostMessagesCount = oldPostMessagesCount;
    }

    public boolean getPostCharacter() {
        return postCharacter;
    }

    public void setPostCharacter(boolean postCharacter) {
        this.postCharacter = postCharacter;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getRefreshLink() {
        return refreshLink;
    }

    public void setRefreshLink(String refreshLink) {
        this.refreshLink = refreshLink;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getRefreshStatus() {
        return refreshStatus;
    }

    public void setRefreshStatus(boolean refreshStatus) {
        this.refreshStatus = refreshStatus;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardAddress() {
        return boardAddress;
    }

    public void setBoardAddress(String boardAddress) {
        this.boardAddress = boardAddress;
    }

    public String getForumLink() {
        return forumLink;
    }

    public void setForumLink(String forumLink) {
        this.forumLink = forumLink;
    }

    public int getPostViewsCount() {
        return postViewsCount;
    }

    public void setPostViewsCount(int postViewsCount) {
        this.postViewsCount = postViewsCount;
    }

    public int getPostMessagesCount() {
        return postMessagesCount;
    }

    public void setPostMessagesCount(int postMessagesCount) {
        this.postMessagesCount = postMessagesCount;
    }




}
