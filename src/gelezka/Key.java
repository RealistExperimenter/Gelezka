package gelezka;


public class Key {
    public String decodeKey(String key){
        return this.decode(key);
    }

    private String decode(String key){
        String name="";
        char[] tempKey=key.toCharArray();
        int adresspointer=10;
        boolean end=false;
        int shift=10;

        for (int i = 0; !end; i++) {
            if (adresspointer > tempKey.length-1) {
                shift++;
                adresspointer=shift;
            }
            if (tempKey[adresspointer] == '?' ) {
                end=true;
                break;
            }
            name+=tempKey[adresspointer];
            adresspointer+=10;
        }
        return name;
    }
}
