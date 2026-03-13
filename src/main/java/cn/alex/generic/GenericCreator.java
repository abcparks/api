package cn.alex.generic;

/**
 * Created by WCY on 2022/10/21
 */
public class GenericCreator<T> {

    private T t;

    public GenericCreator(T t) {
        this.t = t;
    }

    public Object getGenericObject() throws Exception {
        Object genericObject = t.getClass().newInstance();
        return genericObject;
    }

    public static void main(String[] args) throws Exception {
        GenericCreator<String> genericCreator = new GenericCreator<>("Hello World!");
        Object genericObject = genericCreator.getGenericObject();
        System.out.println(genericObject);
    }
}
