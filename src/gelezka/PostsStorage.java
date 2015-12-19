package gelezka;


import java.time.LocalTime;
import java.util.Iterator;
import java.util.LinkedList;

public class PostsStorage {
    private LinkedList<PostInfo> storage = new LinkedList<PostInfo>();

    PostsStorage(){

    }

    PostsStorage(LinkedList<PostInfo> postsList){
       storage=postsList;
    }

    public LinkedList<PostInfo> getPosts(Identifiers ind, String parameter){
    return searchPosts(ind, parameter);
    }

    public void writeChanges(Identifiers searchIdentifier, String parameter, Identifiers changeIdentifier, String data){
   setPostData(searchIdentifier, parameter,changeIdentifier,data);
    }

    public void addPostToStorage(PostInfo post)
    {
        addPost(post);
    }

    public LinkedList<PostInfo> giveAllPosts(){
        return searchPosts(Identifiers.NULL_PARAMETER,null);
    }

    public int getMaxTime(){
        return maxTime();
    }

    private int maxTime(){
        Iterator<PostInfo> it = storage.iterator();
        String time;

        int[] seconds=new int[storage.size()];

        for (int i = 0; i < storage.size(); i++) {
            time=it.next().getPostTime();
            seconds[i]=LocalTime.now().toSecondOfDay()-LocalTime.parse(time).toSecondOfDay();
            if (seconds[i]<-78600)  seconds[i]=seconds[i]+86400;
            if (seconds[i]>-78600&&seconds[i]<0)  seconds[i]=7800;
          }

        int max=seconds[0];

        for (int i = 1; i < storage.size(); i++) {
            if (max > seconds[i]) {
                max = seconds[i];
            }
        }
        return max;
    }


    private void addPost(PostInfo post){
       storage.add(post);
    }

    private LinkedList<PostInfo> searchPosts(Identifiers ind, String parameter){
        LinkedList<PostInfo> temp=new LinkedList<PostInfo>();

        for (int i = 0; i <storage.size(); i++) {

            switch (ind) {
                case ID:
                    if (storage.get(i).getId() == Integer.parseInt(parameter)) temp.add(storage.get(i));
                    break;

                case POST_TEXT:
                    if (storage.get(i).getPostText().equalsIgnoreCase(parameter)) temp.add(storage.get(i));
                    break;

                case POST_TIME:
                    if (storage.get(i).getPostText().equalsIgnoreCase(parameter)) temp.add(storage.get(i));
                    break;

                case POST_DATE:
                    if (storage.get(i).getPostDate().equalsIgnoreCase(parameter)) temp.add(storage.get(i));
                    break;

                case POST_TYPE:
                    if (storage.get(i).getPostType().equalsIgnoreCase(parameter)) temp.add(storage.get(i));
                    break;

                case POST_VIEWS_COUNT:
                    if (storage.get(i).getPostViewsCount() == Integer.parseInt(parameter)) temp.add(storage.get(i));
                    break;

                case POST_MESSAGES_COUNT:
                    if (storage.get(i).getPostMessagesCount() == Integer.parseInt(parameter))
                        temp.add(storage.get(i));
                    break;

                case OLD_POST_MESSAGES_COUNT:
                    if (storage.get(i).getOldPostMessagesCount() == Integer.parseInt(parameter))
                    temp.add(storage.get(i));
                    break;

                case BOARD_NAME:
                    if (storage.get(i).getBoardName().equalsIgnoreCase(parameter)) temp.add(storage.get(i));
                    break;

                case BOARD_ADDRESS:
                    if (storage.get(i).getBoardAddress().equalsIgnoreCase(parameter)) temp.add(storage.get(i));
                    break;

                case FORUM_LINK:
                    if (storage.get(i).getForumLink().equalsIgnoreCase(parameter)) temp.add(storage.get(i));
                    break;

                case REFRESH_STATUS:
                    if (storage.get(i).getRefreshStatus()) temp.add(storage.get(i));
                    break;

                case NULL_PARAMETER:
                    temp.add(storage.get(i));
                    break;

                case POST_CHARACTER:
                    if (storage.get(i).getPostCharacter()) temp.add(storage.get(i));
                    break;

            }


        }

        return temp;
    }

    private void setPostData(Identifiers searchIdentifier, String parameter, Identifiers changeIdentifier, String data){
        LinkedList<PostInfo> temp = searchPosts(searchIdentifier,parameter);
        Iterator it=temp.iterator();
        
        while (it.hasNext()) {
            
            switch (changeIdentifier) {
                case ID:
                    ((PostInfo)it.next()).setId(Integer.parseInt(data));
                    break;

                case POST_TEXT:
                    ((PostInfo)it.next()).setPostText(data);
                    break;

                case POST_TIME:
                    ((PostInfo)it.next()).setPostTime(data);
                    break;

                case POST_DATE:
                    ((PostInfo)it.next()).setPostDate(data);
                    break;

                case POST_TYPE:
                    ((PostInfo)it.next()).setPostType(data);
                    break;

                case POST_VIEWS_COUNT:
                    ((PostInfo)it.next()).setPostViewsCount(Integer.parseInt(data));
                    break;

                case POST_MESSAGES_COUNT:
                    ((PostInfo)it.next()).setPostMessagesCount(Integer.parseInt(data));
                    break;

                case OLD_POST_MESSAGES_COUNT:
                    ((PostInfo)it.next()).setOldPostMessagesCount(Integer.parseInt(data));
                    break;

                case BOARD_NAME:
                    ((PostInfo)it.next()).setBoardName(data);
                    break;

                case BOARD_ADDRESS:
                    ((PostInfo)it.next()).setBoardAddress(data);
                    break;

                case FORUM_LINK:
                    ((PostInfo)it.next()).setForumLink(data);
                    break;

                case REFRESH_STATUS:
                    ((PostInfo)it.next()).setRefreshStatus(Boolean.valueOf(data));
                    break;

                case POST_CHARACTER:
                    ((PostInfo)it.next()).setPostCharacter(Boolean.valueOf(data));
                    break;

            }
        }
    }


}
