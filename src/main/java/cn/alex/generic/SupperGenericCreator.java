package cn.alex.generic;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by WCY on 2022/10/17
 */
public class SupperGenericCreator<T> {

    public Object getGenericObject() throws Exception {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType supperClass = (ParameterizedType) type;
            // 可能有多个泛型类型, 取第一个泛型类型
            Type[] types = supperClass.getActualTypeArguments();
            Class<T> clazz = (Class<T>) types[0];
            return clazz.newInstance();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        SubGenericCreator genericCreator = new SubGenericCreator();
        Object genericObject = genericCreator.getGenericObject();
        System.out.println(genericObject.getClass());
    }
}

// 必须显示执行泛型类型(意义不大)
class SubGenericCreator extends SupperGenericCreator<String> {

}

